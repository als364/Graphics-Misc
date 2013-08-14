package cs4621.ppa1.shader;

import java.nio.FloatBuffer;


import javax.media.opengl.GL2;
import javax.vecmath.Tuple3f;

import cs4621.framework.GlslException;
import cs4621.framework.Program;
import cs4621.framework.TextureOneDim;
import cs4621.framework.TextureUnit;
import cs4621.framework.Uniform;
import cs4621.ppa1.shape.Mesh;

public class NormalShader extends BaseShader {
	
	// Shader file names (files are located in cs4621.shaders)
	final static String VERTEX_FILE_NAME = "normal_shader.vs";
	final static String FRAGMENT_FILE_NAME = "normal_shader.fs";		
	
	Boolean isInitialized = false;
		
	public NormalShader() {
		super("Normal Shader", VERTEX_FILE_NAME, FRAGMENT_FILE_NAME);
	}
	
	public Boolean init(GL2 gl) {
		
		if (isInitialized) {
			return true;
		}
		
		// Return false, if shaders do not compile, or program does not link
		if ( !super.init(gl) ) {
			return false;
		}			
						
		return this.isInitialized = true;
	}
	
	public void render(GL2 gl, Mesh currentMesh) {
		if ( !isInitialized ) {
			return;
		}
		else 
		{
			// TODO(P4) Render the given mesh using the Normal shader
			init(gl);
			gl.glCullFace(GL2.GL_BACK);
			gl.glEnable(GL2.GL_CULL_FACE);
			enableLighting(gl, true);
			shaderProgram.use();
			currentMesh.render(gl);
			shaderProgram.unuse();
		}
	}
	
	// Enable/disable the OpenGL light in the scene
	public void enableLighting(GL2 gl, Boolean enable) {
		// NOP
	}		
}
