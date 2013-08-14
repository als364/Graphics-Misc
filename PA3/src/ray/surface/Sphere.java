package ray.surface;

import ray.IntersectionRecord;
import ray.Ray;
import ray.math.Point3;
import ray.math.Vector3;

/**
 * Represents a sphere as a center and a radius.
 *
 * @author ags
 */
public class Sphere extends Surface {
	
	/** The center of the sphere. */
	protected final Point3 center = new Point3();
	public void setCenter(Point3 center) { this.center.set(center); }
	
	/** The radius of the sphere. */
	protected double radius = 1.0;
	public void setRadius(double radius) { this.radius = radius; }
	
	public Sphere() { }
	
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
		// TODO(A): fill in this function.
		// Hint: This object can be transformed by a transformation matrix.
		// So the rayIn needs to be processed so that it is in the same coordinate as the object.
		rayIn = untransformRay(rayIn);

		
		double dx = rayIn.direction.x;
		double dy = rayIn.direction.y;
		double dz = rayIn.direction.z;
		
		double cx = center.x;
		double cy = center.y;
		double cz = center.z;
		
		double px = rayIn.origin.x;
		double py = rayIn.origin.y;
		double pz = rayIn.origin.z;
		
		double a = dx*dx + dy*dy + dz*dz;
		double b = 2*dx*(px-cx) +  2*dy*(py-cy) +  2*dz*(pz-cz); 
		double cn = cx*cx + cy*cy + cz*cz + px*px + py*py + pz*pz +
                                -2*(cx*px + cy*py + cz*pz) - radius*radius; 
		
		
		
		if (((b*b) - (4*a*cn)) < 0) {return false;}
		double discrim0 = (-b - Math.sqrt((b*b) - (4*a*cn)));
		double discrim1 = (-b + Math.sqrt((b*b) - (4*a*cn)));
		
		double t0 = (discrim0)/(2*a);
		double t1 = (discrim1)/(2*a);
		if(t0 > t1)
		{
			double temp = t1;
			t1 = t0;
			t0 = temp;
		}
		if(t1 < 0)
		{return false;}
		
		if(t0 < 0)
		{
			Point3 intersect = new Point3();
			rayIn.evaluate(intersect, t1);
			Vector3 c = new Vector3(center);
			Vector3 norm = new Vector3(intersect);
			norm.sub(c);
			norm.normalize();
			
			tMat.rightMultiply(intersect);
			tMatTInv.rightMultiply(norm);
			
			outRecord.normal.set(norm);
			outRecord.surface = this;
			outRecord.t = t1;
			outRecord.location.set(intersect);
			return true;
		}
		else
		{
			Point3 intersect = new Point3();
			rayIn.evaluate(intersect, t0);
			Vector3 c = new Vector3(center);
			Vector3 norm = new Vector3 (intersect);
			norm.sub(c);
			norm.normalize();
			
			tMat.rightMultiply(intersect);
			tMatTInv.rightMultiply(norm);
			
			outRecord.normal.set(norm);
			outRecord.surface = this;
			outRecord.t = t0;
			outRecord.location.set(intersect);
			return true;
		}
	}
	
	public void computeBoundingBox() {
		// TODO(B): Compute the bounding box and store the result in
		// averagePosition, minBound, and maxBound.
		Point3 min = new Point3(center);
		Point3 max = new Point3(center);
		Vector3 v = new Vector3(radius, radius, radius);

		//set minimum and max - edges of diameter
		min.sub(v);
		max.add(v);
		
		minBound = new Point3(Double.POSITIVE_INFINITY,Double.POSITIVE_INFINITY,Double.POSITIVE_INFINITY);
		maxBound = new Point3(minBound);
		maxBound.scale(-1);

		
		Point3[] m = {min, max};
		
		// find transformed min and max
		for(int i = 0; i < 2; i++) {
			for(int j = 0; j < 2; j++) {
				for(int k = 0; k < 2; k++) {
					
					Point3 p = new Point3(m[i].x, m[j].y, m[k].z);
					tMat.rightMultiply(p);
						
						if(p.x < minBound.x) {
							minBound.x = p.x;
						}
						if(p.x > maxBound.x) {
							maxBound.x = p.x;
						}
						if(p.y < minBound.y) {
							minBound.y = p.y;
						}
						if(p.y > maxBound.y) {
							maxBound.y = p.y;
						}
						if(p.z < minBound.z) {
							minBound.z = p.z;
						}
						if(p.z > maxBound.z) {
							maxBound.z = p.z;
						}
					}
				}
			}
		//compute avgposition
		Vector3 avg = new Vector3(maxBound);
		Vector3 minb = new Vector3(minBound);
		
		avg.sub(minb);
		avg.scale(0.5);
		
		averagePosition = new Point3(minBound);
		averagePosition.add(avg);
	}
	
	/**
	 * @see Object#toString()
	 */
	public String toString() {
		return "sphere " + center + " " + radius + " " + shader + " end";
	}

}