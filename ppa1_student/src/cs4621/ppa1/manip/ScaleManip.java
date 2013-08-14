package cs4621.ppa1.manip;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.vecmath.Matrix4f;
import javax.vecmath.Vector2f;
import javax.vecmath.Vector3f;
import javax.vecmath.Vector4f;

import cs4621.ppa1.scene.TransformationNode;
import cs4621.ppa1.util.Util;

public class ScaleManip extends Manip {

	Vector3f xManipBasis = new Vector3f();
	Vector3f yManipBasis = new Vector3f();
	Vector3f zManipBasis = new Vector3f();
	Vector3f manipOrigin = new Vector3f();
	
	@Override
	public void dragged(Vector2f mousePosition, Vector2f mouseDelta) 
	{
		// TODO(P3): Fill this method to make the scaling manipulator works.	
		//Convert viewing ray to eyespace
		Vector3f up = new Vector3f(camera.getUp());
		Vector3f right = new Vector3f(camera.getRight());
		
		//x and y delta in world space
		float yworld = camera.getHeight() * mouseDelta.y;
		float xworld = camera.getHeight() * mouseDelta.x;
				
		//scale camera directions accordingly
		up.scale(yworld);
		right.scale(xworld);
		
		if (axisMode == PICK_X) {
			System.out.println("X");
			transformationNode.setScaling(
			up.x + right.y + transformationNode.scaling.x, 
			transformationNode.scaling.y, 
			transformationNode.scaling.z);
		} if (axisMode == PICK_Y) {
			System.out.println("Y");
			transformationNode.setScaling(
			transformationNode.scaling.x, 
			up.x + right.y  + transformationNode.scaling.y, 
			transformationNode.scaling.z);
		} if (axisMode == PICK_Z) {
			System.out.println("Z");
			transformationNode.setScaling(
			transformationNode.scaling.x, 
			transformationNode.scaling.y, 
			up.x + right.y  + transformationNode.scaling.z);
		}
		if (axisMode == PICK_CENTER)
		{	System.out.println("CENTER");
			transformationNode.setScaling(
			up.x + right.y + transformationNode.scaling.x, 
			up.x + right.y  + transformationNode.scaling.y, 
			up.x + right.y  + transformationNode.scaling.z);
		}
	}
		/*float height = camera.getHeight();
		float width = 2 * camera.aspect * camera.getHeight();
		float n = camera.near;
		/* 
		float f = camera.far;
		
		float t = -height/2;
		float b = height/2;
		float l = -width/2;
		float r = width/2;
		float x = mousePosition.x;
		x += 1;
		x /= 2;
		x *= width;
		x -= width/2;
		float y = mousePosition.y;
		y += 1;
		y /= 2;
		y *= height;
		y -= height/2;
		float dx = mouseDelta.x;
		dx += 1;
		dx /= 2;
		dx *= width;
		dx -= width/2;
		float dy = mouseDelta.y;
		dy += 1;
		dy /= 2;
		dy *= height;
		dy -= height/2;
		Vector4f eyespaceMousePosition = new Vector4f(x, y, n, 1);
		Vector4f eyespaceMouseDelta = new Vector4f(dx, dy, n, 1);
		Vector4f mouseOriginVector = new Vector4f(camera.getEye().x, camera.getEye().y, camera.getEye().z, 1);
		mouseOriginVector.sub(eyespaceMousePosition);
		Vector4f mouseEndVector = new Vector4f(camera.getEye().x, camera.getEye().y, camera.getEye().z, 1);
		mouseEndVector.sub(eyespaceMouseDelta);
		
		//Convert viewing rays to worldspace
		Vector4f u = new Vector4f(camera.up.x, camera.up.y, camera.up.z, 0);
		Vector4f v = new Vector4f(camera.right.x, camera.right.y, camera.right.z, 0);
		Vector4f w = new Vector4f(camera.target.x - camera.eye.x, camera.target.y - camera.eye.y, 
				camera.target.z - camera.eye.z, 0);
		Vector4f e = new Vector4f(0, 0, 0, 1);
		Matrix4f canonicalToFrame = new Matrix4f(u.x, v.x, w.x, e.x, u.y, v.y, w.y, e.y, u.z, v.z, w.z, e.z, 0, 0, 0, 1);
		canonicalToFrame.transform(mouseOriginVector);
		canonicalToFrame.transform(mouseEndVector); */
	
		/*//Convert manipulation axis to worldspace
		switch(axisMode)
		{
		case PICK_X:
			break;
		case PICK_Y:
			break;
		case PICK_Z:
			break;
		case PICK_CENTER:
			break;
		}*/

	

	private void initManipBasis()
	{
		// and apply this rotation, since scale happens before rotation
		xManipBasis.set(eX);
		yManipBasis.set(eY);
		zManipBasis.set(eZ);

		transformationNode.rotate(eX, xManipBasis);
		transformationNode.rotate(eY, yManipBasis);
		transformationNode.rotate(eZ, zManipBasis);
		
		xManipBasis.add(transformationNode.translation);
		yManipBasis.add(transformationNode.translation);
		zManipBasis.add(transformationNode.translation);
				
		// get origin
		transformationNode.toWorld(e0, manipOrigin);
		
		if (transformationNode.getParent() != null)
		{
			TransformationNode parent = (TransformationNode)transformationNode.getParent();
			
			parent.toWorld(xManipBasis, xManipBasis);
			parent.toWorld(yManipBasis, yManipBasis);
			parent.toWorld(zManipBasis, zManipBasis);			
		}
		
		xManipBasis.sub(manipOrigin);
		yManipBasis.sub(manipOrigin);
		zManipBasis.sub(manipOrigin);
		
		xManipBasis.normalize();
		yManipBasis.normalize();
		zManipBasis.normalize();		
	}	

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

		initManipBasis();		

		gl.glPushMatrix();
		Util.glRotateYTo(gl,xManipBasis);
		gl.glColor4d(0.8, 0, 0, 1);
		if (pickingMode)
			gl.glLoadName(PICK_X);
		glRenderBoxOnAStick(gl);
		gl.glPopMatrix();

		gl.glPushMatrix();
		Util.glRotateYTo(gl,yManipBasis);
		gl.glColor4d(0, 0.8, 0, 1);
		if (pickingMode)
			gl.glLoadName(PICK_Y);
		glRenderBoxOnAStick(gl);
		gl.glPopMatrix();

		gl.glPushMatrix();
		Util.glRotateYTo(gl,zManipBasis);
		gl.glColor4d(0, 0, 0.8, 1);
		if (pickingMode)
			gl.glLoadName(PICK_Z);
		glRenderBoxOnAStick(gl);
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

	private static void glRenderBoxOnAStick(GL2 gl) {
		glRenderBoxOnAStick(Y_AXIS, gl);
	}

	private static void glRenderBoxOnAStick(byte axis, GL2 gl) {
		gl.glPushMatrix();
		switch (axis) {
		case X_AXIS:
			gl.glRotatef(90f, 0, 0, -1);
			break;
		case Z_AXIS:
			gl.glRotatef(90f, 1, 0, 0);
		}

		gl.glPushAttrib(GL2.GL_CURRENT_BIT);
		gl.glColor4f(1,1,1,1);
		gl.glBegin(GL.GL_LINES);
		gl.glVertex3f(0,0,0);
		gl.glVertex3f(0,2,0);
		gl.glEnd();
		gl.glPopAttrib();

		gl.glTranslatef(0,2,0);
		Util.glRenderBox(gl);

		gl.glPopMatrix();
	}
}
