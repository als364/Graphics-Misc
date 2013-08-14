package pipeline.vertex;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.vecmath.Color3f;
import javax.vecmath.Point3f;
import javax.vecmath.Vector2f;
import javax.vecmath.Vector3f;
import javax.vecmath.Vector4f;

import pipeline.PointLight;
import pipeline.Pipeline;
import pipeline.math.Matrix4f;
import pipeline.misc.Vertex;

import pipeline.math.Util;

/**
 * This triangle processor smoothly interpolates the color across the face of
 * the triangle. This is better than flat shading, but still not as nice as
 * fragment (aka phong) shading.
 * 
 * @author ags
 */
public class SmoothShadedVP extends ShadedVP {
    /**
     * This is the composed modelling, viewing, projection, and viewport matrix.
     */
    protected Matrix4f m = new Matrix4f(); // Model View Projection Matrix
    protected Matrix4f meye = new Matrix4f(); // Model View Matrix

    protected List<PointLight> lights;
    protected float ambientIntensity;
    protected Color3f specularColor;
    protected float specularExponent;

    /**
     * @see VertexProcessor#compatibilityIndex()
     */
    public int compatibilityIndex() {
        return 3;
    }

    /**
     * @see VertexProcessor#updateTransforms(Pipeline)
     */
    public void updateTransforms(Pipeline pipe) {
    	// TODO
    	
    	// Model View Projection Matrix = 
    	// modelviewMatrix*projectionMatrix*viewportMatrix
    	m.set(pipe.modelviewMatrix);
    	m.leftCompose(pipe.projectionMatrix);
    	m.leftCompose(pipe.viewportMatrix);
    	
    	// Model View Matrix
    	meye.set(pipe.modelviewMatrix); 
    }

    /**
     * @see VertexProcessor#updateLightModel(Pipeline)
     */
    public void updateLightModel(Pipeline pipe) {
        // TODO : Update the light model (lights, amibent intensity, etc) to
        // ones given by the pipeline.
    	
    	lights = pipe.lights;
    	ambientIntensity = pipe.ambientIntensity;
    	specularColor = pipe.specularColor;
    	specularExponent = pipe.specularExponent;
    }

    /**
     * Vertex shader which shades each vertex and passes the information to the
     * fragment shader ColorZBufferFP.
     */
    public void vertex(Vector3f v, Color3f c, Vector3f n, Vector2f t,
            Vertex output) {
    	
    	//normal vector in eye coords
    	Vector4f norm = new Vector4f(n.x, n.y, n.z, 0);
        meye.rightMultiply(norm);
        norm.normalize();
        
        
        //vertex in eye coords
        Vector4f veye = new Vector4f(v.x, v.y, v.z, 1);
        meye.rightMultiply(veye);
        
        
        //vertex completely transformed
        Vector4f vertex = new Vector4f(v.x, v.y, v.z, 1);
        m.rightMultiply(vertex);
   
        
    	// color 'accumulator'
        Color3f co = new Color3f(0, 0, 0);
        Color3f ambience = new Color3f(ambientIntensity, ambientIntensity, ambientIntensity);
        
        for(PointLight li : lights) {
        	
        	// light source vector = light location - vertex location
        	Vector4f source = new Vector4f(li.getPosition());
        	source.sub(veye);
        	source.normalize();
  
        	// view vector = through the eye coord vertex = negative of coords
        	Vector4f viewer = new Vector4f(veye);
        	viewer.scale(-1);
        	viewer.normalize();
        	
        	// half vector = light source + view / |light source + view\
        	Vector4f half = new Vector4f(viewer);
        	half.add(source);
        	half.normalize();
        	
        	// diffuse intensity and specular intensity
        	Color3f dintense = li.getIntensity();
        	Color3f sintense = Pipeline.specularColor;
        	
        
        	// diffuse intensity - l*n - light (source) * normal (norm3)
        	float dif = Math.max(0, source.dot(norm));

        	
        	// specular intensity - Ls =  ks I max(0, n · h)^k
        	// k = specularExponent, n = norm3, h = half, 
            float s = (float)Math.max(0, Math.pow((double)(half.dot(norm)), (double) specularExponent));
            
            Color3f col = new Color3f(c.x * dif * dintense.x+s * sintense.x, 
            		c.y * dif * dintense.y + s * sintense.y,
            		c.z * dif * dintense.z + s * sintense.z);
            co.add(col);
            //(c = light reflectance *(h.n) to the power p
        }
        // add ambience
        co.add(ambience);
        // prevent color values from being too high
     	co.clamp(0, 1);
        
        output.v.set(vertex); //set output to the transformed vertex coords
        output.vEye.set(veye.x, veye.y, veye.z); // set eye vector
        output.n.set(norm.x, norm.y, norm.z); // set normal
        output.c.set(co); // set color
        
    }
   
	

    /**
     * @see VertexProcessor#triangle(Vector3f[], Color3f[], Vector3f[],
     *      Vector2f[], Vertex[])
     */
    public void triangle(Vector3f[] vs, Color3f[] cs, Vector3f[] ns,
            Vector2f[] ts, Vertex[] outputs) {
    	
    	for(int i=0; i < 3; i++){
    	vertex(vs[i], cs[i], ns[i], ts[i], outputs[i]);
    	}
    }
}
