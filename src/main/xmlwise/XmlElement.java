package xmlwise;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;

import java.util.LinkedList;

/**
 * A simplified XML Element that only has an attribute map, a list of sub elements a text value and
 * a name.
 *
 * @author Christoffer Lerno
 * @version $Revision$ $Date$   $Author$
 */
@SuppressWarnings({"serial"})
public class XmlElement extends LinkedList<XmlElement>
{
	private final XmlElementAttributes m_attributes;
	private final String m_value;
	private final String m_name;

	/**
	 * Creates a new XmlElement given an Element object.
	 *
	 * @param element the element to construct this object from.
	 */
	public XmlElement(Element element)
	{
		m_attributes = new XmlElementAttributes(element);
		NodeList children = element.getChildNodes();
		m_name = element.getNodeName();
		StringBuilder textValue = new StringBuilder();
		for (int i = 0; i < children.getLength(); i++)
		{
			Node node = children.item(i);
			if (node.getNodeType() == Node.ELEMENT_NODE)
			{
				add(new XmlElement((Element) node));
			}
			if (node.getNodeType() == Node.TEXT_NODE)
			{
				textValue.append(node.getNodeValue());
			}
		}
		m_value = textValue.toString();
	}

	/**
	 * Get the single direct sub-element with the given name.
	 *
	 * @param name the name of the sub-element.
	 * @return the sub element.
	 * @throws XmlParseException if there are more than one of the sub element, or if no such element was found.
	 */
	public XmlElement getUnique(final String name) throws XmlParseException
	{
		LinkedList<XmlElement> matches = get(name);
		if (matches.size() != 1)
		{
			throw new XmlParseException("Unexpected number of elements of type " + name + " in element <" + getName() + ">");
		}
		return matches.getFirst();
	}

	/**
	 * Get an integer attribute for this element.
	 *
	 * @param attribute the name of the attribute.
	 * @return the integer value of the attribute.
	 * @throws XmlParseException if we fail to parse this attribute as an int, or the attribute is missing.
	 */
	public int getIntAttribute(String attribute) throws XmlParseException
	{
		return getAttributes().getInt(attribute);
	}

	/**
	 * Get an integer attribute for this element, defaulting to a default value if the attribute is missing.
	 *
	 * @param attribute the name of the attribute.
	 * @param defaultValue the default value for the attribute, returned if the attribute is missing.
	 * @return the integer value of the attribute or the default value if the attribute is missing.
	 * @throws XmlParseException if we fail to parse this attribute as an int.
	 */
	public int getIntAttribute(String attribute, int defaultValue) throws XmlParseException
	{
		return containsAttribute(attribute) ? getIntAttribute(attribute) : defaultValue;
	}

	/**
	 * Get a (string) attribute for this element, defaulting to a default value if the attribute is missing.
	 *
	 * @param attribute the name of the attribute.
	 * @param defaultValue the default value for the attribute, returned if the attribute is missing.
	 * @return the value of the attribute or the default value if the attribute is missing.
	 */
	public String getAttribute(String attribute, String defaultValue)
	{
		String value = getAttribute(attribute);
		return value == null ? defaultValue : value;
	}

	/**
	 * Returns the (string) value of an attribute.
	 *
	 * @param attribute the attribute name.
	 * @return the value of the attribute.
	 */
	public String getAttribute(String attribute)
	{
		return getAttributes().get(attribute);
	}

	/**
	 * Get an boolean attribute for this element.
	 * <p>
	 * "true", "yes" and "y" are all interpreted as true. (Case-independent)
	 * <p>
	 * "false", "no" and "no" are all interpreted at false. (Case-independent)
	 *
	 * @param attribute the name of the attribute.
	 * @return the boolean value of the attribute.
	 * @throws XmlParseException if the attribute value does match true or false as defined, or the attribute is missing.
	 */
	public boolean getBoolAttribute(String attribute) throws XmlParseException
	{
		return getAttributes().getBoolean(attribute);
	}

	/**
	 * Get an boolean attribute for this element, defaulting to the default value if missing.
	 * <p>
	 * "true", "yes" and "y" are all interpreted as true. (Case-independent)
	 * <p>
	 * "false", "no" and "no" are all interpreted at false. (Case-independent)
	 *
	 * @param attribute the name of the attribute.
	 * @param defaultValue the default value of the attribute.
	 * @return the boolean value of the attribute or the default value if the attribute is missing.
	 * @throws XmlParseException if the attribute value does match true or false as defined
	 */
	public boolean getBoolAttribute(String attribute, boolean defaultValue) throws XmlParseException
	{
		return containsAttribute(attribute) ? getBoolAttribute(attribute) : defaultValue;
	}

	/**
	 * Get all elements matching the given key.
	 *
	 * @param name the key to match ignoring case.
	 * @return a linked list of matching xml elements
	 */
	public LinkedList<XmlElement> get(String name)
	{
		LinkedList<XmlElement> list = new LinkedList<XmlElement>();
		for (XmlElement element : this)
		{
			if (element.getName().equals(name))
			{
				list.add(element);
			}
		}
		return list;
	}

	/**
	 * Determines if a direct sub-element exists.
	 *
	 * @param key the name of the sub-element.
	 * @return true if the element exists, false otherwise.
	 */
	public boolean contains(String key)
	{
		for (XmlElement element : this)
		{
			if (element.getName().equals(key))
			{
				return true;
			}
		}
		return false;
	}
	/**
	 * Renders this as XML. Does not do proper XML-escaping.
	 *
	 * @return an xml string based on this element and its sub-elements.
	 */
	public String toXml()
	{
		StringBuilder builder = new StringBuilder("<").append(m_name);
		if (m_attributes.size() > 0)
		{
			builder.append(m_attributes.toXml());
		}
		if (isEmpty() && m_value.length() == 0)
		{
			builder.append("/>");
		}
		else
		{
			builder.append('>');
			builder.append(m_value);
			for (XmlElement element : this)
			{
				builder.append(element.toXml());
			}
			builder.append("</").append(m_name).append('>');
		}
		return builder.toString();
	}

	public String getValue()
	{
		return m_value;
	}

	/**
	 * @return a map with the attributes for this element.
	 */
	public XmlElementAttributes getAttributes()
	{
		return m_attributes;
	}

	/**
	 * @return the name of this attribute.
	 */
	public String getName()
	{
		return m_name;
	}

	/**
	 * Determines if an attribute exists.
	 * @param attribute the attribute to check.
	 * @return true if the attribute exists on this element, false otherwise.
	 */
	public boolean containsAttribute(String attribute)
	{
		return getAttributes().containsKey(attribute);
	}
}
