package cs4621.ppa1.shader;

import java.nio.FloatBuffer;

import javax.media.opengl.GL2;

import cs4621.framework.GlslException;
import cs4621.framework.Program;
import cs4621.framework.TextureOneDim;
import cs4621.framework.TextureUnit;
import cs4621.framework.Uniform;
import cs4621.ppa1.shape.Mesh;

public class ToonShader extends BaseShader {
	
	// Shader file names (files are located in cs4621.shaders)
	final static String VERTEX_FILE_NAME = "toon_shader.vs";
	final static String FRAGMENT_FILE_NAME = "toon_shader.fs";
	
	// Defines the thicnkess of the silhouette lines
	final static float SILHOUETTE_LINE_WIDTH = 5.0f;
	
	// The name of the uniform variables as it appears in the fragment shader
	static private final String ENABLE_LIGHTS_UNIFORM_NAME = "enable_lights";				
	
	// Use this uniform object to work with the num_lights uniform
	private Uniform enableLightsUniform = null;	
	
	Boolean isInitialized = false;
		
	public ToonShader() {
		super("Toon Shader", VERTEX_FILE_NAME, FRAGMENT_FILE_NAME);
	}
	
	public Boolean init(GL2 gl) {
		
		if (isInitialized) {
			return true;
		}
		
		// Return false, if shaders do not compile, or program does not link
		if ( !super.init(gl) ) {
			return false;
		}			
		
		// Retrieve the enableLights uniform location (in the fragment shader)
		enableLightsUniform = 
			shaderProgram.GetUniforms().get(ENABLE_LIGHTS_UNIFORM_NAME);
		
		if (enableLightsUniform == null) {
			System.out.println("Uniform " + ENABLE_LIGHTS_UNIFORM_NAME + " NOT used");
		}
						
		return this.isInitialized = true;
	}
	
	public void render(GL2 gl, Mesh currentMesh) {		
		if ( !isInitialized ) {
			return;
		}
		else
		{
			// TODO(P4) First, draw the solid object, using the Toon shader
			// 
			// 1. Enable back face culling
			// 2. Render the given mesh (in solid mode) using the Toon shader
			init(gl);
			gl.glCullFace(GL2.GL_BACK);
			gl.glEnable(GL2.GL_CULL_FACE);
			//enableLighting(gl, true);
			shaderProgram.use();
			currentMesh.render(gl);
			shaderProgram.unuse();
			// TODO(P4) Second, draw the object's outline in wire-frame mode
			//
			// 1. Switch to the fixed pipeline
			// 2. Enable front-face culling
			// 3. Use glPolygonOffset to offset the wire-frame model
			// 4. Render the wire-frame model with thicker, black edges, where
			// the edge width is defined in the SILHOUETTE_LINE_WIDTH constant
			
			gl.glCullFace(gl.GL_FRONT);
			gl.glPolygonOffset(0, 0.1f);
			gl.glLineWidth(SILHOUETTE_LINE_WIDTH);
			gl.glPolygonMode(GL2.GL_FRONT_AND_BACK, GL2.GL_LINE);
			gl.glColor3f(0, 0, 0);
			currentMesh.render(gl);
		}
		gl.glLineWidth(1);
	}
	
	// This functions enables/disables the light contribution in the 
	// shader, by setting the enableLights uniform variable to 1/0.
	public void enableLighting(GL2 gl, Boolean enable) {
		if (enableLightsUniform == null) {
			return;
		}
		
		shaderProgram.use();
		if (enable) {			
			// Enable the lights in the shader		
			//System.out.println("1");
			enableLightsUniform.set1Int(1);			
		} else {
			// Disable the lights in the shader
			//System.out.println("0");
			enableLightsUniform.set1Int(0);			
		}
		shaderProgram.unuse();
	}	
}
