package xmlwise;
/**
 * @author Christoffer Lerno
 * @version $Revision$ $Date$   $Author$
 */

import junit.framework.TestCase;

public class XmlElementAttributesTest extends TestCase
{
	XmlElementAttributes m_xmlElementAttributes;

	public void testXmlElementAttributes() throws Exception
	{
		XmlElementAttributes attributes = new XmlElementAttributes(Xml.createDocument("<x ab='cd' ef='12'/>").getDocumentElement());
		assertEquals("cd", attributes.get("ab"));
		assertEquals("12", attributes.get("ef"));
	}

	public void testGetInt() throws Exception
	{
		XmlElementAttributes attributes = new XmlElementAttributes(Xml.createDocument("<x ab='cd' ef='12'/>").getDocumentElement());
		assertEquals(12, attributes.getInt("ef"));
	}

	public void testGetIntErrorMissing() throws Exception
	{
		XmlElementAttributes attributes = new XmlElementAttributes(Xml.createDocument("<x ab='cd' ef='12'/>").getDocumentElement());
		try
		{
			attributes.getInt("gh");
			fail();
		}
		catch (XmlParseException e)
		{}
	}

	public void testGetIntErrorMalformed() throws Exception
	{
		XmlElementAttributes attributes = new XmlElementAttributes(Xml.createDocument("<x ab='cd' ef='12'/>").getDocumentElement());
		try
		{
			attributes.getInt("ab");
			fail();
		}
		catch (XmlParseException e)
		{}
	}

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
		XmlElementAttributes attributes = new XmlElementAttributes(Xml.createDocument("<x ab='yes' ef='12' gh='true' ij='y'/>").getDocumentElement());
		try
		{
			attributes.getBoolean("ef");
			fail();
		}
		catch (XmlParseException e)
		{}
		try
		{
			attributes.getBoolean("notpresent");
			fail();
		}
		catch (XmlParseException e)
		{}
	}
}