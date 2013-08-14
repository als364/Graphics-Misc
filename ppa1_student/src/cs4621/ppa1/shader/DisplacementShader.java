package cs4621.ppa1.shader;

import java.nio.FloatBuffer;

import javax.media.opengl.GL2;

import cs4621.framework.GlslException;
import cs4621.framework.Program;
import cs4621.framework.TextureOneDim;
import cs4621.framework.TextureUnit;
import cs4621.framework.Uniform;
import cs4621.ppa1.shape.Mesh;

public class DisplacementShader extends BaseShader {
	
	// Shader file names (files are located in cs4621.shaders)
	final static String VERTEX_FILE_NAME = "displacement_shader.vs";
	final static String FRAGMENT_FILE_NAME = "displacement_shader.fs";		
	
	// The name of the uniform variables as it appears in the shaders
	static final String TIME_UNIFORM_NAME = "time";
	static final String ENABLE_LIGHTS_UNIFORM_NAME = "enable_lights";
	
	// Use this Uniform object to work with the enableLights shader uniform
	private Uniform enableLightsUniform = null;	
	
	// Use this Uniform object to work with the time shader uniform
	private Uniform timeUniform = null;
	private float count = 0;
	
	Boolean isInitialized = false;
		
	public DisplacementShader() {
		super("Displacement Shader", VERTEX_FILE_NAME, FRAGMENT_FILE_NAME);
	}
			
	public Boolean init(GL2 gl) {
		
		if (isInitialized) {
			return true;
		}		
		
		// Return false, if the shaders do not compile, or program does not link
		if ( !super.init(gl) ) {
			return false;
		}
		
		// Retrieve the enableLights uniform location (in the fragment shader)
		enableLightsUniform = 
			shaderProgram.GetUniforms().get(ENABLE_LIGHTS_UNIFORM_NAME);
		
		if (enableLightsUniform == null) {
			System.out.println("Uniform " + ENABLE_LIGHTS_UNIFORM_NAME + " NOT used");
		}
		
		// Retrieve the time uniform location (in the vertex shader)
		timeUniform = shaderProgram.GetUniforms().get(TIME_UNIFORM_NAME);
		
		if (timeUniform == null) {
			System.out.println("Uniform " + TIME_UNIFORM_NAME + " NOT used");
		}		
								
		return this.isInitialized = true;
	}
	
	public void render(GL2 gl, Mesh currentMesh) {
		if ( !isInitialized ) {
			return;
		}
		else
		{
			// TODO(P4) Update the value of the time uniform (with some reasonable
			// value) and render the given mesh, using the Displacement shader.
			init(gl);
			gl.glCullFace(GL2.GL_BACK);
			gl.glEnable(GL2.GL_CULL_FACE);
			//enableLighting(gl, true);
			count += .01;
			shaderProgram.use();
			timeUniform.set1Float(count);
			currentMesh.render(gl);
			shaderProgram.unuse();
		}
	}
	
	// Enable/disable the light in the scene
	public void enableLighting(GL2 gl, Boolean enable) {		
		if (enableLightsUniform == null) {
			return;
		}
		
		shaderProgram.use();
		if (enable) {
			// Enable the lights in the shader
			enableLightsUniform.set1Int(1);							
		} else {
			// Disable the lights in the shader
			enableLightsUniform.set1Int(0);
		}
		shaderProgram.unuse();
	}		
}
