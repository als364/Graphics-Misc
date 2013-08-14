package pipeline.math;

import java.awt.Point;

import javax.swing.text.Position;
import javax.vecmath.Color3f;
import javax.vecmath.Point3f;
import javax.vecmath.Point4f;
import javax.vecmath.Vector2f;
import javax.vecmath.Vector3f;
import javax.vecmath.Vector4f;

import pipeline.misc.Vertex;

public class Util
{	
	public static Vector3f reflect(Vector3f incidentVector, Vector3f normalVector)
	{
		float refScale = incidentVector.dot(normalVector) * -2.0f;
		Vector3f refVec = Util.copy(normalVector);
		refVec.scale(refScale);
		refVec.add(incidentVector);
		return refVec;
	}
	
	public static Color3f comMult(Color3f param0, Color3f param1)
	{
		return new Color3f(param0.x * param1.x, param0.y * param1.y, param0.z * param1.z);
	}
	
	public static Vector4f convert(Vector3f param)
	{
		return new Vector4f(param.x, param.y, param.z, 1.0f);
	}
	
	public static Vector3f convert(Vector4f param)
	{
		return new Vector3f(param.x, param.y, param.z);
	}
	
	public static Vector4f convert(Point3f param)
	{
		return new Vector4f(param.x, param.y, param.z, 1.0f);
	}
	
	public static Vector3f convert(Point4f param)
	{
		return new Vector3f(param.x, param.y, param.z);
	}
	
	public static Vector3f copy(Vector3f param)
	{
		return new Vector3f(param.x, param.y, param.z);
	}
	
	public static Color3f copy(Color3f param)
	{
		return new Color3f(param.x, param.y, param.z);
	}
	
	public static Vector4f copy(Vector4f param)
	{
		return new Vector4f(param.x, param.y, param.z, 1.0f);
	}
	
	public static void copy(Vector3f param0, Vector3f param1)
	{
		param1.set(param0.x, param0.y, param0.z);
	}
	
	public static void copy(Vector4f param0, Vector3f param1)
	{
		param1.set(param0.x, param0.y, param0.z);
	}
	
	public static void copy(Vector4f param0, Vector4f param1)
	{
		param1.set(param0.x, param0.y, param0.z, param0.w);
	}
	
	public static void copy(Vector3f param0, Vector4f param1)
	{
		param1.set(param0.x, param0.y, param0.z, 1.0f);
	}
	
	public static void ftransform(Vertex output, Vector3f gl_Vertex, Matrix4f gl_ModelViewProjectionMatrix)
	{
		Util.copy(gl_Vertex, output.v);
		gl_ModelViewProjectionMatrix.rightMultiply(output.v);
	}
}
