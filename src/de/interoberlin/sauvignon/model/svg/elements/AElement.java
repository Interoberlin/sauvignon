package de.interoberlin.sauvignon.model.svg.elements;

public abstract class AElement
{
	private static String	name;
	private EElement		type;

	private String			id;
	private int				zIndex;

	public BoundingRect getBoundingRect()
	{
		return new BoundingRect();
	}

	public static String getName()
	{
		return name;
	}

	public EElement getType()
	{
		return type;
	}

	public void setType(EElement type)
	{
		this.type = type;
	}

	public String getId()
	{
		return id;
	}

	public void setId(String id)
	{
		this.id = id;
	}

	public int getzIndex()
	{
		return zIndex;
	}

	public void setzIndex(int zIndex)
	{
		this.zIndex = zIndex;
	}

	public void mustUpdateCTM()
	{
		/*
		 * In case, this element is a geometric element (AGeometric),
		 * AGeometric's mustUpdateCTM will be used, otherwise do nothing.
		 */
	}
}
