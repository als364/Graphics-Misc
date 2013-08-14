package cs4621.ppa1.util;

import java.util.List;

import javax.media.opengl.GL2;
import javax.vecmath.Vector3f;

public class Util
{
	public static Vector3f getVector3ffromYamlObject(Object yamlObject)
	{
		if (!(yamlObject instanceof List))
			throw new RuntimeException("yamlObject not a List");
		List yamlList = (List)yamlObject;
		return new Vector3f(
				Float.valueOf(yamlList.get(0).toString()),
				Float.valueOf(yamlList.get(1).toString()),
				Float.valueOf(yamlList.get(2).toString()));				
	}

	public static void assign4ElementArrayFromYamlObject(float[] output, Object yamlObject)
	{
		if (!(yamlObject instanceof List))
			throw new RuntimeException("yamlObject not a List");
		List yamlList = (List)yamlObject;
		
		output[0] = Float.valueOf(yamlList.get(0).toString());
		output[1] = Float.valueOf(yamlList.get(1).toString());
		output[2] = Float.valueOf(yamlList.get(2).toString());
		output[3] = Float.valueOf(yamlList.get(3).toString());
	}

	/**
	 * Get a vector not parallel to v.
	 */
	public static void nonParallelVector(Vector3f v, Vector3f nonParallel) 
	{
		if (v.x <= v.y && v.x <= v.z) nonParallel.set(1,0,0);
		else if (v.y <= v.x && v.y <= v.z) nonParallel.set(0,1,0);
		else if (v.z <= v.x && v.z <= v.y) nonParallel.set(0,0,1);
	}


	/**
	 * gl-rotates the Y axis to the given vector using
	 * an arbitrary frame
	 */
	public static void glRotateYTo(GL2 gl, Vector3f v) 
	{
		Vector3f ortho1 = new Vector3f();
		Vector3f ortho2 = new Vector3f();
		
		nonParallelVector(v, ortho1);
		ortho2.cross(v, ortho1);
		ortho1.cross(ortho2, v);
		ortho1.normalize();
		ortho2.normalize();
		
		gl.glMultMatrixf(new float[] {
				ortho1.x, ortho1.y, ortho1.z, 0,
				v.x, v.y, v.z, 0,
				ortho2.x, ortho2.y, ortho2.z, 0,
				0,0,0, 1 }, 0);
	}
	
	public static final double BOX_RADIUS = 0.1;
	
	public static void glRenderBox(GL2 gl) 
	{
		double r = BOX_RADIUS;
		gl.glBegin(GL2.GL_QUADS);
		{
			gl.glNormal3d(1, 0, 0);
			gl.glVertex3d(r, r, r);
			gl.glVertex3d(r, -r, r);
			gl.glVertex3d(r, -r, -r);
			gl.glVertex3d(r, r, -r);

			gl.glNormal3d(-1, 0, 0);
			gl.glVertex3d(-r, r, -r);
			gl.glVertex3d(-r, -r, -r);
			gl.glVertex3d(-r, -r, r);
			gl.glVertex3d(-r, r, r);

			gl.glNormal3d(0, 1, 0);
			gl.glVertex3d(r, r, r);
			gl.glVertex3d(r, r, -r);
			gl.glVertex3d(-r, r, -r);
			gl.glVertex3d(-r, r, r);

			gl.glNormal3d(0, -1, 0);
			gl.glVertex3d(-r, -r, r);
			gl.glVertex3d(-r, -r, -r);
			gl.glVertex3d(r, -r, -r);
			gl.glVertex3d(r, -r, r);

			gl.glNormal3d(0, 0, 1);
			gl.glVertex3d(r, r, r);
			gl.glVertex3d(-r, r, r);
			gl.glVertex3d(-r, -r, r);
			gl.glVertex3d(r, -r, r);

			gl.glNormal3d(0, 0, -1);
			gl.glVertex3d(r, -r, -r);
			gl.glVertex3d(-r, -r, -r);
			gl.glVertex3d(-r, r, -r);
			gl.glVertex3d(r, r, -r);

		}
		gl.glEnd();
	}
}
