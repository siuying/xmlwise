package xmlwise;
/**
 * @author Christoffer Lerno 
 */

import junit.framework.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class XmlwiseTest extends TestCase
{
	public void testCreateErronousDocument() throws Exception
	{
		Xmlwise.createDocument("<c></c>");
		try
		{
			Xmlwise.createDocument("<x>dks</y>");
			fail("Should fail");
		}
		catch (XmlParseException e)
		{
		}
	}

	public void testCreateDocument() throws Exception
	{
		XmlElement e = Xmlwise.createXml("<x>foo</x>");
		assertEquals("<x>foo</x>", e.toXml());
	}

	@SuppressWarnings({"IOResourceOpenedButNotSafelyClosed"})
	public void testFromFile() throws Exception
	{
		File f = File.createTempFile("xmltest", "xml");
		f.deleteOnExit();
		FileOutputStream out = new FileOutputStream(f);
		out.write("<xml><test/></xml>".getBytes());
		out.close();
		XmlElement e = Xmlwise.loadXml(f.getAbsolutePath());
		assertEquals("<xml><test/></xml>", e.toXml());
	}


	@SuppressWarnings({"IOResourceOpenedButNotSafelyClosed"})
	public void testFromFileParseFail() throws Exception
	{
		File f = File.createTempFile("xmltest", "xml");
		f.deleteOnExit();
		FileOutputStream out = new FileOutputStream(f);
		out.write("<xml></mlx>".getBytes());
		out.close();
		try
		{
			Xmlwise.loadXml(f.getAbsolutePath());
			fail();
		}
		catch (XmlParseException e)
		{
		}
	}

	public void testFromFileIOException() throws Exception
	{
		try
		{
			Xmlwise.loadXml("not.a.file.that.exists");
			fail();
		}
		catch (IOException e)
		{
		}
	}

	public void testEscapeXML() throws Exception
	{
		assertEquals("&lt;&gt;&quot;&apos;&amp;", Xmlwise.escapeXML("<>\"'&"));
	}
}