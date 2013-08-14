package ray.surface;

import ray.IntersectionRecord;
import ray.Ray;
import ray.math.Point2;
import ray.math.Point3;
import ray.math.Vector2;
import ray.math.Vector3;
import ray.shader.Shader;

/**
 * Represents a sphere as a center and a radius.
 *
 * @author ags
 */
public class TriangleMesh extends Surface {
	/** The normal vector of this triangle mesh */
	Vector3 norm;
	
	/** The mesh that contains this triangle mesh */
	Mesh owner;
	
	/** 3 indices to the vertices of this triangle. */
	int []index;
	
	public TriangleMesh(Mesh owner, int index0, int index1, int index2, Shader material) {
		this.owner = owner;
		index = new int[3];
		index[0] = index0;
		index[1] = index1;
		index[2] = index2;
		
		Point3 v0 = owner.getVertex(index0);
		Point3 v1 = owner.getVertex(index1);
		Point3 v2 = owner.getVertex(index2);
		
		if(!owner.existsNormals()) {
			Vector3 e0 = new Vector3(), e1 = new Vector3();
			e0.sub(v1, v0);
			e1.sub(v2, v0);
			norm = new Vector3();
			norm.cross(e0, e1);
		}
				
		this.setShader(material);
	}

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
		// TODO(B): fill in this function.
		// Hint: This object can be transformed by a transformation matrix.
		// So the rayIn needs to be processed so that it is in the same coordinate as the object.
		
		//get intersection using Barycentric coords
		
		
        Ray ray = untransformRay(rayIn);
        //get the three points of the triangle
        Point3 p1 = new Point3(owner.getVertex(index[0]));
        Point3 p2 = new Point3(owner.getVertex(index[1]));
        Point3 p3 = new Point3(owner.getVertex(index[2]));
        

        //length of two sides
        Vector3 one = new Vector3(p1);
        Vector3 two = new Vector3(p2);
        one.sub(two);
        

        Vector3 three = new Vector3(p1);
        Vector3 four = new Vector3(p3);
        three.sub(four);

        
        //direction
        Vector3 r = new Vector3(ray.direction);

        
        //distance between the first point and eye vector
        Vector3 dif = new Vector3(p1);
        Vector3 origin = new Vector3(ray.origin);
        dif.sub(origin);
        
        Vector3 c1 = new Vector3();
        Vector3 c2 = new Vector3();
        
        
        c1.cross(three, r);
        c2.cross(one, dif);

        
        double area = one.dot(c1);
        double t =-1 * ((three.dot(c2)) / area);
        
        double gamma = (r.dot(c2)) / area;
        double beta = (dif.dot(c1)) / area;
        double alpha = 1-beta-gamma;

        
        //find the point along the ray at magnitude t
        //where the ray intersects, if it intersects
        Point3 p = new Point3();
        ray.evaluate(p, t);


        if ((gamma < 0) || (gamma > 1)){return false;}
        if ((beta < 0) || (beta > (1-gamma))){return false;}
        if ((t < ray.start) || (t > ray.end)){return false;}
        
        // set the IntersectionRecord
        outRecord.location.set(p);
        outRecord.surface = this;
        outRecord.t = t;
        
        if (norm != null) {
                outRecord.normal.set(norm);
        }
        else { //interpolate the normals
                Vector3 n0 = owner.getNormal(index[0]);
                Vector3 n1 = owner.getNormal(index[1]);
                Vector3 n2 = owner.getNormal(index[2]);
                
                Vector3 n = new Vector3(0,0,0);
                n.scaleAdd(alpha, n0);
                n.scaleAdd(beta, n1);
                n.scaleAdd(gamma, n2);
                outRecord.normal.set(n);
        }
        
        if (owner.existsTexture()) {
        	//interpolate texture coords
                Point2 a = new Point2();
                Point2 b = new Point2();
                Point2 c = new Point2();
                
                a = owner.getTexcoords(index[0]);
                a.scale(alpha);
                b = owner.getTexcoords(index[1]);
                b.scale(beta);
                c = owner.getTexcoords(index[2]);
                c.scale(gamma);
                
                Point2 cord = new Point2(a.x + b.x + c.x, a.y + b.y + c.y);
                outRecord.texCoords.set(cord);
        }
        
        // transform everything back
        tMat.rightMultiply(outRecord.location);
        tMatTInv.rightMultiply(outRecord.normal);
        outRecord.normal.normalize();
        
        return true;
	}

	public void computeBoundingBox() {
		// TODO(B): Compute the bounding box and store the result in
		// averagePosition, minBound, and maxBound.
		Point3 min = new Point3(1,1,1);
		Point3 max = new Point3(1,1,1);
		
		for(int i = 0; i < 3; ++i) {
				Point3 v = owner.getVertex(index[i]);
				
				if(v.x < min.x) {
					min.x = v.x;
				}
				if(v.x > max.x) {
					max.x = v.x;
				}
				if(v.y < min.y) {
					min.y = v.y;
				}
				if(v.y > max.y) {
					max.y = v.y;
				}
				if(v.z < min.z) {
					min.z = v.z;
				}
				if(v.z > max.z) {
					max.z = v.z;
				}
			
		}

		minBound = new Point3();
		maxBound = new Point3();
		averagePosition = new Point3();
		
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
		return "Triangle ";
	}
}