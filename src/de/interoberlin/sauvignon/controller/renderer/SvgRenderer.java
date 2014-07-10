package de.interoberlin.sauvignon.controller.renderer;

import java.util.List;

import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.RectF;
import de.interoberlin.sauvignon.model.svg.SVG;
import de.interoberlin.sauvignon.model.svg.elements.AGeometric;
import de.interoberlin.sauvignon.model.svg.elements.BoundingRect;
import de.interoberlin.sauvignon.model.svg.elements.EElement;
import de.interoberlin.sauvignon.model.svg.elements.circle.SVGCircle;
import de.interoberlin.sauvignon.model.svg.elements.ellipse.SVGEllipse;
import de.interoberlin.sauvignon.model.svg.elements.line.SVGLine;
import de.interoberlin.sauvignon.model.svg.elements.path.SVGPath;
import de.interoberlin.sauvignon.model.svg.elements.path.SVGPathSegment;
import de.interoberlin.sauvignon.model.svg.elements.rect.SVGRect;
import de.interoberlin.sauvignon.model.util.Matrix;
import de.interoberlin.sauvignon.model.util.Vector2;

public class SvgRenderer
{
	/**
	 * Renders svg object to canvas
	 * 
	 * @param canvas
	 * @param svg
	 * @return
	 */
	public static Canvas renderToCanvas(Canvas canvas, SVG svg)
	{
		for (AGeometric element : svg.getAllSubElements())
		{
			if (element != null && canvas != null)
			{
				switch (element.getType())
				{
					case RECT:
					{
						renderRect((SVGRect) element, canvas);
						break;
					}
					case CIRCLE:
					{
						renderCircle((SVGCircle) element, canvas);
						break;
					}
					case ELLIPSE:
					{
						renderEllipse((SVGEllipse) element, canvas);
						break;
					}
					case LINE:
					{
						renderLine((SVGLine) element, canvas);
						break;
					}
					case PATH:
					{
						renderPath((SVGPath) element, canvas);
						break;
					}
					default:
					{
						break;
					}
				}
			}
		}
		return canvas;
	}

	/**
	 * Renders a raster to canvas
	 * 
	 * @param canvas
	 * @param svg
	 * @return
	 */
	public static Canvas renderRasterToCanvas(Canvas canvas, SVG svg)
	{
		Paint mainLine = new Paint();
		mainLine.setARGB(150, 150, 150, 150);
		mainLine.setStyle(Style.STROKE);
		mainLine.setStrokeWidth(4);

		Paint subLine = new Paint();
		subLine.setARGB(150, 150, 150, 150);
		subLine.setStyle(Style.STROKE);
		subLine.setStrokeWidth(2);
		subLine.setPathEffect(new DashPathEffect(new float[]
		{ 10, 10 }, 0));

		// Calculate distances between raster lines
		float smallest = svg.getHeight() < svg.getWidth() ? svg.getHeight() : svg.getWidth();

		int digits = (int) (Math.log10(smallest) + 1);
		float distance = (float) (Math.pow(10, (digits - 1)) / 2);

		// Draw vertical lines
		for (int i = 0; i < svg.getWidth() / distance; i++)
		{
			SVGLine l = new SVGLine();
			Vector2 one = new Vector2(i * distance, 0f);
			Vector2 two = new Vector2(i * distance, svg.getHeight());

			// one = one.applyCTM(svg.getCTM());
			// two = two.applyCTM(svg.getCTM());

			l.setX1(one.getX());
			l.setY1(one.getY());
			l.setX2(two.getX());
			l.setY2(two.getY());

			if (i % 2 == 1)
			{
				l.getStyle().setStroke(subLine);
				l.getStyle().setStrokeWidth(2);
			} else
			{
				l.getStyle().setStroke(mainLine);
				l.getStyle().setStrokeWidth(4);
			}

			l.setMySVG(svg);
			renderLine(l, canvas);
		}

		// Draw horizontal lines
		for (int i = 0; i < svg.getHeight() / distance; i++)
		{
			SVGLine l = new SVGLine();
			Vector2 one = new Vector2(0f, i * distance);
			Vector2 two = new Vector2(svg.getHeight(), i * distance);

			l.setX1(one.getX());
			l.setY1(one.getY());
			l.setX2(two.getX());
			l.setY2(two.getY());

			if (i % 2 == 1)
			{
				l.getStyle().setStroke(subLine);
				l.getStyle().setStrokeWidth(2);
			} else
			{
				l.getStyle().setStroke(mainLine);
				l.getStyle().setStrokeWidth(4);
			}

			l.setMySVG(svg);
			renderLine(l, canvas);
		}

		return canvas;
	}

	/**
	 * Renders bounding rects information of svg to canvas
	 * 
	 * @param canvas
	 * @param svg
	 * @return
	 */
	public static Canvas renderBoundingRectsToCanvas(Canvas canvas, SVG svg)
	{
		List<AGeometric> all = svg.getAllSubElements();

		Paint boundingRectColor = new Paint();
		boundingRectColor.setARGB(150, 255, 0, 255);
		boundingRectColor.setStyle(Style.STROKE);
		boundingRectColor.setStrokeWidth(5);

		for (AGeometric element : all)
		{
			if (element.getType() != EElement.G)
			{

				// Render bounding rect
				BoundingRect br = element.getBoundingRect();
				Matrix ctm = element.getCTM();
				ctm = element.getMySVG().getCTM().multiply(ctm);
				br = br.applyMatrix(ctm);

				Path p = new Path();
				p.moveTo(br.getUpperLeft().getX(), br.getUpperLeft().getY());
				p.lineTo(br.getUpperRight().getX(), br.getUpperRight().getY());
				p.lineTo(br.getLowerRight().getX(), br.getLowerRight().getY());
				p.lineTo(br.getLowerLeft().getX(), br.getLowerLeft().getY());
				p.close();

				canvas.drawPath(p, boundingRectColor);
			}
		}

		return canvas;
	}

	public static Canvas renderBoundingRectsToCanvas2(Canvas canvas, SVG svg)
	{
		List<AGeometric> all = svg.getAllSubElements();

		Paint boundingRectColor = new Paint();
		boundingRectColor.setARGB(150, 255, 0, 255);
		boundingRectColor.setStyle(Style.STROKE);
		boundingRectColor.setStrokeWidth(5);

		for (AGeometric element : all)
		{
			if (element.getType() != EElement.G)
			{

				// Render bounding rect
				BoundingRect br = element.getBoundingRect();
				Matrix ctm = element.getCTM();
				ctm = element.getMySVG().getCTM().multiply(ctm);

				br.setUpperLeft(br.getUpperLeft().applyCTM(ctm));
				br.setUpperRight(br.getUpperRight().applyCTM(ctm));
				br.setLowerLeft(br.getLowerLeft().applyCTM(ctm));
				br.setLowerRight(br.getLowerRight().applyCTM(ctm));

				Path p = new Path();
				p.moveTo(br.getUpperLeft().getX(), br.getUpperLeft().getY());
				p.lineTo(br.getUpperRight().getX(), br.getUpperRight().getY());
				p.lineTo(br.getLowerRight().getX(), br.getLowerRight().getY());
				p.lineTo(br.getLowerLeft().getX(), br.getLowerLeft().getY());
				p.close();

				canvas.drawPath(p, boundingRectColor);
			}
		}

		return canvas;
	}

	private static void renderRect(SVGRect r, Canvas canvas)
	{
		Paint fill = r.getStyle().getFill();
		fill.setStyle(Style.FILL);

		Paint stroke = r.getStyle().getStroke();
		stroke.setStrokeWidth(r.getStyle().getStrokeWidth());
		stroke.setStyle(Style.STROKE);

		// all transformations excluding transformations of the SVG itself
		Matrix ctm = r.getCTM();
		// SVG transformations, e.g. stretch to canvas
		ctm = r.getMySVG().getCTM().multiply(ctm);
		// apply to copied object, which is discarded after rendering
		r = r.applyCTM(ctm);

		float x = r.getX();
		float y = r.getY();
		float width = r.getWidth();
		float height = r.getHeight();

		Vector2 ul = new Vector2(x, y);
		Vector2 ur = new Vector2(x + width, y);
		Vector2 ll = new Vector2(x, y + height);
		Vector2 lr = new Vector2(x + width, y + height);

		Path p = new Path();
		p.moveTo(ul.getX(), ul.getY());
		p.lineTo(ur.getX(), ur.getY());
		p.lineTo(lr.getX(), lr.getY());
		p.lineTo(ll.getX(), ll.getY());
		p.close();

		canvas.drawPath(p, fill);
		canvas.drawPath(p, stroke);
	}

	private static void renderCircle(SVGCircle c, Canvas canvas)
	{
		Paint fill = c.getStyle().getFill();
		fill.setStyle(Style.FILL);

		Paint stroke = c.getStyle().getStroke();
		stroke.setStrokeWidth(c.getStyle().getStrokeWidth());
		stroke.setStyle(Style.STROKE);

		Matrix ctm = c.getCTM();
		ctm = c.getMySVG().getCTM().multiply(ctm);
		c = c.applyCTM(ctm);

		float cx = c.getCx();
		float cy = c.getCy();
		float r = c.getRadius();

		canvas.drawCircle(cx, cy, r, fill);
		canvas.drawCircle(cx, cy, r, stroke);
	}

	private static void renderEllipse(SVGEllipse e, Canvas canvas)
	{
		Paint fill = e.getStyle().getFill();
		fill.setStyle(Style.FILL);

		Paint stroke = e.getStyle().getStroke();
		stroke.setStrokeWidth(e.getStyle().getStrokeWidth());
		stroke.setStyle(Style.STROKE);

		Matrix ctm = e.getCTM();
		ctm = e.getMySVG().getCTM().multiply(ctm);
		e = e.applyCTM(ctm);

		float cx = e.getCx();
		float cy = e.getCy();
		float rx = e.getRx();
		float ry = e.getRy();

		canvas.drawOval(new RectF(cx - rx, cy - ry, cx + rx, cy + ry), fill);
		canvas.drawOval(new RectF(cx - rx, cy - ry, cx + rx, cy + ry), stroke);
	}

	private static void renderLine(SVGLine l, Canvas canvas)
	{
		Paint stroke = l.getStyle().getStroke();
		stroke.setStyle(Style.STROKE);
		stroke.setStrokeWidth(l.getStyle().getStrokeWidth());

		Matrix ctm = l.getCTM();
		ctm = l.getMySVG().getCTM().multiply(ctm);
		l = l.applyCTM(ctm);

		canvas.drawLine(l.getX1(), l.getY1(), l.getX2(), l.getY2(), stroke);
	}

	private static void renderPath(SVGPath elementPath, Canvas canvas)
	{
		Paint fill = elementPath.getStyle().getFill();
		fill.setStyle(Style.FILL);

		Paint stroke = elementPath.getStyle().getStroke();
		stroke.setStrokeWidth(elementPath.getStyle().getStrokeWidth());
		stroke.setStyle(Style.STROKE);

		Matrix ctm = elementPath.getCTM();
		ctm = elementPath.getMySVG().getCTM().multiply(ctm);
		elementPath = elementPath.applyCTM(ctm);

		Path androidPath = new Path();

		for (SVGPathSegment segment : elementPath.getD())
		{
			switch (segment.getSegmentType())
			{
				case MOVETO:
				{
					Vector2 moveto = segment.getCoordinates().get(1);
					androidPath.moveTo(moveto.getX(), moveto.getY());
					break;
				}
				case LINETO:
				case LINETO_HORIZONTAL:
				case LINETO_VERTICAL:
				{
					Vector2 lineto = segment.getCoordinates().get(1);
					androidPath.lineTo(lineto.getX(), lineto.getY());
					break;
				}
				case CLOSEPATH:
				{
					androidPath.close();
					break;
				}
				case CURVETO_CUBIC:
				{
					Vector2 c1 = segment.getCoordinates().get(1);
					Vector2 c2 = segment.getCoordinates().get(2);
					Vector2 end = segment.getCoordinates().get(3);

					androidPath.cubicTo(c1.getX(), c1.getY(), c2.getX(), c2.getY(), end.getX(), end.getY());
					break;
				}
				case CURVETO_CUBIC_SMOOTH:
				{
					break;
				}
				case CURVETO_QUADRATIC:
				{
					Vector2 c = segment.getCoordinates().get(1);
					Vector2 end = segment.getCoordinates().get(2);

					androidPath.quadTo(c.getX(), c.getY(), end.getX(), end.getY());
					break;
				}
				case CURVETO_QUADRATIC_SMOOTH:
				{
					break;
				}
				case ARC:
				{
					break;
				}
			}

		}

		// Draw path
		canvas.drawPath(androidPath, fill);
		canvas.drawPath(androidPath, stroke);
	}
}