package xmlwise;

/**
 * @author Christoffer Lerno
 * @version $Revision$ $Date$   $Author$
 */
public class XmlParseException extends Exception
{

	public XmlParseException(String message, Throwable cause)
	{
		super(message, cause);
	}

	public XmlParseException(String message)
	{
		super(message);
	}

	public XmlParseException(Exception e)
	{
		super(e);
	}
}
