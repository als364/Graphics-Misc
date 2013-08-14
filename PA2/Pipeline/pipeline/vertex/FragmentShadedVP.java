package pipeline.vertex;

import java.util.List;
import java.util.Vector;

import javax.vecmath.Color3f;
import javax.vecmath.Vector2f;
import javax.vecmath.Vector3f;
import javax.vecmath.Vector4f;

import pipeline.Pipeline;
import pipeline.PointLight;
import pipeline.math.Matrix4f;
import pipeline.math.Util;
import pipeline.misc.Vertex;

/**
 * Passes the normals and the colors of each vertex on to be rasterized, and
 * later shaded during the fragment stage of the pipeline. This results in the
 * highest quality images, but results in costly computation.
 * 
 * @author ags
 */
public class FragmentShadedVP extends VertexProcessor {
    protected Matrix4f mvp = new Matrix4f(); // Model View Projection Matrix
    protected Matrix4f mv = new Matrix4f(); // Model View Matrix

    protected List<PointLight> lights;
    protected float ambientIntensity;
    protected Color3f specularColor;
    protected float specularExponent;

    public int compatibilityIndex() {
        return 9;
    }

    /**
     * @see VertexProcessor#updateTransforms(Pipeline)
     */
    public void updateTransforms(Pipeline pipe) {
        // TODO : Update the transformation matrices to ones given by the
        // pipeline.
    	mv.set(pipe.modelviewMatrix);
    	
    	mvp.set(pipe.modelviewMatrix);
    	mvp.leftCompose(pipe.projectionMatrix);
    	mvp.leftCompose(pipe.viewportMatrix);
    }

    /**
     * Vertex Shader, which sends all geometry information into the fragment
     * shader to be processed by Phong Shading.
     */
    public void vertex(Vector3f v, Color3f c, Vector3f n, Vector2f t,
            Vertex output) {
        // TODO
    	//normal vector in eye coords
    	Vector4f normal = new Vector4f(n.x, n.y, n.z, 0);
        mv.rightMultiply(normal);
        normal.normalize();
        
        //vertex in eye coords
        Vector4f vEye = new Vector4f(v.x, v.y, v.z, 1);
        mv.rightMultiply(vEye);

        
        //vertex completely transformed
        Vector4f vertex = new Vector4f(v.x, v.y, v.z, 1);
        mvp.rightMultiply(vertex);
    	
    	Color3f color = new Color3f(c);
    	
    	// clamp
 		color.clamp(0,  1);
    	
    	output.c.set(color);
    	output.n.set(normal.x, normal.y, normal.z);
    	output.v.set(vertex);
    	output.vEye.set(vEye.x, vEye.y, vEye.z);
    }

    /**
     * @see VertexProcessor#triangle(Vector3f[], Color3f[], Vector3f[],
     *      Vector2f[], Vertex[])
     */
    public void triangle(Vector3f[] vs, Color3f[] cs, Vector3f[] ns,
            Vector2f[] ts, Vertex[] outputs) {
        // TODO
    	for(int i=0; i < 3; i++)
    	{
        	vertex(vs[i], cs[i], ns[i], ts[i], outputs[i]);
    	}
    }
}
