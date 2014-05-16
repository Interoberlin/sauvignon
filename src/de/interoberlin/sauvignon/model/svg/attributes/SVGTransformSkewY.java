package de.interoberlin.sauvignon.model.svg.attributes;

import de.interoberlin.sauvignon.model.util.Matrix;

public class SVGTransformSkewY extends ATransformOperator
{
	public final ETransformOperators type = ETransformOperators.SKEWY;
	private Float angle = 0f;
	
	public SVGTransformSkewY() {}
	
	public SVGTransformSkewY(Float angle)
	{
		this.angle = angle;
		updateMatrix = true;
	}
	
	public SVGTransformSkewY(Float[] args)
	{
		if (args.length > 0)
		{
			angle = args[0];
			updateMatrix = true;
		}
	}
	
	public Float getAngle()
	{
		return angle;
	}

	public void setAngle(Float angle)
	{
		this.angle = angle;
		updateMatrix = true;
	}

	public Matrix getResultingMatrix()
	{
		if (this.updateMatrix)
		{
			this.resultingMatrix = new Matrix(1f, (float) Math.tan(angle), 0f, 1f, 0f, 0f);
			this.updateMatrix = false;
		}
		return this.resultingMatrix;
	}
}
