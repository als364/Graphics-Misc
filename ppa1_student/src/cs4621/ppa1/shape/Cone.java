package cs4621.ppa1.shape;

import java.util.HashMap;
import java.util.Map;

/**
 * A truncated cone centered at (0,0,0).
 * The cone axis of symmetry is the y-axis.
 * The cone y-extent is from y = -1 to y = 1.
 * The smaller cap is a circle of radius 0.5  above that lies completely in the 
 * 		plane y = 1, and the center is at (0,1,0).
 * The bigger cap is a circle of radius 1 that lies 
 * 		completely in the plane y = -1, and the center is at (0,-1,0).
 *  
 * @author pramook 
 */
public class Cone extends TriangleMesh 
{
	public static final float DEFAULT_SMALL_CAP_RADIUS = 0.5f;	
	
	@Override
	public void buildMesh(float tolerance) 
	{			
		// TODO(P2): Edit this method so that it creates a cone mesh.		
		int cuts = (int)(Math.PI / tolerance);
		double theta = 2 * (Math.PI / cuts);
		double topRadius = 0.5;
		
		// arrays for the points, triangles, and normals
		float[] points = new float[12 * cuts];
		float[] norms = new float[12 * cuts];
		int[] triangles = new int[12 * cuts];
		
		//coords of points along the base cap
		float[] xcord = new float[cuts];
		float[] zcord = new float[cuts];
		
		//coords of points along the top cap
		float[] xcordt = new float[cuts];
		float[] zcordt = new float[cuts];
		
		//get the x and z coords for every point around the bottom cap
		double angle = 0;
		for (int i = 0; i < cuts; i++) {
			xcord[i] = (float) Math.cos(angle);
			zcord[i] = (float) Math.sin(angle);
			angle += theta;
		}
		//get the x and z coords for every point around the top cap
		for (int i = 0; i < cuts; i++) {
			xcordt[i] = (float) (topRadius * Math.cos(angle));
			zcordt[i] = (float) (topRadius * Math.sin(angle));
			angle += theta; 
		}
		
		//and everything from here on is exactly the same as cylinder
		
		//accumulate the points and normals
		for (int i = 0; i < cuts; i++) {
			int mult = 3 * i;
			
			//top cap
			points[mult] = xcordt[i];
			points[mult + 1] = 1;
			points[mult + 2] = zcordt[i];
			
			norms[mult] = 0;
			norms[mult + 1] = 1;
			norms[mult + 2] = 0;
			
			//side along the top
			mult = 3 * cuts + 3 * i;
			points[mult] = xcordt[i];
			points[mult + 1] = 1;
			points[mult + 2] = zcordt[i];
			
			norms[mult] = xcordt[i];
			norms[mult + 1] = 0;
			norms[mult + 2] = zcordt[i];
			
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
		for (int i = 1; i < (cuts - 1); i++) {
			//multiplier keeps us from overwriting other triangles.
			int mult = 3 * i; 
			
			//top cap
			triangles[mult] = 0;
			triangles[mult + 1] = i + 1;
			triangles[mult + 2] = i;
			
			//bottom cap
			mult = 3 * (cuts + i);
			triangles[mult] = 3 * cuts;
			triangles[mult + 1] = 3 * cuts + i;
			triangles[mult + 2] = 3 * cuts + i + 1;
		}
		//accumulate side triangles
		for (int i = 0; i < cuts; i++) {
			//edge on top, point on bottom
			int mult = 3 * (2 * cuts + i);
			triangles[mult] = cuts + i;
			triangles[mult + 1] = cuts + (i + 1)%cuts;
			triangles[mult + 2] = 2 * cuts + i;
			//edge on bottom, point on top
			mult = 3 * (3 * cuts + i);
			triangles[mult] = cuts + (i + 1)%cuts;
			triangles[mult + 1] = 2 * cuts + (i + 1)%cuts;
			triangles[mult + 2] = 2 * cuts + i;
		}
		setMeshData(points, norms, triangles);
	}

	@Override
	public Object getYamlObjectRepresentation()
	{
		Map<Object,Object> result = new HashMap<Object, Object>();
		result.put("type", "Cone");
		return result;
	}	
}
