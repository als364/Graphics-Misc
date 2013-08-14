package ray.surface;

import ray.IntersectionRecord;
import ray.Ray;
import ray.math.Point3;
import ray.math.Vector3;

public class Box extends Surface {
	
	/* The corner of the box with the smallest x, y, and z components. */
	protected final Point3 minPt = new Point3();
	public void setMinPt(Point3 minPt) { this.minPt.set(minPt); }
	
	/* The corner of the box with the largest x, y, and z components. */
	protected final Point3 maxPt = new Point3();
	public void setMaxPt(Point3 maxPt) { this.maxPt.set(maxPt); }
	
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
		// You will need to implement the three-slab intersection test
		// Hint look up Shirley section 12.3
		Ray ray = untransformRay(rayIn);
		
        double min = ray.start;
        double max = ray.end;
        
        double x = ray.origin.x;
        double y = ray.origin.y;
        double z = ray.origin.z;
        double dx = ray.direction.x;
        double dy = ray.direction.y;
        double dz = ray.direction.z;
     
        
        double xmin;
        double xmax;
        double ymin;
        double ymax;
        double zmin;
        double zmax;
       
        
        if (dx >= 0) {
        	xmin = (minPt.x - x) / dx;
        	xmax = (maxPt.x - x) / dx;
        }
        else {
        	xmin = (maxPt.x - x) / dx;
        	xmax = (minPt.x - x) / dx;
        }
        if (dy >= 0) {
    		ymin = (minPt.y - y) / dy;
    		ymax = (maxPt.y - y) / dy;
        	}
    	else {
    		ymin = (maxPt.y - y) / dy;
    		ymax = (minPt.y - y) / dy;
    	}
    	if (dz >= 0) {
    		zmin = (minPt.z - z) / dz;
    		zmax = (maxPt.z - z) / dz;
    		}
    	else {
    		zmin = (maxPt.z - z) / dz;
    		zmax = (minPt.z - z) / dz;
    	}
    
    		if (xmin > min) {
    			min = xmin;
    		}
    		if (xmax < max) {
    			max = xmax;
    		}
        	if (ymin > min) {
                min = ymin;
        	}
        	if (ymax < max) {
        		max = ymax;
        	}
        	if (zmin > min) {
        		min = zmin;
        	}
        	if (zmax < max) {
        		max = zmax;
        	}
        	
        	
        	if (min > xmax || xmin > max) {return false;}
        	if (min > ymax || ymin > max) {return false;}
        	if (min > zmax || zmin > max) {return false;}
        
        	if (outRecord != null) {
                outRecord.t = min;
                ray.evaluate(outRecord.location, min);
                outRecord.surface = this;
                
                
                if (min == xmin) {
                	outRecord.normal.set(1, 0, 0);
                }
                else if (min == ymin) {
                    outRecord.normal.set(0, 1, 0);
                }
                else {
                    outRecord.normal.set(0, 0, 1);
                }
               if (outRecord.normal.dot(ray.direction) > 0) {
                    outRecord.normal.scale(-1);
               }
        }
        
        return true;
	}

	public void computeBoundingBox() {
		// TODO(B): Compute the bounding box and store the result in
		// averagePosition, minBound, and maxBound.
		
		// Hint: The bounding box is not the same as just minPt and maxPt, because 
		// this object can be transformed by a transformation matrix.
		minBound = new Point3();
		maxBound = new Point3();
		
		Point3[] m = {minPt, maxPt};
		
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
		return "Box " + minPt + " " + maxPt + " " + shader + " end";
	}
}

