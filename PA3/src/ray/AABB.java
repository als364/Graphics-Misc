package ray;

import java.util.Arrays;
import java.util.Comparator;
import ray.math.Point3;
import ray.math.Vector3;
import ray.surface.Surface;
/**
 * Class for Axis-Aligned-Bounding-Box to speed up the intersection look up time.
 *
 * @author ss932
 */
public class AABB {
	boolean isLeaf;
	
	/** The current bounding box for this tree node.
	 *  The bounding box is described by 
	 *  (minPt.x, minPt.y, minPt.z) - (maxBound.x, maxBound.y, maxBound.z). */
	Point3 minBound, maxBound;
	
	/** child[0] is the left child. child[1] is the right child. */
	AABB []child;
	
	/** A shared surfaces array that will be used across every node in the tree. */
	static Surface []surfaces;
	
	/** left and right are indices for []surfaces,
	 * meaning that this tree node contains a set of surfaces 
	 * from surfaces[left] to surfaces[right-1]. */
	int left, right;
	
	/** A comparator class that can sort surfaces by x, y, or z coordinate. */
	static MyComparator cmp = new MyComparator();
	
	
	public AABB(boolean isLeaf, Point3 minBound, Point3 maxBound, AABB leftChild, AABB rightChild, int left, int right) {
		this.isLeaf = isLeaf;
		this.minBound = minBound;
		this.maxBound = maxBound;
		this.child = new AABB[2];
		this.child[0] = leftChild;
		this.child[1] = rightChild;
		this.left = left;
		this.right = right;
	}
	
	public AABB() {	}
	
	/**
	 * Set the shared static surfaces that every node in the tree will use by 
	 * using left and right indices.
	 */
	public static void setSurfaces(Surface []surfaces) {
		AABB.surfaces = surfaces;
	}
	
	/**
	 * Create an AABB [sub]tree.  This tree node will be responsible for storing
	 * and processing surfaces[left] to surfaces[right-1].
	 * @param left The left index of []surfaces
	 * @param left The right index of []surfaces
	 */
	public static AABB createTree(int left, int right) {
		// TODO(B): fill in this function.
		
		// ==== Step 1 ====
		// Find out the BIG bounding box enclosing all the surfaces in the range [left, right)
		// and store them in minB and maxB.
		// Hint: To find the bounding box for each surface, use getMinBound() and getMaxBound() */
		
		//giant-ass bounding box
		Point3 min = new Point3(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY); 
		Point3 max = new Point3(Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY);
		
		
		for(int i = left; i < right; i++) {
			Point3 minb = surfaces[i].getMinBound();
			Point3 maxb = surfaces[i].getMaxBound();
				
				if(minb.x < min.x) {
					min.x = minb.x;
				}
				if(maxb.x > max.x) {
					max.x = maxb.x;
				}
				if(minb.y < min.y) {
					min.y = minb.y;
				}
				if(maxb.y > max.y) {
					max.y = maxb.y;
				}
				if(minb.z < min.z) {
					min.z = minb.z;
				}
				if(maxb.z > max.z) {
					max.z = maxb.z;
				}
		}
		Point3 minB = new Point3(min); 
		Point3 maxB = new Point3(max);
		// ==== Step 2 ====
		// Check for the base case. 
		// If the range [left, right) is small enough, just return a new leaf node.
		if(right - left < 4) {return new AABB(true, min, max, null, null, left, right);}

		// ==== Step 3 ====
		// Figure out the widest dimension (x or y or z).
		// If x is the widest, set widestDim = 0. If y, set widestDim = 1. If z, set widestDim = 2.
			int xyz = 0;
			double widestDim = Double.NEGATIVE_INFINITY;
				if((max.x - min.x) > widestDim) {
					widestDim = (max.x - min.x);
					xyz = 0;
				}
				if((max.y - min.y) > widestDim) {
					widestDim = (max.y - min.y);
					xyz = 1;
				}
				if((max.z - min.z) > widestDim) {
					widestDim = (max.y - min.y);
					xyz = 2;
				}
			

			// ==== Step 4 ====
			// Sort surfaces according to the widest dimension.
			// You can also implement O(n) randomized splitting algorithm.
			cmp.setIndex(xyz);
			Arrays.sort(surfaces, left, right, cmp);

			// ==== Step 5 ====
			// Recursively create left and right children.
			Point3 min2 = minB;
			Point3 max2 = maxB;
			Point3 half = maxB;
			half.scale(0.5);
			
			//half of the giant box (from min to min+.5*max)
			Point3 max01 = new Point3(min2.x, min2.y, min2.z);
			Point3 min01 = new Point3(half.x + min2.x, half.y + half.y, half.z + half.z);
			
			//second half of the giant box (from min+.5*max to max)
			max2 = new Point3(max2.x + min2.x, max2.y + min2.y, max2.z + min2.z);
			min2 = new Point3(max2.x + min2.x, max2.y + min2.y, max2.z + min2.z);
			
			AABB leftChild = new AABB(false, min01, max01, null, null, left, right/2);
			leftChild.createTree(left, right/2);
			AABB rightChild = new AABB(false, min2, max2, null, null, left + right/2 + 1, right);
			rightChild.createTree(left + right/2 + 1, right);

			
			return new AABB(false, min, max, leftChild, rightChild, left, right);
	}
	
	/**
	 * Set outRecord to the first intersection of ray with the scene. Return true
	 * if there was an intersection and false otherwise. If no intersection was
	 * found outRecord is unchanged.
	 *
	 * @param outRecord the output IntersectionRecord
	 * @param ray the ray to intersect
	 * @param anyIntersection if true, will immediately return when found an intersection
	 * @return true if and intersection is found.
	 */
	public boolean intersect(IntersectionRecord outRecord, Ray rayIn, boolean anyIntersection) {
		// TODO(B): fill in this function.
		// Hint: For a leaf node, use a normal linear search. 
		// Otherwise, search in the left and right children.
		IntersectionRecord record = new IntersectionRecord();
		
		if(isLeaf) {
			boolean hit = false;
			Ray ray = new Ray(rayIn.origin, rayIn.direction);
			ray.start = rayIn.start;
			ray.end = rayIn.end;
			
			for(int i = left; i < right; i++) {
				if(surfaces[i].intersect(record, ray) && (record.t < ray.end) && (record.t > ray.start) ) {
					if(anyIntersection) {return true;}
					hit = true;
					ray.end = record.t;
					if(outRecord != null) {
						outRecord.set(record);
					}
				}
			}
			return hit;
		}
		else {
			if (child[0].intersect(outRecord, rayIn, anyIntersection)||
			child[1].intersect(outRecord, rayIn, anyIntersection)) {return true;}
			else {return false;}
		}
	}
	
	/** 
	 * Check if the ray intersects this bounding box.
	 * @param ray
	 * @return true if ray intersects this bounding box
	 */
	private boolean isIntersect(Ray ray) {
		// TODO(B): fill in this function.
		// Hint: reuse your code from box intersection.
		
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
        
        Point3 minPt = minBound;
        Point3 maxPt = maxBound;
       
        
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
    
    		if (xmin > min) {min = xmin;}
    		if (xmax < max) {max = xmax;}
        	if (ymin > min) {min = ymin;}
        	if (ymax < max) {max = ymax;}
        	if (zmin > min) {min = zmin;}
        	if (zmax < max) {max = zmax;}
        	
        	
        	if (min > xmax || xmin > max) {return false;}
        	if (min > ymax || ymin > max) {return false;}
        	if (min > zmax || zmin > max) {return false;}
        	else {return true;}
	}
}

class MyComparator implements Comparator<Surface> {
	int index;
	public MyComparator() {	}
	
	public void setIndex(int index) {
		this.index = index;
	}
	
	public int compare(Surface o1, Surface o2) {
		double v1 = o1.getAveragePosition().getE(index);
		double v2 = o2.getAveragePosition().getE(index);
		if(v1 < v2) return 1;
		if(v1 > v2) return -1;
		return 0;
	}
	
}
