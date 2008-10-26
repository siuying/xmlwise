package xmlwise;
/**
 * @author Christoffer Lerno
 */

import junit.framework.TestCase;

public class XmlElementAttributesTest extends TestCase
{
	XmlElementAttributes m_attributes;

	@Override
	protected void setUp() throws Exception
	{
		m_attributes = new XmlElementAttributes(Xml.createDocument("<x ab='cd' ef='12' y='12.5'/>").getDocumentElement());
	}

	public void testXmlElementAttributes() throws Exception
	{
		assertEquals("cd", m_attributes.get("ab"));
		assertEquals("12", m_attributes.get("ef"));
	}

	public void testGetInt() throws Exception
	{
		assertEquals(12, m_attributes.getInt("ef"));
	}

	public void testGetIntErrorMissing() throws Exception
	{
		try
		{
			m_attributes.getInt("gh");
			fail();
		}
		catch (XmlParseException e)
		{}
	}

	public void testGetIntErrorMalformed() throws Exception
	{
		try
		{
			m_attributes.getInt("ab");
			fail();
		}
		catch (XmlParseException e)
		{}
	}

	public void testGetDouble() throws Exception
	{
		assertEquals(12.5, m_attributes.getDouble("y"));
	}

	public void testGetDoubleErrorMissing() throws Exception
	{
		try
		{
			m_attributes.getDouble("gh");
			fail();
		}
		catch (XmlParseException e)
		{}
	}

	public void testGetDoubleErrorMalformed() throws Exception
	{
		try
		{
			m_attributes.getDouble("ab");
			fail();
		}
		catch (XmlParseException e)
		{}
	}

	@SuppressWarnings({"MismatchedQueryAndUpdateOfCollection"})
	public void testGetBoolean() throws Exception
	{
		XmlElementAttributes attributes = new XmlElementAttributes(Xml.createDocument("<x ab='yes' ef='12' gh='true' ij='y' kl='no' m='n' o='false'/>").getDocumentElement());
		assertTrue(attributes.getBoolean("ab"));
		assertTrue(attributes.getBoolean("gh"));
		assertTrue(attributes.getBoolean("ij"));
		assertFalse(attributes.getBoolean("kl"));
		assertFalse(attributes.getBoolean("m"));
		assertFalse(attributes.getBoolean("o"));

	}

	public void testGetBooleanError() throws Exception
	{
		try
		{
			m_attributes.getBoolean("ab");
			fail();
		}
		catch (XmlParseException e)
		{}
		try
		{
			m_attributes.getBoolean("notpresent");
			fail();
		}
		catch (XmlParseException e)
		{}
	}
}