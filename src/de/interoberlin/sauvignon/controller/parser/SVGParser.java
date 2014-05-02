package de.interoberlin.sauvignon.controller.parser;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.graphics.Paint;
import android.util.Xml;
import de.interoberlin.sauvignon.model.svg.SVG;
import de.interoberlin.sauvignon.model.svg.elements.AElement;
import de.interoberlin.sauvignon.model.svg.elements.Circle;
import de.interoberlin.sauvignon.model.svg.elements.SVGGElement;
import de.interoberlin.sauvignon.model.svg.elements.Line;
import de.interoberlin.sauvignon.model.svg.elements.Path;
import de.interoberlin.sauvignon.model.svg.elements.Rect;
import de.interoberlin.sauvignon.model.svg.meta.CC_Work;
import de.interoberlin.sauvignon.model.svg.meta.DC_Type;
import de.interoberlin.sauvignon.model.svg.meta.Defs;
import de.interoberlin.sauvignon.model.svg.meta.Metadata;
import de.interoberlin.sauvignon.model.svg.meta.RDF_RDF;
import de.interoberlin.sauvignon.model.util.Vector2;

public class SVGParser
{
	private static SVGParser	instance;

	private SVGParser()
	{

	}

	public static SVGParser getInstance()
	{
		if (instance == null)
		{
			instance = new SVGParser();
		}

		return instance;
	}

	/**
	 * Parses svg file an returns an SVG element
	 * 
	 * @param in
	 * @return
	 * @throws XmlPullParserException
	 * @throws IOException
	 */
	public SVG parse(InputStream in) throws XmlPullParserException, IOException
	{
		try
		{
			XmlPullParser parser = Xml.newPullParser();
			parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
			parser.setInput(in, null);
			parser.nextTag();
			return readSVG(parser);
		} finally
		{
			in.close();
		}
	}

	/**
	 * Returns a SVG element
	 * 
	 * @param parser
	 * @return
	 * @throws XmlPullParserException
	 * @throws IOException
	 */
	private SVG readSVG(XmlPullParser parser) throws XmlPullParserException, IOException
	{
		String name = "";
		parser.require(XmlPullParser.START_TAG, null, SVG.getName());

		// Initialize attributes and subelements
		String xmlns_dc = "";
		String xmlns_cc = "";
		String xmlns_rdf = "";
		String xmlns_svg = "";
		String xmlns = "";
		String version = "";
		String width = "0";
		String height = "0";
		String id = "";
		Defs defs = null;
		Metadata metadata = null;
		List<AElement> subelements = new ArrayList<AElement>();

		// Read attributes
		xmlns_dc = parser.getAttributeValue(null, "dc");
		xmlns_cc = parser.getAttributeValue(null, "cc");
		xmlns_rdf = parser.getAttributeValue(null, "rdf");
		xmlns_svg = parser.getAttributeValue(null, "svg");
		xmlns = parser.getAttributeValue(null, "xmlns");
		version = parser.getAttributeValue(null, "version");
		width = parser.getAttributeValue(null, "width");
		height = parser.getAttributeValue(null, "height");
		id = parser.getAttributeValue(null, "id");

		// Read subelements
		while (parser.next() != XmlPullParser.END_TAG)
		{
			if (parser.getEventType() != XmlPullParser.START_TAG)
			{
				continue;
			}

			name = parser.getName();

			// Starts by looking for the entry tag
			if (name.equals("defs"))
			{
				defs = (readDefs(parser));
			} else if (name.equals("metadata"))
			{
				metadata = (readMetadata(parser));
			} else if (name.equals("g"))
			{
				subelements.add(readG(parser));
			} else if (name.equals("rect"))
			{
				subelements.add(readRect(parser));
			} else if (name.equals("circle"))
			{
				subelements.add(readCircle(parser));
			} else if (name.equals("line"))
			{
				subelements.add(readLine(parser));
			} /* else if (name.equals("path"))
			{
				subelements.add(readPath(parser));
			} */ else
			{
				skip(parser);
			}
		}

		SVG svg = new SVG();
		svg.setXmlns_dc(xmlns_dc);
		svg.setXmlns_cc(xmlns_cc);
		svg.setXmlns_rdf(xmlns_rdf);
		svg.setXmlns_svg(xmlns_svg);
		svg.setXmlns(xmlns);
		svg.setVersion(version);
		svg.setWidth(Float.parseFloat(width));
		svg.setHeight(Float.parseFloat(height));
		svg.setId(id);
		svg.setDefs(defs);
		svg.setMetadata(metadata);
		svg.setSubelements(subelements);

		return svg;
	}

	/**
	 * Returns a Defs element
	 * 
	 * @param parser
	 * @return
	 * @throws XmlPullParserException
	 * @throws IOException
	 */
	private Defs readDefs(XmlPullParser parser) throws XmlPullParserException, IOException
	{
		String name = null;
		parser.require(XmlPullParser.START_TAG, null, Defs.getName());

		// Initialize attributes and subelements
		String id = "";

		// Read attributes
		id = parser.getAttributeValue(null, "id");

		// Read subelements
		while (parser.next() != XmlPullParser.END_TAG)
		{
			if (parser.getEventType() != XmlPullParser.START_TAG)
			{
				continue;
			}

			name = parser.getName();
		}

		Defs defs = new Defs();
		defs.setId(id);

		return defs;
	}

	/**
	 * Returns a Metadata element
	 * 
	 * @param parser
	 * @return
	 * @throws XmlPullParserException
	 * @throws IOException
	 */
	private Metadata readMetadata(XmlPullParser parser) throws XmlPullParserException, IOException
	{
		String name = null;
		parser.require(XmlPullParser.START_TAG, null, Metadata.getName());

		// Initialize attributes and subelements
		String id = "";
		RDF_RDF rdf_RDF = null;

		// Read attributes
		id = parser.getAttributeValue(null, "id");

		// Read subelements
		while (parser.next() != XmlPullParser.END_TAG)
		{
			if (parser.getEventType() != XmlPullParser.START_TAG)
			{
				continue;
			}

			name = parser.getName();

			// Starts by looking for the entry tag
			if (name.equals("rdf:RDF"))
			{
				rdf_RDF = (readRdf_RDF(parser));
			} else
			{
				skip(parser);
			}
		}

		Metadata metadata = new Metadata();
		metadata.setId(id);
		metadata.setRdf_RDF(rdf_RDF);

		return metadata;
	}

	/**
	 * Returns a RDF_RDF element
	 * 
	 * @param parser
	 * @return
	 * @throws XmlPullParserException
	 * @throws IOException
	 */
	private RDF_RDF readRdf_RDF(XmlPullParser parser) throws XmlPullParserException, IOException
	{
		String name = null;
		parser.require(XmlPullParser.START_TAG, null, RDF_RDF.getName());

		// Initialize attributes and subelements
		CC_Work cc_work = null;

		// Read subelements
		while (parser.next() != XmlPullParser.END_TAG)
		{
			if (parser.getEventType() != XmlPullParser.START_TAG)
			{
				continue;
			}

			name = parser.getName();

			// Starts by looking for the entry tag
			if (name.equals("cc:Work"))
			{
				cc_work = (readCC_Work(parser));
			} else
			{
				skip(parser);
			}
		}

		RDF_RDF rdf_RDF = new RDF_RDF();
		rdf_RDF.setCc_work(cc_work);

		return rdf_RDF;
	}

	/**
	 * Returns a CC_Work element
	 * 
	 * @param parser
	 * @return
	 * @throws XmlPullParserException
	 * @throws IOException
	 */
	private CC_Work readCC_Work(XmlPullParser parser) throws XmlPullParserException, IOException
	{
		String name = null;
		parser.require(XmlPullParser.START_TAG, null, CC_Work.getName());

		// Initialize attributes and subelements
		String rdf_about = "";
		String dc_format = "";
		DC_Type dc_type = null;
		String dc_title = "";

		// Read attributes
		rdf_about = parser.getAttributeValue("rdf", "about");

		// Read subelements
		while (parser.next() != XmlPullParser.END_TAG)
		{
			if (parser.getEventType() != XmlPullParser.START_TAG)
			{
				continue;
			}

			name = parser.getName();

			// Starts by looking for the entry tag
			if (name.equals("dc:format"))
			{
				dc_format = readString(parser, "dc:format");
			} else if (name.equals("dc:type"))
			{
				dc_type = readDC_Type(parser);
			} else if (name.equals("dc:title"))
			{
				dc_title = readString(parser, "dc:title");
			} else
			{
				skip(parser);
			}
		}

		CC_Work cc_work = new CC_Work();
		cc_work.setRdf_about(rdf_about);
		cc_work.setDc_Format(dc_format);
		cc_work.setDc_type(dc_type);
		cc_work.setDc_title(dc_title);

		return cc_work;
	}

	/**
	 * Returns a CC_Work element
	 * 
	 * @param parser
	 * @return
	 * @throws XmlPullParserException
	 * @throws IOException
	 */
	private DC_Type readDC_Type(XmlPullParser parser) throws XmlPullParserException, IOException
	{
		String name = null;
		parser.require(XmlPullParser.START_TAG, null, DC_Type.getName());

		// Initialize attributes and subelements
		String rdf_resource = null;

		// Read attributes
		rdf_resource = parser.getAttributeValue("rdf", "resource");

		// Read subelements
		while (parser.next() != XmlPullParser.END_TAG)
		{
			if (parser.getEventType() != XmlPullParser.START_TAG)
			{
				continue;
			}

			name = parser.getName();
		}

		DC_Type dc_type = new DC_Type();
		dc_type.setRdf_resource(rdf_resource);

		return dc_type;
	}

	/**
	 * Returns a G element
	 * 
	 * @param parser
	 * @return
	 * @throws XmlPullParserException
	 * @throws IOException
	 */
	private SVGGElement readG(XmlPullParser parser) throws XmlPullParserException, IOException
	{
		String name = null;
		parser.require(XmlPullParser.START_TAG, null, SVGGElement.getName());

		// Initialize attributes and subelements
		String transform = "";
		List<AElement> subelements = new ArrayList<AElement>();

		// Read attributes
		transform = parser.getAttributeValue(null, "transform");

		// Read subelements
		while (parser.next() != XmlPullParser.END_TAG)
		{
			if (parser.getEventType() != XmlPullParser.START_TAG)
			{
				continue;
			}

			name = parser.getName();

			// Starts by looking for the entry tag
			if (name.equals("g"))
			{
				subelements.add(readG(parser));
			} else if (name.equals("rect"))
			{
				subelements.add(readRect(parser));
			} else if (name.equals("circle"))
			{
				subelements.add(readCircle(parser));
			} else if (name.equals("line"))
			{
				subelements.add(readLine(parser));
			} /* else if (name.equals("path"))
			{
				subelements.add(readPath(parser));
			} */ else
			{
				skip(parser);
			}
		}

		SVGGElement g = new SVGGElement();
		g.setTransform(transform);
		g.setSubelements(subelements);

		return g;
	}

	/**
	 * Returns a Rect element
	 * 
	 * @param parser
	 * @return
	 * @throws XmlPullParserException
	 * @throws IOException
	 */
	private Rect readRect(XmlPullParser parser) throws XmlPullParserException, IOException
	{
		String name = null;
		parser.require(XmlPullParser.START_TAG, null, Rect.getName());

		// Initialize attributes and subelements
		String x = "";
		String y = "";
		String width = "";
		String height = "";
		String rx = "";
		String ry = "";

		String id = "";
		String fill = "";
		String opacity = "";
		String stroke = "";
		String style = "";

		// Read attributes
		id = parser.getAttributeValue(null, "id");

		x = parser.getAttributeValue(null, "x");
		y = parser.getAttributeValue(null, "y");
		width = parser.getAttributeValue(null, "width");
		height = parser.getAttributeValue(null, "height");
		rx = parser.getAttributeValue(null, "rx");
		ry = parser.getAttributeValue(null, "ry");

		style = parser.getAttributeValue(null, "style");
		fill = parser.getAttributeValue(null, "fill");
		opacity = parser.getAttributeValue(null, "opacity");
		stroke = parser.getAttributeValue(null, "stroke");

		// Read subelements
		while (parser.next() != XmlPullParser.END_TAG)
		{
			if (parser.getEventType() != XmlPullParser.START_TAG)
			{
				continue;
			}

			name = parser.getName();
		}

		Rect rect = new Rect();

		if (id != null)
			rect.setId(id);

		if (x != null)
			rect.setX(Float.parseFloat(x));
		if (y != null)
			rect.setY(Float.parseFloat(y));
		if (width != null)
			rect.setWidth(Float.parseFloat(width));
		if (height != null)
			rect.setHeight(Float.parseFloat(height));
		if (rx != null)
			rect.setRx(Float.parseFloat(rx));
		if (rx != null)
			rect.setRy(Float.parseFloat(ry));

		if (style != null)
		{
			if (style.contains("opacity"))
			{
				opacity = getAttributeFromStyle(style, "opacity");
			}
			if (style.contains("fill"))
			{
				fill = getAttributeFromStyle(style, "fill");
			}
			if (style.contains("stroke"))
			{
				stroke = getAttributeFromStyle(style, "stroke");
			}
		}

		rect.setFill(readPaint(fill, opacity));
		rect.setStroke(readPaint(stroke, opacity));

		return rect;
	}

	/**
	 * Returns a Circle element
	 * 
	 * @param parser
	 * @return
	 * @throws XmlPullParserException
	 * @throws IOException
	 */
	private Circle readCircle(XmlPullParser parser) throws XmlPullParserException, IOException
	{
		String name = null;
		parser.require(XmlPullParser.START_TAG, null, Circle.getName());

		// Initialize attributes and subelements
		String id = "";

		String cx = "";
		String cy = "";
		String r = "";

		String style = "";
		String fill = "";
		String opacity = "";
		String stroke = "";

		// Read attributes
		id = parser.getAttributeValue(null, "id");

		cx = parser.getAttributeValue(null, "cx");
		cy = parser.getAttributeValue(null, "cy");
		r = parser.getAttributeValue(null, "r");

		style = parser.getAttributeValue(null, "style");
		fill = parser.getAttributeValue(null, "fill");
		opacity = parser.getAttributeValue(null, "opacity");
		stroke = parser.getAttributeValue(null, "stroke");

		// Read subelements
		while (parser.next() != XmlPullParser.END_TAG)
		{
			if (parser.getEventType() != XmlPullParser.START_TAG)
			{
				continue;
			}

			name = parser.getName();
		}

		Circle circle = new Circle();
		circle.setId(id);

		circle.setCx(Integer.parseInt(cx));
		circle.setCy(Integer.parseInt(cy));
		circle.setR(Integer.parseInt(r));

		// Evaluate style
		if (style != null)
		{
			if (style.contains("opacity"))
			{
				opacity = getAttributeFromStyle(style, "opacity");
			}

			if (style.contains("fill"))
			{
				fill = getAttributeFromStyle(style, "fill");
			}
			if (style.contains("stroke"))
			{
				stroke = getAttributeFromStyle(style, "stroke");
			}
		}

		circle.setFill(readPaint(fill, opacity));
		circle.setStroke(readPaint(stroke, opacity));

		return circle;
	}

	/**
	 * Returns a Line element
	 * 
	 * @param parser
	 * @return
	 * @throws XmlPullParserException
	 * @throws IOException
	 */
	private Line readLine(XmlPullParser parser) throws XmlPullParserException, IOException
	{
		String name = null;
		parser.require(XmlPullParser.START_TAG, null, Line.getName());

		// Initialize attributes and subelements
		String id = "";

		String x1 = "";
		String y1 = "";
		String x2 = "";
		String y2 = "";

		String style = "";
		String fill = "";
		String opacity = "";
		String stroke = "";
		String strokeWidth = "";

		// Read attributes
		id = parser.getAttributeValue(null, "id");

		x1 = parser.getAttributeValue(null, "x1");
		y1 = parser.getAttributeValue(null, "y1");
		x2 = parser.getAttributeValue(null, "x2");
		y2 = parser.getAttributeValue(null, "y2");

		style = parser.getAttributeValue(null, "style");
		fill = parser.getAttributeValue(null, "fill");
		opacity = parser.getAttributeValue(null, "opacity");
		stroke = parser.getAttributeValue(null, "stroke");
		strokeWidth = parser.getAttributeValue(null, "stroke-width");

		// Read subelements
		while (parser.next() != XmlPullParser.END_TAG)
		{
			if (parser.getEventType() != XmlPullParser.START_TAG)
			{
				continue;
			}

			name = parser.getName();
		}

		Line line = new Line();
		line.setId(id);

		line.setX1(Float.parseFloat(x1));
		line.setY1(Float.parseFloat(y1));
		line.setX2(Float.parseFloat(x2));
		line.setY2(Float.parseFloat(y2));

		// Evaluate style
		if (style != null)
		{
			if (style.contains("opacity"))
			{
				opacity = getAttributeFromStyle(style, "opacity");
			}

			if (style.contains("fill"))
			{
				fill = getAttributeFromStyle(style, "fill");
			}
			if (style.contains("stroke"))
			{
				stroke = getAttributeFromStyle(style, "stroke");
			}
			if (style.contains("stroke-width"))
			{
				strokeWidth = getAttributeFromStyle(style, "stroke-width");
			}
		}

		line.setFill(readPaint(fill, opacity));
		line.setStroke(readPaint(stroke, opacity));
		line.setStrokeWidth(Float.parseFloat(strokeWidth));

		return line;
	}

	/**
	 * Returns a Path element
	 * 
	 * @param parser
	 * @return
	 * @throws XmlPullParserException
	 * @throws IOException
	 */
	private Path readPath(XmlPullParser parser) throws XmlPullParserException, IOException
	{
		String name = null;
		parser.require(XmlPullParser.START_TAG, null, Path.getName());

		// Initialize attributes and subelements
		String id = "";
		String sides = "0";
		String cx = "0";
		String cy = "0";
		String r1 = "0";
		String r2 = "0";
		String arg1 = "0";
		String arg2 = "0";
		String flatsided = "false";
		String randomized = "0";
		String d = "";
		String transformCenterX = "0";
		String transformCenterY = "0";

		String fill;
		String opacity;
		String stroke;
		String style;

		// Read attributes
		id = parser.getAttributeValue(null, "id");
		sides = parser.getAttributeValue(null, "sodipodi:sides");
		cx = parser.getAttributeValue(null, "sodipodi:cx");
		cy = parser.getAttributeValue(null, "sodipodi:cy");
		r1 = parser.getAttributeValue(null, "sodipodi:r1");
		r2 = parser.getAttributeValue(null, "sodipodi:r2");
		arg1 = parser.getAttributeValue(null, "sodipodi:arg1");
		arg2 = parser.getAttributeValue(null, "sodipodi:arg2");
		flatsided = parser.getAttributeValue(null, "inkscape:flatsided");
		randomized = parser.getAttributeValue(null, "inkscape:randomized");
		d = parser.getAttributeValue(null, "d");
		transformCenterX = parser.getAttributeValue(null, "inkscape:transform-center-x");
		transformCenterY = parser.getAttributeValue(null, "inkscape:transform-center-y");

		fill = parser.getAttributeValue(null, "fill");
		opacity = parser.getAttributeValue(null, "opacity");
		stroke = parser.getAttributeValue(null, "stroke");
		style = parser.getAttributeValue(null, "style");

		// Read subelements
		while (parser.next() != XmlPullParser.END_TAG)
		{
			if (parser.getEventType() != XmlPullParser.START_TAG)
			{
				continue;
			}

			name = parser.getName();
		}

		Path path = new Path();
		path.setId(id);
		path.setSides(Integer.parseInt(sides));
		path.setCx(Float.parseFloat(cx));
		path.setCy(Float.parseFloat(cy));
		path.setR1(Float.parseFloat(r1));
		path.setR2(Float.parseFloat(r2));
		path.setArg1(Float.parseFloat(arg1));
		path.setArg2(Float.parseFloat(arg2));
		path.setFlatsided(Boolean.parseBoolean(flatsided));
		path.setRandomized(Integer.parseInt(randomized));
		path.setTransformCenterX(Float.parseFloat(transformCenterX));
		path.setTransformCenterY(Float.parseFloat(transformCenterY));

		path.setD(readD(d));

		if (style != null)
		{
			if (style.contains("opacity"))
			{
				opacity = getAttributeFromStyle(style, "opacity");
			}

			if (style.contains("fill"))
			{
				fill = getAttributeFromStyle(style, "fill");
			}
			if (style.contains("stroke"))
			{
				stroke = getAttributeFromStyle(style, "stroke");
			}
		}

		path.setFill(readPaint(fill, opacity));
		path.setStroke(readPaint(stroke, opacity));

		return path;
	}

	private List<Vector2> readD(String d)
	{
		List<Vector2> dList = new ArrayList<Vector2>();

		if (d != null)
		{

			String[] ds = d.split(" ");

			for (String c : new ArrayList<String>(Arrays.asList(ds)))
			{
				if (c.contains(","))
				{
					float x = Float.parseFloat(c.replaceAll(",.*", "  "));
					float y = Float.parseFloat(c.replaceAll(".*,", "  "));

					dList.add(new Vector2(x, y));
				}
			}

		}

		return dList;
	}

	private String getAttributeFromStyle(String style, String attribute)
	{
		return style.replaceAll(".*" + attribute + ":", "").replaceAll(";.*", "");
	}

	private Paint readPaint(String paint, String opacity)
	{
		// Set defaults
		if (paint == null)
		{
			paint = "#FFFFFF";
		}
		if (opacity == null)
		{
			opacity = "1";
		}
		if (paint.equals("none"))
		{
			paint = "#FFFFFF";
			opacity = "0";
		}

		int colorA = (int) Float.parseFloat(opacity) * 255;
		int colorR = Integer.parseInt(paint.substring(1, 3), 16);
		int colorG = Integer.parseInt(paint.substring(3, 5), 16);
		int colorB = Integer.parseInt(paint.substring(5, 7), 16);

		Paint paintFill = new Paint();
		paintFill.setARGB(colorA, colorR, colorG, colorB);
		return paintFill;
	}

	/**
	 * Returns the title of a card
	 * 
	 * @param parser
	 * @return
	 * @throws IOException
	 * @throws XmlPullParserException
	 */
	private String readString(XmlPullParser parser, String tag) throws IOException, XmlPullParserException
	{
		parser.require(XmlPullParser.START_TAG, null, tag);
		String title = readText(parser);
		parser.require(XmlPullParser.END_TAG, null, tag);
		return title;
	}

	/**
	 * Reads the value of a cell
	 * 
	 * @param parser
	 * @return
	 * @throws IOException
	 * @throws XmlPullParserException
	 */
	private String readText(XmlPullParser parser) throws IOException, XmlPullParserException
	{
		String result = "";
		if (parser.next() == XmlPullParser.TEXT)
		{
			result = parser.getText();
			parser.nextTag();
		}
		return result;
	}

	/**
	 * Skips a tag that does not fit
	 * 
	 * @param parser
	 * @throws XmlPullParserException
	 * @throws IOException
	 */
	private void skip(XmlPullParser parser) throws XmlPullParserException, IOException
	{
		if (parser.getEventType() != XmlPullParser.START_TAG)
		{
			throw new IllegalStateException();
		}
		int depth = 1;
		while (depth != 0)
		{
			switch (parser.next())
			{
				case XmlPullParser.END_TAG:
					depth--;
					break;
				case XmlPullParser.START_TAG:
					depth++;
					break;
			}
		}
	}
}
