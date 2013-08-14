package ray.surface;

import ray.IntersectionRecord;
import ray.Ray;
import ray.math.Point3;
import ray.math.Vector3;

public class Cylinder extends Surface {
	
	/** The center of the bottom of the cylinder  x , y ,z components. */
	protected final Point3 center = new Point3();
	public void setCenter(Point3 center) { this.center.set(center); }
	
	/** The radius of the cylinder. */
	protected double radius = 1.0;
	public void setRadius(double radius) { this.radius = radius; }
	
	/** The height of the cylinder. */
	protected double height = 1.0;
	public void setHeight(double height) { this.height = height; }
	
	public Cylinder() { }
	
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
		
		double zMax = center.z + (height/2);
		double zMin = center.z - (height/2);
		
		Vector3 d = rayIn.direction;
		Vector3 p = new Vector3(rayIn.origin);
		
		
		double a = ((d.x * d.x) + (d.y * d.y));
		double b = (2 * (d.x * p.x) + 2*(d.y * p.y));
		double c = ((p.x * p.x) + (p.y * p.y) - (radius * radius));
		
		if (((b*b) - (4*a*c)) < 0) {return false;}
		
		double t0 = (-b - Math.sqrt((b*b) - (4*a*c))) / (2 * a);
		double t1 = (-b + Math.sqrt((b*b) - (4*a*c))) / (2 * a);
		
		
		
		if(zMin > zMax) //zMax must be largest
		{
			double temp = zMin;
			zMin = zMax;
			zMax = temp;
		}
		
		if(t0 > t1) //t1 must always be largest
		{
			double temp = t1;
			t1 = t0;
			t0 = temp;
		}
		
		double z0 = p.z + (t0*d.z);
		double z1 = p.z + (t1*d.z);
		
		if((z0 < zMax && z0 > zMin)&&(z1 < zMax && z1 > zMin)) //intercepting wall
		{
			double tee = t0;
			if (t0 < 0 && t1 >= 0) {tee = t1;}//tee = smallest t value
			if (t0 < 0&& t1 < 0) {return false;}
			
	
			
			Point3 intersect = new Point3(); 
			rayIn.evaluate(intersect, tee); //sets intersect to intersection point
			
			
			Vector3 norm = new Vector3(intersect.x, intersect.y, 0.0);
			norm.normalize();
			
			
			tMat.rightMultiply(intersect);
			tMatTInv.rightMultiply(norm);
			
			System.out.println("hit wall");
			
			outRecord.normal.set(norm);
			outRecord.surface = this;
			outRecord.t = tee;
			outRecord.location.set(intersect);
			
			return true;
		}
		else if(z0 > zMax && z1 <= zMax) //intercepting top cap
		{
			double tee = (zMax - p.z) / d.z;
			Point3 intersect = new Point3();
			rayIn.evaluate(intersect, tee);
			
			if (tee < 0) {return false;}
			
			Vector3 norm = new Vector3(0, 0, 1);
			norm.normalize();
			
			tMat.rightMultiply(intersect);
			tMatTInv.rightMultiply(norm);
			
			System.out.println("hit top");
			
			outRecord.normal.set(norm);
			outRecord.surface = this;
			outRecord.t = tee;
			outRecord.location.set(intersect);
			return true;
		}
		else if(z0 < zMin && z1 >= zMin) //intercepting bottom cap
		{
			double tee = (zMin - p.z) / d.z;
			Point3 intersect = new Point3();
			rayIn.evaluate(intersect, tee);
			
			if (tee < 0) {return false;}
			
			Vector3 norm = new Vector3(0, 0, -1);
			norm.normalize();
			
			tMat.rightMultiply(intersect);
			tMatTInv.rightMultiply(norm);
			
			System.out.println("hit bottom");
			
			outRecord.normal.set(norm);
			outRecord.surface = this;
			outRecord.t = tee;
			outRecord.location.set(intersect);
			return true;
		}
		else return false;
	}

	public void computeBoundingBox() {
		// TODO(B): Compute the bounding box and store the result in
	    // averagePosition, minBound, and maxBound.
	    // Hint: The bounding box may be transformed by a transformation matrix.
		//set minimum and max - edges of diameter
		Point3 min = new Point3(center);
		Point3 max = new Point3(center);
		Vector3 v = new Vector3(radius, radius, radius);
		Vector3 v2 = new Vector3(height/2, height/2, height/2);
		
		min.sub(v);
		max.add(v);
		max.add(v2);
				
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
		return "Cylinder " + center + " " + radius + " " + height + " "+ shader + " end";
	}
}

