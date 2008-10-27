package xmlwise;

/**
 * @author Christoffer Lerno 
 */

import junit.framework.*;

import java.util.List;

public class XmlElementTest extends TestCase
{
	XmlElement m_xmlElement;

	protected void setUp() throws Exception
	{
		m_xmlElement = new XmlElement(Xmlwise.createDocument(
				"<x ab='cd&gt;' ef='12'><dfe x='34.1'/><ag>1</ag><ag/><jo test='yes'>hej&lt;</jo></x>").getDocumentElement());
	}

	public void testCreateXml()
	{
		m_xmlElement = new XmlElement("node", "'text'");
		XmlElement subElement = new XmlElement("subnode");
		m_xmlElement.add(subElement);
		subElement.setAttribute("int", 5);
		m_xmlElement.setAttribute("string", "\"'<&>'\"");
		m_xmlElement.setAttribute("foo", "bar");
		m_xmlElement.removeAttribute("foo");
		assertEquals("<node string='&quot;&apos;&lt;&amp;&gt;&apos;&quot;'>" +
		             "&apos;text&apos;<subnode int='5'/></node>",
		             m_xmlElement.toXml());
	}
	public void testToXml() throws Exception
	{
		String backToXml = m_xmlElement.toXml();
		assertTrue("<x ab='cd&gt;' ef='12'><dfe x='34.1'/><ag>1</ag><ag/><jo test='yes'>hej&lt;</jo></x>".equals(backToXml)
		           || "<x ef='12' ab='cd&gt;'><dfe x='34.1'/><ag>1</ag><ag/><jo test='yes'>hej&lt;</jo></x>".equals(backToXml));
	}

	public void testContent() throws XmlParseException
	{
		assertEquals("hej<", m_xmlElement.getUnique("jo").getValue());
	}
	public void testGetUnique() throws Exception
	{
		assertEquals("<dfe x='34.1'/>", m_xmlElement.getUnique("dfe").toXml());
	}


	public void testGetUniqueFailMore() throws Exception
	{
		try
		{
			m_xmlElement.getUnique("ag");
			fail();
		}
		catch (XmlParseException e)
		{
		}
	}

	public void testGetUniqueNotExist() throws Exception
	{
		try
		{
			m_xmlElement.getUnique("fiej");
			fail();
		}
		catch (XmlParseException e)
		{
		}
	}

	public void testGet() throws Exception
	{
		List<XmlElement> elements = m_xmlElement.get("ag");
		assertEquals("<ag>1</ag>", elements.get(0).toXml());
		assertEquals("<ag/>", elements.get(1).toXml());
		assertEquals(2, elements.size());
	}

	public void testGetNone() throws Exception
	{
		assertEquals(0, m_xmlElement.get("fek").size());
	}

	public void testGetIntAttribute() throws Exception
	{
		assertEquals(12, m_xmlElement.getIntAttribute("ef"));
		assertEquals(12, m_xmlElement.getIntAttribute("ef", 3));
	}

	public void testGetIntAttributeWithDefault() throws Exception
	{
		assertEquals(3, m_xmlElement.getIntAttribute("ok", 3));
	}

	public void testGetIntAttributeError() throws Exception
	{
		try
		{
			m_xmlElement.getIntAttribute("ok");
			fail("Should not work");
		}
		catch (XmlParseException e)
		{
		}
	}

	public void testGetDoubleAttribute() throws Exception
	{
		XmlElement element = m_xmlElement.getUnique("dfe");
		assertEquals(34.1, element.getDoubleAttribute("x"));
		assertEquals(34.1, element.getDoubleAttribute("x", 3));
	}

	public void testGetDoubleAttributeWithDefault() throws Exception
	{
		assertEquals(3.4, m_xmlElement.getDoubleAttribute("ok", 3.4));
	}

	public void testGetDoubleAttributeError() throws Exception
	{
		try
		{
			m_xmlElement.getDoubleAttribute("ok");
			fail("Should not work");
		}
		catch (XmlParseException e)
		{
		}
	}

	public void testGetBoolAttribute() throws Exception
	{
		assertTrue(m_xmlElement.getUnique("jo").getBoolAttribute("test"));
	}

	public void testGetBoolAttributeWithDefault() throws Exception
	{
		assertTrue(m_xmlElement.getBoolAttribute("ok", true));
		assertTrue(m_xmlElement.getUnique("jo").getBoolAttribute("test", false));
	}


	public void testGetBoolAttributeErrorMalformed() throws Exception
	{
		try
		{
			m_xmlElement.getBoolAttribute("ef");
			fail("Should not work");
		}
		catch (XmlParseException e)
		{
		}
	}

	public void testGetBoolAttributeErrorMissing() throws Exception
	{
		try
		{
			m_xmlElement.getBoolAttribute("ok");
			fail("Should not work");
		}
		catch (XmlParseException e)
		{
		}
	}

	public void testGetAttribute() throws Exception
	{
		assertEquals("12", m_xmlElement.getAttribute("ef"));
		assertEquals("cd>", m_xmlElement.getAttribute("ab"));
		assertEquals(null, m_xmlElement.getAttribute("not_present"));
	}

	public void testGetAttributeWithDefault() throws Exception
	{
		assertEquals("12", m_xmlElement.getAttribute("ef", "24"));
		assertEquals("ok", m_xmlElement.getAttribute("not_present", "ok"));
	}

	public void testContains() throws Exception
	{
		assertTrue(m_xmlElement.contains("jo"));
		assertFalse(m_xmlElement.contains("missing_element"));
	}
}