package cs4621.ppa1.shape;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.vecmath.Point3f;
import javax.vecmath.Point3i;
import javax.vecmath.Vector3f;

/**
 * A sphere mesh centered at the origin with radius 1.
 * 
 * @author pramook
 */
public class Sphere extends TriangleMesh 
{	
	@Override
	public void buildMesh(float tolerance) 
	{
		// TODO(P2): Edit this method so that it creates a sphere mesh.
		
		// number of horizontal and vertical edges
		int horizEdges = 2 * (int)(Math.PI /tolerance); //one edge for each pt along the sphere
		int vertEdges = (int) (Math.PI / tolerance); //we'll use half the number of horizontal
												 //because phiMax = theta/2

		// divide up the mesh
		float divPhi = (float) (2 * Math.PI / (vertEdges - 1)); //longitudinal cuts
		float divTheta = (float) (Math.PI / (horizEdges - 1)); //latitudinal cuts
		
		//arrays for the points for each mesh triangle, and the norms for each triangle
		float[] points = new float[3 * horizEdges * vertEdges];
		float[] norms = new float[3 * horizEdges * vertEdges];;
		int[] triangles = new int[6 * horizEdges * vertEdges];
		
		
		
		float theta = 0; //starting point for horizontal cuts
		float phi = 0; //starting point for vertical cuts
		
		
		//iterate through the edges and get all the points
		for (int y = 0; y < horizEdges; y++) {
			for (int x = 0; x < vertEdges; x++) {
				int i = 3 * (horizEdges * x + y);
				//polar coords
				//http://en.wikipedia.org/wiki/Spherical_coordinate_system#Coordinate_system_conversions
				//x = r*sin theta * cos phi
				//y = r*sin theta * sin phi
				//z = r*cos theta
				points[i] = ((float) (Math.sin(theta) * Math.cos(phi))); 
				points[i + 1] = ((float) (Math.sin(theta) * Math.sin(phi)));
				points[i + 2] = ((float) Math.cos(theta));
				phi += divPhi;
			}
			theta += divTheta;
		}
		
		//accumulate all the triangles
		for (int y = 0; y < horizEdges; y++) {
			for (int x = 0; x < vertEdges; x++) {
				//cap off x+1 and y+1 so they don't go out of bounds
				int my;
				int mx;
				if (y+1 == horizEdges) { 
					my = 0;
				} else {my = y + 1;}
				if (x+1 == vertEdges) { 
					mx = 0;
				} else {mx = x + 1;}
				
				//makes sure to not overwrite anything
				int i = 6 * (horizEdges * x + y);
				triangles[i] = horizEdges * x + y;
				triangles[i + 1] = horizEdges * mx + y;
				triangles[i + 2] = horizEdges * x + my;
				
				triangles[i + 3] = horizEdges * mx + y;
				triangles[i + 4] = horizEdges * mx + my;
				triangles[i + 5] = horizEdges * x + my;
				
			}
			
			//normals are the same as the points, because it's a canonical sphere, 
			//so the normal's from the center through the point
			norms = points;
		}
		setMeshData(points, norms, triangles);
	}

	@Override
	public Object getYamlObjectRepresentation()
	{
		Map<Object,Object> result = new HashMap<Object, Object>();
		result.put("type", "Sphere");
		return result;
	}
}
