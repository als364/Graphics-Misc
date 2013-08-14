package cs4621.ppa1.shape;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;

public abstract class TriangleMesh extends Mesh {	
	protected float[] vertices = null;
	protected float[] normals = null;
	protected int[] triangles = null;	
	
	public TriangleMesh()
	{
		super();
	}
	
	public final void render(GL2 gl)
	{
		// TODO(P1): Fill this method so that it draws the triangle mesh.
		gl.glBegin(GL.GL_TRIANGLES);
		for(int i = 0; i < triangles.length; i+=3)
		{
			int i0 = triangles[i];
			int i1 = triangles[i+1];
			int i2 = triangles[i+2];
			gl.glNormal3f(normals[3*i0], normals[3*i0+1], normals[3*i0+2]);
			gl.glVertex3f(vertices[3*i0], vertices[3*i0+1], vertices[3*i0+2]);
			gl.glNormal3f(normals[3*i1], normals[3*i1+1], normals[3*i1+2]);
			gl.glVertex3f(vertices[3*i1], vertices[3*i1+1], vertices[3*i1+2]);
			gl.glNormal3f(normals[3*i2], normals[3*i2+1], normals[3*i2+2]);
			gl.glVertex3f(vertices[3*i2], vertices[3*i2+1], vertices[3*i2+2]);
		}
		gl.glEnd();
	}	
	
	protected void setVertex(int i, float x, float y, float z)
	{
		vertices[3*i]   = x;
		vertices[3*i+1] = y;
		vertices[3*i+2] = z;
	}
	
	protected void setNormal(int i, float x, float y, float z)
	{
		normals[3*i]   = x;
		normals[3*i+1] = y;
		normals[3*i+2] = z;
	}
	
	protected void setTriangle(int i, int i0, int i1, int i2)
	{
		triangles[3*i]   = i0;
		triangles[3*i+1] = i1;
		triangles[3*i+2] = i2;
	}
						
	public final void setMeshData(float[] vertices, float[] normals, int[] triangles)
	{
		if (vertices.length % 3 != 0)
			throw new Error("Vertex array's length is not a multiple of 3.");
		if (normals.length % 3 != 0)
			throw new Error("Normal array's length is not a multiple of 3");
		if (vertices.length != normals.length)
			throw new Error("Vertex and normal array are not equal in size.");
	    if (triangles.length % 3 != 0)
	        throw new Error("Triangle array's length is not a multiple of 3.");	    

	    this.vertices = vertices;
	    this.normals = normals;
	    this.triangles = triangles;
	    
	    /*System.out.print("Vertices: ");
	    for(float vertex : vertices)
	    {
	    	System.out.println(vertex + ", ");
	    }
	    System.out.println("end");*/
	}
}
