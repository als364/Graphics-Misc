package ray.shader;

import java.util.ArrayList;

import ray.IntersectionRecord;
import ray.Ray;
import ray.Scene;
import ray.light.Light;
import ray.math.Color;
import ray.math.Vector3;
import ray.math.Point3;

/**
 * A Lambertian material scatters light equally in all directions. BRDF value is
 * a constant
 *
 * @author ags
 */
public class Lambertian extends Shader {
	
	/** The color of the surface. */
	protected final Color diffuseColor = new Color(1, 1, 1);
	public void setDiffuseColor(Color inDiffuseColor) { diffuseColor.set(inDiffuseColor); }
	
	public Lambertian() { }
	
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
		//   1. Add contribution from each light source to the final pixel . 
		//   2. See how to use isShadowed().
		
		// Get the light direction
		// Compute the BRDF value
		
		Ray r = new Ray(record.location, toEye);
		r.evaluate(record.location, .000001);
		
        Vector3 n = new Vector3(record.normal);
        n.normalize();
		
		
        Color outC = new Color(0, 0, 0);
        for (int i = 0; i < lights.size(); i++)
        {		Light light = lights.get(i);
                Color out = new Color(diffuseColor);
                if (!isShadowed(scene, light, record))
                {		Vector3 lightDir = new Vector3();
						lightDir.sub(light.position, record.location);
						lightDir.normalize();
						

                        out.scale(light.intensity);
                        out.scale(Math.max(0, n.dot(lightDir)));
                        outC.add(out);
                }
        }
        outColor.set(outC);
		}
	
	/**
	 * @see Object#toString()
	 */
	public String toString() {
		
		return "lambertian: " + diffuseColor;
	}
	
}