package ray.shader;

import java.util.ArrayList;

import ray.IntersectionRecord;
import ray.Ray;
import ray.Scene;
import ray.light.Light;
import ray.math.Color;
import ray.math.Point3;
import ray.math.Vector3;

/**
 * A Phong material. Uses the Modified Blinn-Phong model which is energy
 * preserving and reciprocal.
 *
 * @author ags
 */
public class Phong extends Shader {
	
	/** The color of the diffuse reflection. */
	protected final Color diffuseColor = new Color(1, 1, 1);
	public void setDiffuseColor(Color diffuseColor) { this.diffuseColor.set(diffuseColor); }
	
	/** The color of the specular reflection. */
	protected final Color specularColor = new Color(1, 1, 1);
	public void setSpecularColor(Color specularColor) { this.specularColor.set(specularColor); }
	
	/** The exponent controlling the sharpness of the specular reflection. */
	protected double exponent = 1.0;
	public void setExponent(double exponent) { this.exponent = exponent; }
	
	public Phong() { }
	
	/**
	 * Calculate the BRDF value for this material at the intersection described in record.
	 * Returns the BRDF color in outColor.
	 * @param outColor Space for the output color
	 * @param scene The scene
	 * @param lights The lights
	 * @param toEye Vector pointing towards the eye
	 * @param record The intersection record, which hold the location, normal, etc.
	 * @param depth The depth of recursive calls. You can ignore this parameter.
	 * @param contribution The contribution of the current ray. You can ignore this parameter.
	 * @param internal You can ignore this parameter.
	 */
	public void shade(Color outColor, Scene scene, ArrayList<Light> lights, Vector3 toEye, 
			IntersectionRecord record, int depth, double contribution, boolean internal) {	
		// TODO(A): fill in this function.
		// Hint: 
		//   1. Add contribution to the final pixel from each light source.  
		//   2. See how to use isShadowed().
		
		// Get the light direction
		// Calculate the half vector and normalize
		// Compute the specular scale factor
		// Compute the BRDF value
		
		//Ray r = new Ray(record.location, toEye);
		//r.evaluate(record.location, contribution);
		//Point3 pix = new Point3(r.origin);
		
		Ray r = new Ray(record.location, toEye);
		r.evaluate(record.location, .000001);
		
		
		for (int i = 0; i < lights.size(); i++) {
			Light light = lights.get(i);
			if (isShadowed(scene, light, record)) {outColor.set(0, 0, 0);}
			else {
				Vector3 ldir = new Vector3(); //light direction
				ldir.sub(light.position, r.origin); //light direction= location of light-point it hits
				//ldir.evaluate(record.location, contribution);
				ldir.normalize();
				
				
				
				Vector3 half = new Vector3();
				half.add(toEye, ldir); //half vector = normalize (eye vector + light vector)
				half.normalize();
				
				// ratio of specular reflected light to original light
				// I max(0, v*r) ^ n
				double deg = Math.max(record.normal.dot(half), 0);
				double ref = Math.pow(deg, exponent); //sharpness of specular reflection
				     
				
				
				// Compute the BRDF value
				outColor.set(diffuseColor);
				double scl = Math.max(record.normal.dot(ldir), 0);
				outColor.scale(scl);  
				outColor.scaleAdd(ref, specularColor);
			    outColor.scale(light.intensity);

				
			    
	
			}
			}

		
		
	}
	
	/**
	 * @see Object#toString()
	 */
	public String toString() {
		
		return "phong " + diffuseColor + " " + specularColor + " " + exponent + " end";
	}
}