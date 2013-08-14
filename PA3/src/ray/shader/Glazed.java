package ray.shader;

import java.util.ArrayList;

import ray.IntersectionRecord;
import ray.Ray;
import ray.RayTracer;
import ray.Scene;
import ray.Workspace;
import ray.light.Light;
import ray.math.Color;
import ray.math.Vector3;

public class Glazed extends Shader {
	
	protected double refractiveIndex;
	public void setRefractiveIndex(double refractiveIndex) { this.refractiveIndex = refractiveIndex; }
	
	protected Shader substrate;
	public void setSubstrate(Shader substrate) { this.substrate = substrate; }
	
	/** The exponent controlling the sharpness of the specular reflection. */
	protected double exponent = 1.0;
	public void setExponent(double exponent) { this.exponent = exponent; }
	
	public Glazed() { }
	
	/**
	 * Calculate the BRDF value for this material at the intersection described in record.
	 * Returns the BRDF color in outColor.
	 * @param outColor Space for the output color
	 * @param scene The scene
	 * @param lights The lights
	 * @param toEye Vector pointing towards the eye
	 * @param record The intersection record, which hold the location, normal, etc.
	 * @param depth The depth of recursive calls.
	 *        You can use it any way you want as long as it's consistent.
	 * @param contribution The contribution of the current ray.
	 * 		  You can use it any way you want.
	 * @param internal You can ignore this for glazed.
	 */
	public void shade(Color outColor, Scene scene, ArrayList<Light> lights, Vector3 toEye, 
			IntersectionRecord record, int depth, double contribution, boolean internal) {
		// TODO(B): fill in this function.
		// reflectance at normal index (R0)
		double R0 = ((refractiveIndex - 1) / (refractiveIndex + 1))*((refractiveIndex - 1) / 
				(refractiveIndex + 1));
		//angle between the normal and the eye vector (A · B = A B cos theta = |A||B| cos theta)
		double cosTheta = toEye.dot(record.normal) / (toEye.length() * record.normal.length());
		
        Color base = new Color();
        this.substrate.shade(base, scene, lights, toEye, record, depth+1, 1, internal);
        if (cosTheta <= 0) {
                outColor.set(base);
                return;
        }
		
		
		//Schlick's approximation
		double R = R0 + (1 - R0) * Math.pow(1 - cosTheta, 5);
		

		//calculate reflection Rr = d - 2 N (Ri . N)  Ri = -toEye, N=normal, Rr = reflection vector 
		//// R = toEye - 2(toEye . n)n
		//http://paulbourke.net/geometry/reflected/
		Vector3 reflectVector = new Vector3(toEye);//toeye
		
		Vector3 subVector = new Vector3(record.normal);//N
		subVector.scale(2 * record.normal.dot(toEye)); //(2 * toEye * N)
		reflectVector.sub(subVector);


  
        Workspace workspace = new Workspace();
        workspace.eyeRay.set(record.location, reflectVector);
        workspace.eyeRay.direction.scale(-1);
        workspace.eyeRay.makeOffsetRay();
        workspace.toEye.set(workspace.eyeRay.direction);
        workspace.toEye.scale(-1);
        workspace.toEye.normalize();
        
        workspace.eyeRecord.set(new IntersectionRecord());
        
        
        
        Color glazed = new Color();
        RayTracer.shadeRay(glazed, scene, workspace.eyeRay, workspace, lights, depth+1, (1-R)*contribution, internal);
        workspace.shadowRay.makeOffsetRay();
        base.scale((1-R) * contribution);
        glazed.scale((1-R) * contribution);
        
        
        outColor.set(base);
        //outColor.set(0, 0, 0);
        outColor.add(glazed);
	}
}