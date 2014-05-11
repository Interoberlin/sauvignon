package de.interoberlin.sauvignon.model.svg.elements;

import java.util.ArrayList;
import java.util.List;

/*
 * http://www.w3.org/TR/SVG11/paths.html
 */

/**
 * An SVG Path Segment consists of a command char, of which some are followed by
 * one or more numbers
 */
public class SVGPathSegment
{
	private ESVGPathSegmentType				segmentType;
	private ESVGPathSegmentCoordinateType	coordinateType;
	private List<Float>						numbers	= new ArrayList<Float>();

	public ESVGPathSegmentType getSegmentType()
	{
		return segmentType;
	}

	public void setSegmentType(ESVGPathSegmentType segmentType)
	{
		this.segmentType = segmentType;
	}

	public ESVGPathSegmentCoordinateType getCoordinateType()
	{
		return coordinateType;
	}

	public void setCoordinateType(ESVGPathSegmentCoordinateType coordinateType)
	{
		this.coordinateType = coordinateType;
	}

	public List<Float> getNumbers()
	{
		return numbers;
	}

	public void setNumbers(List<Float> numbers)
	{
		this.numbers = numbers;
	}
	
	public void addNumber(float number)
	{
		numbers.add(number);
	}
}
