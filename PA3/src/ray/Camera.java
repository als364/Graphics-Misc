package ray;

import ray.math.Point3;
import ray.math.Vector3;

/**
 * Represents a simple camera.
 */
public class Camera {
	
	/*
	 * Fields that are read in from the input file to describe the camera.
	 * You'll probably want to store some derived values to make ray generation easy.
	 */
	
	protected final Point3 viewPoint = new Point3();
	public void setViewPoint(Point3 viewPoint) { this.viewPoint.set(viewPoint); }
	
	protected final Vector3 viewDir = new Vector3(0, 0, -1);
	public void setViewDir(Vector3 viewDir) { this.viewDir.set(viewDir); }
	
	protected final Vector3 viewUp = new Vector3(0, 1, 0);
	public void setViewUp(Vector3 viewUp) { this.viewUp.set(viewUp); }
	
	protected final Vector3 projNormal = new Vector3(0, 0, 1);
	public void setProjNormal(Vector3 projNormal) { this.projNormal.set(projNormal); }
	
	protected double viewWidth = 1.0;
	public void setViewWidth(double viewWidth) { this.viewWidth = viewWidth; }
	
	protected double viewHeight = 1.0;
	public void setViewHeight(double viewHeight) { this.viewHeight = viewHeight; }
	
	protected double projDistance = 1.0;
	public void setprojDistance(double projDistance) { this.projDistance = projDistance; }
	
	/*
	 * Derived values that are computed before ray generation.
	 * basisU, basisV, and basisW form an orthonormal basis.
	 * basisW is parallel to projNormal.
	 */
	protected final Vector3 basisU = new Vector3();
	protected final Vector3 basisV = new Vector3();
	protected final Vector3 basisW = new Vector3();
	protected final Vector3 centerDir = new Vector3();
	
	// Has the view been initialized?
	protected boolean initialized = false;
	
	/**
	 * Initialize the derived view variables to prepare for using the camera.
	 */
	public void initView() {
		// TODO(A): fill in this function. 
		// Hint:
		// 	 1. set basisW to be parallel to projectNormal and anti-parallel to viewingDir.
		//   2. set basisU to be parallel to the image's X-axis.
		//   3. set basisV to be parallel to the image's Y-axis.
		basisU.cross(viewUp, projNormal);
		basisU.normalize();
		basisV.cross(projNormal, basisU);
		basisV.normalize();
		basisW.set(projNormal);
		basisW.normalize();

		
		initialized = true;
	}
	
	/**
	 * Set outRay to be a ray from the camera through a point in the image.
	 *
	 * @param outRay The output ray (not normalized)
	 * @param inU The u coord of the image point (range [0,1])
	 * @param inV The v coord of the image point (range [0,1])
	 */
	public void getRay(Ray outRay, double inU, double inV) {
		if (!initialized) initView();
		inU = 2*inU-1;
		inV = 2*inV-1;
		
		// TODO(A): fill in this function.
		// Remap to UV coordinates and set the output ray
		//Point3 outPoint = new Point3(inU, inV, projDistance);
		Vector3 newViewDir = new Vector3(viewDir);
		newViewDir.normalize();
		newViewDir.scale(projDistance);
		newViewDir.scaleAdd(inU * viewWidth / 2, basisU);
		newViewDir.scaleAdd(inV * viewHeight / 2, basisV);

		
		outRay.set(viewPoint, newViewDir);
		outRay.makeOffsetRay();
	}
}