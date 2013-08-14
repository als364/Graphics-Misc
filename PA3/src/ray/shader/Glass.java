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

/**
 * A Glass material. 
 *
 */
public class Glass extends Shader {
	
	protected double refractiveIndex;
	public void setRefractiveIndex(double refractiveIndex) { this.refractiveIndex = refractiveIndex; }
	
	
	public Glass() { }
	
	/**
	 * @see ray.shader.Shader#shade(ray.math.Color, Scene,
	 *      Lights, ray.math.Vector3, ray.IntersectionRecord, depth, contribution, internal)
	 */
	public void shade(Color outColor, Scene scene, ArrayList<Light> lights, Vector3 toEye, 
			IntersectionRecord record, int depth, double contribution, boolean internal) {

		// TODO(B): fill in this function.
		
		// Implement Snell's law, make a new Workspace and recursive call to shadeRay  
		// for the reflected and the refracted rays. Use Schlick's Approximation
		// to weight the contributions of the corresponding rays.
		
		//Snell's Law
		double n1;
		double n2;
		double theta1;
		double theta2;
		if(!internal)
		{
			n1 = 1;
			n2 = refractiveIndex;
			theta1 = Math.acos(toEye.dot(record.normal) / (toEye.length() * record.normal.length()));
			theta2 = snell(n1, n2, theta1);
		}
		else
		{
			n1 = refractiveIndex;
			n2 = 1;
			theta1 = Math.acos(toEye.dot(record.normal) / (toEye.length() * record.normal.length()));
			theta2 = snell(n1, n2, theta1);
		}
		
		double n = n1 / n2;
		
		//calculate reflection Rr = Ri - 2 N (Ri . N)  Ri = -toEye, N=normal, Rr = reflection vector 
		//http://paulbourke.net/geometry/reflected/
		Vector3 N = new Vector3(record.normal);
		//N.normalize();
		Vector3 reflect = new Vector3(toEye);
		N.scale(2 * record.normal.dot(toEye));
		reflect.sub(N);
		//reflect.normalize();
		//outColor.add(new Color(reflect.x, reflect.y, reflect.z));
		
		Vector3 newViewer = new Vector3(toEye);
		newViewer.scale(n);
		Vector3 M = new Vector3(record.normal);
		M.scale(Math.cos(theta1));
		M.sub(toEye);
		//M.normalize();
		double c1 = record.normal.dot(toEye);
		double c2 = Math.sqrt((1 - (n * n)) * (1 - (c1 * c1)));
		Vector3 refract = new Vector3(N);
		refract.scale((n * c1) - c2);
		refract.add(newViewer);
		refract.normalize();
		//outColor.add(new Color(refract.x, refract.y, refract.z));
		
		//Schlick's Approximation
		double cosTheta = toEye.dot(record.normal) / (toEye.length() * record.normal.length());
		double R0 = Math.pow(((n2 - n1) / (n2 + n1)), 2);
		//double R = R0 + (1 - R0) * (1 - Math.pow(Math.cos(theta1), 5));
		double R = R0 + (1 - R0) * Math.pow(1 - cosTheta, 5);
		
		Workspace reflectSpace = new Workspace();
		reflectSpace.eyeRay.set(record.location, reflect);
        reflectSpace.eyeRay.direction.scale(-1);
        reflectSpace.eyeRay.makeOffsetRay();
        reflectSpace.shadowRay.makeOffsetRay();
        reflectSpace.toEye.set(reflectSpace.eyeRay.direction);
        reflectSpace.toEye.scale(-1);
        reflectSpace.toEye.normalize();
        reflectSpace.eyeRecord.set(new IntersectionRecord());
        
        Color reflectColor = new Color();
        RayTracer.shadeRay(reflectColor, scene, reflectSpace.eyeRay, reflectSpace, lights, depth+1, R*contribution, internal);
        //System.out.println("ReflectColor: " + reflectColor);
		reflectColor.scale((1-R)*contribution);
		outColor.add(reflectColor);
		
		Workspace refractSpace = new Workspace();
		refractSpace.eyeRay.set(record.location, reflect);
        refractSpace.eyeRay.direction.scale(-1);
        refractSpace.eyeRay.makeOffsetRay();
        refractSpace.shadowRay.makeOffsetRay();
        refractSpace.toEye.set(reflectSpace.eyeRay.direction);
        refractSpace.toEye.scale(-1);
        refractSpace.toEye.normalize();
        refractSpace.eyeRecord.set(new IntersectionRecord());
        
        Color refractColor = new Color();
        RayTracer.shadeRay(refractColor, scene, refractSpace.eyeRay, refractSpace, lights, depth+1, (1-R)*contribution, internal);
        //System.out.println("RefractColor: " + refractColor);
        refractColor.scale(R*contribution);
        outColor.add(refractColor);
        
        //System.out.println("OutColor: " + outColor);
	}
	
	private double snell(double n1, double n2, double theta1)
	{
		double theta2 = 0;
		theta2 = Math.asin((n1 / n2) * Math.sin(theta1));
		return theta2;
	}
}