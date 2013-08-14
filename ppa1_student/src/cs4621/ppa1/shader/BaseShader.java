package cs4621.ppa1.shader;

import javax.media.opengl.GL2;


import cs4621.framework.GlslException;
import cs4621.framework.Program;
import cs4621.framework.Shader;
import cs4621.ppa1.shape.Mesh;

public abstract class BaseShader {	
	protected Program shaderProgram = null;
	
	protected String vertexFileName;
	protected String fragmentFileName;
	
	protected String shaderName;
	
	public BaseShader() {}
	
	public BaseShader(String shaderName, 
			String vertexFileName, String fragmentFileName) {
		this.shaderName = shaderName;
		
		this.vertexFileName = vertexFileName;
		this.fragmentFileName = fragmentFileName;
	}
	
	// 1. Load, compile and link the shader program
	// 2. Sub-classes can initialize needed GL functionality here
	public Boolean init(GL2 gl) {
		
		// Check whether GLSL is supported
		if ( !Shader.checkGlslSupport(gl) ) {
			return false;
		}
		
		// Load, compile and link the shaders
		try {		
			this.shaderProgram = new Program(gl, 
					vertexFileName, fragmentFileName);
			
		} catch (GlslException e) {
			
			System.out.println("******* GLSL Begin Error *******");
			System.err.println(e.getMessage());
			System.out.println("******* GLSL End Error *******");
			return false;
		}
		
		// The user should explicitly use the shader
		this.shaderProgram.unuse();
		
		return true;
	}
	
	public abstract void render(GL2 gl, Mesh currentMesh);
	
	public abstract void enableLighting(GL2 gl, Boolean enable);	
	
	public String getName() { return shaderName; }
}
