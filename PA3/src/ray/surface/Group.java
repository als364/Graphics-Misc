package ray.surface;

import java.util.ArrayList;
import java.util.Iterator;

import ray.IntersectionRecord;
import ray.Ray;
import ray.math.Matrix4;
import ray.math.Point3;
import ray.math.Vector3;

public class Group extends Surface {

	/** List of objects under this group. */
	ArrayList<Surface> objs = new ArrayList<Surface>();
	
	/** The transformation matrix associated with this group. */
	private Matrix4 transformMat;
	
	/** A shared temporary matrix */
	static Matrix4 tmp = new Matrix4();
	
	public Group() {
		transformMat = new Matrix4();
		transformMat.setIdentity();
	}
	
	/**
	 * Compute tMat, tMatInv, tMatTInv for this group and propagate values to the children under it.
	 * @param cMat The transformation matrix of the parent for this node.
	 * @param cMatInv The inverse of cMat.
	 * @param cMatTInv The inverse of the transpose of cMat.
	 */
	public void setTransformation(Matrix4 cMat, Matrix4 cMatInv, Matrix4 cMatTInv) {
		// TODO(B): Compute tMat, tMatInv, tMatTInv using transformMat.
		// Hint: We apply the transformation from bottom up the tree. 
		// i.e. The child's transformation will be applied to objects before its parent's.
		
		//set tMat - transformation matrix 
		// = parent transformation matrix*this one
        tMat = new Matrix4(transformMat);
        tMat.leftCompose(cMat);
		
		//set tMatInv - the inverse of tMat
        tMatInv = new Matrix4(tMat);
        tMatInv.invert();

		//set tMatTInv - inverse of the transpose of tMat
        tMatTInv = new Matrix4(tMat);
        tMatTInv.transpose();
        tMatTInv.invert();


        //set the transformations for each surface
        for(int i = 0; i < objs.size(); i++) {
        	Surface s = objs.get(i);
            s.setTransformation(tMat, tMatInv, tMatTInv);
        }
                
        computeBoundingBox();
	}
	
	
	public void setTranslate(Vector3 T) { 
		tmp.setTranslate(T);
		transformMat.rightCompose(tmp);
	}
	
	public void setRotate(Point3 R) {
		// TODO(B): add rotation to transformMat
		Vector3 v = new Vector3();
		
		v.set(1, 0, 0);
		tmp.setRotate(R.x, v);
		transformMat.rightCompose(tmp);
		
		v.set(0, 1, 0);
		tmp.setRotate(R.y, v);
		transformMat.rightCompose(tmp);

		v.set(0, 0, 1);
		tmp.setRotate(R.z, v);
		transformMat.rightCompose(tmp);
	}
	
	public void setScale(Vector3 S) { 
		// TODO(B): add scale to transformMat
		tmp.setScale(S);
		transformMat.rightCompose(tmp);
	}
	
	public void addSurface(Surface a) {
		objs.add(a);
	}
	
	public boolean intersect(IntersectionRecord outRecord, Ray ray) { return false; }
	public void computeBoundingBox() {	}

	public void appendRenderableSurfaces (ArrayList<Surface> in) {
		for (Iterator<Surface> iter = objs.iterator(); iter.hasNext();)
			iter.next().appendRenderableSurfaces(in);
	}
}
