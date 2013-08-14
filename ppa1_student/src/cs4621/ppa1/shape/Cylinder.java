package cs4621.ppa1.shape;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.vecmath.Point3f;
import javax.vecmath.Point3i;
import javax.vecmath.Vector3f;

/**
 * A cylinder whose axis of symmetry is the y-axis.
 * The cylinder has height 2 and radius 1. The bottom
 * capSeg is in the y=-1 plane and the top capSeg is in
 * the y=1 plane.
 * 
 * @author pramook
 */
public class Cylinder extends TriangleMesh 
{	
	@Override
	public void buildMesh(float tolerance) 
	{
		// TODO(P2): Edit this method so that it creates a cylinder mesh.
		int cuts = (int)(Math.PI / tolerance);
		double theta = 2 * (Math.PI / cuts);
		
		// arrays for the points, triangles, and normals
		float[] points = new float[12 * cuts];
		float[] norms = new float[12 * cuts];
		int[] triangles = new int[12 * cuts];
		
		
		
		float[] xcord = new float[cuts];
		float[] zcord = new float[cuts];
		
		//x and z coords for every points around the caps
		double angle = 0;
		for (int i = 0; i < cuts; ++i) {
			xcord[i] = (float) Math.cos(angle);
			zcord[i] = (float) Math.sin(angle);
			angle += theta;
		}
		
		//accumulate the points and normals
				for (int i = 0; i < cuts; i++) {
					int mult = 3 * i;
					
					//top cap
					points[mult] = xcord[i];
					points[mult + 1] = 1;
					points[mult + 2] = zcord[i];
					
					norms[mult] = 0;
					norms[mult + 1] = 1;
					norms[mult + 2] = 0;
					
					//side along the top
					mult = 3 * cuts + 3 * i;
					points[mult] = xcord[i];
					points[mult + 1] = 1;
					points[mult + 2] = zcord[i];
					
					norms[mult] = xcord[i];
					norms[mult + 1] = 0;
					norms[mult + 2] = zcord[i];
					
					//side along the bottom
					mult = 6 * cuts + 3 * i;
					points[mult] = xcord[i];
					points[mult + 1] = -1;
					points[mult + 2] = zcord[i];
					
					norms[mult] = xcord[i];
					norms[mult + 1] = 0;
					norms[mult + 2] = zcord[i];
					
					//bottom cap
					mult = 9 * cuts + 3 * i;
					points[mult] = xcord[i];
					points[mult + 1] = -1;
					points[mult + 2] = zcord[i];
					
					norms[mult] = 0;
					norms[mult + 1] = -1;
					norms[mult + 2] = 0;
				}
		
		//accumulate cap triangles
		for (int i = 1; i < cuts - 1; i++) {
			//multiplier keeps us from overwriting other triangles.
			int mult = 3 * i; 
			
			//top cap
			triangles[mult] = 0;
			triangles[mult + 1] = i;
			triangles[mult + 2] = i + 1;
			
			//bottom cap
			mult = 3 * (cuts + i);
			triangles[mult] = 3 * cuts;
			triangles[mult + 1] = 3 * cuts + i;
			triangles[mult + 2] = 3 * cuts + i + 1;
		}
		//accumulate side triangles
		for (int i = 0; i < cuts; i++) {
			int mi;
			if (i+1 == cuts) { 
				mi = 0;
			} else {mi = i + 1;}
			
			//edge on top, point on bottom
			int mult = 3 * (2 * cuts + i);
			triangles[mult] = cuts + i;
			triangles[mult + 1] = cuts + mi;
			triangles[mult + 2] = 2 * cuts + i;
			
			//edge on bottom, point on top
			mult = 3 * (3 * cuts + i);
			triangles[mult] = cuts + mi;
			triangles[mult + 1] = 2 * cuts + mi;
			triangles[mult + 2] = 2 * cuts + i;
		}
		setMeshData(points, norms, triangles);
		
	}

	@Override
	public Object getYamlObjectRepresentation()
	{
		Map<Object,Object> result = new HashMap<Object, Object>();
		result.put("type", "Cylinder");
		return result;
	}
}
