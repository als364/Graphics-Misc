package cs4621.ppa1.manip;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.vecmath.Matrix4f;
import javax.vecmath.Vector2f;
import javax.vecmath.Vector3f;
import javax.vecmath.Vector4f;

import cs4621.ppa1.scene.TransformationNode;
import cs4621.ppa1.util.Util;

public class TranslateManip extends Manip 
{		
	@Override
	public void dragged(Vector2f mousePosition, Vector2f mouseDelta) 
	{
		// TODO(P3): Fill this method so that the translation manipulator works.
		//camera up and right vectors, normalized
		Vector3f up = new Vector3f(camera.getUp());
		Vector3f right = new Vector3f(camera.getRight());
		
		//x and y delta in world space
		float yworld = camera.getHeight() * mouseDelta.y;
		float xworld = camera.getHeight() * mouseDelta.x;
				
		//scale camera directions accordingly
		up.scale(yworld);
		right.scale(xworld);
		
		if (axisMode == PICK_X) {
			System.out.println("x");
			transformationNode.setTranslation(
			up.x + right.x + transformationNode.translation.x, 
			transformationNode.translation.y, 
			transformationNode.translation.z);
		}
		if (axisMode == PICK_Y) {
			System.out.println("y");
			transformationNode.setTranslation(
			transformationNode.translation.x, 
			up.y + right.y + transformationNode.translation.y, 
			transformationNode.translation.z);
		}
		if (axisMode == PICK_Z) {
			System.out.println("z");
			transformationNode.setTranslation(
			transformationNode.translation.x, 
			transformationNode.translation.y, 
			up.z + right.z + transformationNode.translation.z);
		}
		if (axisMode == PICK_CENTER) {
			
	
		//set the translation vector
		transformationNode.setTranslation(
		up.x + right.x + transformationNode.translation.x, 
		up.y + right.y + transformationNode.translation.y, 
		up.z + right.z + transformationNode.translation.z);
			//getting x and y delta in image coods found on
	} 		//http://svn.xp-dev.com/svn/cs4620-proj2/modeler/TranslateManip.java
}
		
		

	private Vector3f xAxis = new Vector3f();
	private Vector3f yAxis = new Vector3f();
	private Vector3f zAxis = new Vector3f();
	
	public void glRender(GL2 gl, double scale, boolean pickingMode) 
	{
		gl.glPushAttrib(GL2.GL_COLOR);

		gl.glPushAttrib(GL2.GL_LIGHTING);
		gl.glDisable(GL2.GL_LIGHTING);

		gl.glPushAttrib(GL2.GL_DEPTH_TEST);
		gl.glDisable(GL2.GL_DEPTH_TEST);

		gl.glPushMatrix();
		
		transformationNode.glTranslateToOriginInWorldSpace(gl);		

		gl.glScaled(scale, scale, scale);

		transformationNode.getParentBasisInWorldSpace(xAxis, yAxis, zAxis);		

		gl.glPushMatrix();
		Util.glRotateYTo(gl,xAxis);
		gl.glColor4d(0.8, 0, 0, 1);
		if (pickingMode)
			gl.glLoadName(PICK_X);
		glRenderArrow(gl);
		gl.glPopMatrix();

		gl.glPushMatrix();
		Util.glRotateYTo(gl,yAxis);
		gl.glColor4d(0, 0.8, 0, 1);
		if (pickingMode)
			gl.glLoadName(PICK_Y);
		glRenderArrow(gl);
		gl.glPopMatrix();

		gl.glPushMatrix();
		Util.glRotateYTo(gl,zAxis);
		gl.glColor4d(0, 0, 0.8, 1);
		if (pickingMode)
			gl.glLoadName(PICK_Z);
		glRenderArrow(gl);
		gl.glPopMatrix();    

		if (pickingMode)
			gl.glLoadName(PICK_CENTER);
		gl.glColor4d(0.8, 0.8, 0, 1);
		Util.glRenderBox(gl);

		gl.glPopMatrix();

		gl.glPopAttrib();
		gl.glPopAttrib();
		gl.glPopAttrib();
	}
	
	private static double arrowDivs = 32;
	private static double arrowTailRadius = 0.05;
	private static double arrowHeadRadius = 0.11;

	public static void glRenderArrow(GL2 gl) {
		glRenderArrow(Y_AXIS, gl);
	}

	public static void glRenderArrow(byte axis, GL2 gl) {

		gl.glPushMatrix();
		switch (axis) {
		case X_AXIS:
			gl.glRotatef(90f, 0, 0, -1);
			break;
		case Z_AXIS:
			gl.glRotatef(90f, 1, 0, 0);
		}
		// tail coney
		double theta = 0;
		gl.glBegin(GL.GL_TRIANGLE_FAN);
		gl.glVertex3d(0d, 0d, 0d);
		for (double i = 0; i <= arrowDivs; ++i) {
			theta = (i / arrowDivs) * Math.PI * 2;
			gl.glVertex3d(Math.cos(theta) * arrowTailRadius, 1.8, Math.sin(theta) * arrowTailRadius);
		}
		gl.glEnd();

		// neck ring
		gl.glBegin(GL2.GL_QUAD_STRIP);
		for (double i = 0; i <= arrowDivs; ++i) {
			theta = (i / arrowDivs) * Math.PI * 2;
			gl.glVertex3d(Math.cos(theta) * arrowTailRadius, 1.8, Math.sin(theta) * arrowTailRadius);
			gl.glVertex3d(Math.cos(theta) * arrowHeadRadius, 1.83, Math.sin(theta) * arrowHeadRadius);
		}
		gl.glEnd();

		// head coney
		gl.glBegin(GL.GL_TRIANGLE_FAN);
		gl.glVertex3d(0, 2, 0);
		for (double i = 0; i <= arrowDivs; ++i) {
			theta = (i / arrowDivs) * Math.PI * 2;
			gl.glVertex3d(Math.cos(theta) * arrowHeadRadius, 1.83, Math.sin(theta) * arrowHeadRadius);
		}
		gl.glEnd();

		gl.glPopMatrix();
	}
}
