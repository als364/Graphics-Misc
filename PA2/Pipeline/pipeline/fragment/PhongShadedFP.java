package pipeline.fragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.vecmath.Color3f;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;
import javax.vecmath.Vector4f;

import pipeline.Pipeline;
import pipeline.PointLight;
import pipeline.math.Util;
import pipeline.misc.Fragment;
import pipeline.misc.FrameBuffer;

/**
 * This is the fragment program which actually uses a shading model to compute
 * the color on a per fragment basis.
 * 
 * @author ags
 */
public class PhongShadedFP extends FragmentProcessor {

    protected List<PointLight> lights;
    protected float ambientIntensity;
    protected Color3f specularColor;
    protected float specularExponent;

    public int compatibilityIndex() {
        return 9;
    }

    public void fragment(Fragment f, FrameBuffer fb) {
        // TODO : Update the color and Z value of fragment specified by f in the
        // framebuffer.

        // Depth test
    	if(f.depth >= fb.getZ(f.x, f.y)) {
            return;
        }
    	Color3f color = new Color3f(0, 0, 0);
    	Color3f ambience = new Color3f(ambientIntensity, ambientIntensity, ambientIntensity);
    	
    	Vector3f veye = new Vector3f(f.vEye);
    	Vector3f norm = new Vector3f(f.n);
    	norm.normalize();
    	
    	for(PointLight light: lights)
    	{
    		// light source vector = light location - vertex location
        	Vector3f source = new Vector3f(light.getPosition());
        	source.sub(veye);
        	source.normalize();
        	
        	// view vector = through the eye coord vertex
        	Vector3f viewer = new Vector3f(veye);
        	viewer.scale(-1);
        	viewer.normalize();
        	
        	// half vector = light source + view / |light source + view\
        	Vector3f half = new Vector3f(viewer);
        	half.add(source);
        	half.normalize();
        	
        	
        	// Compute the diffuse color
        	
    		// diffuse intensity and specular intensity
        	Color3f dintense = light.getIntensity();
        	Color3f sintense = Pipeline.specularColor;
        	
        	// diffuse intensity - l*n - light (source) * normal
        	float dif = Math.max(0, source.dot(f.n));
        	
        	// Compute the specular color
        	// specular intensity - Ls =  ks I max(0, n · h)^k
        	float s = (float) Math.max(0, Math.pow((double)(half.dot(f.n)), (double) specularExponent));
           
        	
        	// add diffuse and specular to the color
            Color3f col = new Color3f((f.c.x * dif * dintense.x + s * sintense.x), 
            			(f.c.y * dif * dintense.y + s * sintense.y), 
        				(f.c.z * dif * dintense.z + s * sintense.z));
            color.add(col);
    	}
    	// add ambience to the color
    	color.add(ambience);
    	// clamp - prevent color values from being over what they're allowed
        color.clamp(0,  1);
        
        // Output the color to the framebuffer
    	fb.set(f.x, f.y, color.x, color.y, color.z, f.depth);
    }

    public void updateLightModel(Pipeline pipe) {
        // TODO : Update the light model (lights, amibent intensity, etc) to
        // ones given by the pipeline.
    	
    	lights = pipe.lights;
    	ambientIntensity = pipe.ambientIntensity;
    	specularColor = pipe.specularColor;
    	specularExponent = pipe.specularExponent;

    }
}
