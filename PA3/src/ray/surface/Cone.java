package ray.surface;

import ray.IntersectionRecord;
import ray.Ray;
import ray.math.Point3;
import ray.math.Vector3;

public class Cone extends Surface {
	
	/** The center of the bottom of the cone  x , y ,z components. */
	protected final Point3 center = new Point3();
	public void setCenter(Point3 center) { this.center.set(center); }
	
	/** The bottom radius of the cone. */
	protected double radiusBt = 1.0;
	public void setRadiusBt(double radiusBt) { this.radiusBt = radiusBt; }
	
	/** The top radius of the cone. */
	protected double radiusTop = 1.0;
	public void setRadiusTop(double radiusTop) { this.radiusBt = radiusTop; }
	
	/** The height of the cylinder. */
	protected double height = 1.0;
	public void setHeight(double height) { this.height = height; }
	
	public Cone() { }
	
	/**
	 * Tests this surface for intersection with ray. If an intersection is found
	 * record is filled out with the information about the intersection and the
	 * method returns true. It returns false otherwise and the information in
	 * outRecord is not modified.
	 *
	 * @param outRecord the output IntersectionRecord
	 * @param ray the ray to intersect
	 * @return true if the surface intersects the ray
	 */
	public boolean intersect(IntersectionRecord outRecord, Ray rayIn) {		
		return false;
	}

	public void computeBoundingBox() {		
	}
	
	/**
	 * @see Object#toString()
	 */
	public String toString() {
		return "Cone " + center + " " + radiusBt + " " + radiusTop + " "+ height + " "+ shader + " end";
	}
}