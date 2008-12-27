package xmlwise;
/**
 * @author Christoffer Lerno 
 */

import junit.framework.*;
import xmlwise.Plist;

import java.util.*;
import java.io.File;
import java.io.Closeable;
import java.io.IOException;

public class PlistTest extends TestCase
{
	Plist m_plist = new Plist();


	public void testParse() throws Exception
	{
		Map<String, Object> result = quickParse(
				"<key>1</key><array>" +
				"<integer>1</integer>" +
				"<real>1.0</real>" +
				"<string>test&amp;</string>" +
				"<date>1998-01-02T11:22:33Z</date>" +
				"<true/>" +
				"<false/></array>");
		assertEquals("{1=[1, 1.0, test&, Fri Jan 02 12:22:33 CET 1998, true, false]}", result.toString());
	}

	public void testToXml() throws Exception
	{
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("a", 3);
		map.put("b", 1.0);
		map.put("c", "foo");
		ArrayList list = new ArrayList();
		list.add("a");
		list.add(3);
		map.put("f", list);
		assertEquals(map, Plist.fromXml(Plist.toXml(map)));
		File temp = File.createTempFile("plist", "foo");
		temp.deleteOnExit();
		Plist.store(map, temp.getAbsolutePath());
		assertEquals(map, Plist.load(temp.getAbsolutePath()));
	}

	public void testLongVsInt() throws Exception
	{
		assertEquals(Long.class, quickParse("<key>a</key><integer>" + Long.MAX_VALUE + "</integer>").get("a").getClass());
		assertEquals(Integer.class, quickParse("<key>a</key><integer>" + Integer.MAX_VALUE + "</integer>").get("a").getClass());
		assertEquals(Long.class, quickParse("<key>a</key><integer>" + (Integer.MAX_VALUE + 1L) + "</integer>").get("a").getClass());
	}

	private Map<String, Object> quickParse(String string) throws XmlParseException
	{
		return m_plist.parse(Xmlwise.createXml("<plist><dict>" + string + "</dict></plist>"));
	}

	public static void testParseNonPlist()
	{
		try
		{
			Plist.fromXml("<dict></dict>");
		}
		catch (XmlParseException e)
		{
			assertEquals("Expected plist top element, was: dict", e.getMessage());
		}
	}

	public void testParseBytes() throws Exception
	{
		byte[] bytes = new byte[]{1, 2, (byte) 255};
		String bytesBase64 = Plist.base64encode(bytes);

		Map<String, Object> result = quickParse("<key>a</key><data>" + bytesBase64 + "</data>");
		assertEquals("[1, 2, -1]", Arrays.toString((byte[]) result.get("a")));
	}

	public void testFailUnknownType()
	{
		try
		{
			quickParse("<key>a</key><foo>bar</foo>");
			fail();
		}
		catch (XmlParseException e)
		{
			assertEquals("Failed to parse: <dict><key>a</key><foo>bar</foo></dict>", e.getMessage());
		}
	}

	public void testFailUnexpectedKey()
	{
		try
		{
			quickParse("<string>bar</string>");
			fail();
		}
		catch (XmlParseException e)
		{
			assertEquals("Failed to parse: <dict><string>bar</string></dict>", e.getMessage());
		}
	}

	public void testFailToParseBytes()
	{
		try
		{
			quickParse("<key>a</key><data>!</data>");
			fail();
		}
		catch (XmlParseException e)
		{
			assertEquals("Failed to parse: <dict><key>a</key><data>!</data></dict>", e.getMessage());
		}
	}

	private String toXml(Object o)
	{
		return m_plist.objectToXml(o).toXml();
	}

	public void testObjectToXml() throws Exception
	{
		assertEquals("<string>&lt;&amp;&gt;</string>", toXml("<&>"));
		assertEquals("<integer>-1</integer>", toXml(-1));
		assertEquals("<real>0.1</real>", toXml(0.1));
		assertEquals("<date>1970-01-01T00:00:00Z</date>", toXml(new Date(0)));
		assertEquals("<true/>", toXml(true));
		assertEquals("<false/>", toXml(false));
		assertEquals("<array><array><string>Foo</string><integer>0</integer></array></array>",
		             toXml(Arrays.asList(Arrays.asList("Foo", 0))));
		TreeMap<String, Object> map = new TreeMap<String, Object>();
		map.put("b", 0.1);
		map.put("a", "foo");
		assertEquals("<dict><key>a</key><string>foo</string><key>b</key><real>0.1</real></dict>", toXml(map));
		assertEquals("<data>Rm9vYmFy</data>", toXml("Foobar".getBytes()));
		toXml((byte) 1);
		toXml((float) 1);
		toXml((short) 1);
		toXml((long) 1);
		try
		{
			toXml(new Object());
			fail();
		}
		catch (Exception e)
		{
			assertEquals("Cannot use class java.lang.Object in plist.", e.getMessage());
		}
	}

	public void testDecode() throws Exception
	{
		assertEquals("Foobar", new String(Plist.base64decode(" " + Plist.base64encode("Foobar".getBytes()))));
		assertEquals("Foobar!", new String(Plist.base64decode(Plist.base64encode("Foobar!".getBytes()))));
		assertEquals("Foobar!!", new String(Plist.base64decode(Plist.base64encode("Foobar!!".getBytes()))));
	}

	public void testEncode() throws Exception
	{
		assertEquals("Rm9vYmFy", Plist.base64encode("Foobar".getBytes()));
		assertEquals("Rm9vYmFyIQ==", Plist.base64encode("Foobar!".getBytes()));
		assertEquals("Rm9vYmFyISE=", Plist.base64encode("Foobar!!".getBytes()));
	}

	public void testSilentlyClose() throws Exception
	{
		Plist.silentlyClose(new Closeable()
		{
			public void close() throws IOException
			{
				throw new IOException();
			}
		});
		Plist.silentlyClose(null);
	}
}