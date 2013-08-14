package cs4621.framework;

import java.util.HashMap;

import javax.media.opengl.GL2;

public class Program {

private static final String SHADERS_BASE_DIR = "src//cs4621//glsl_shaders//";

private static Program current = null;

// ************* Static functions *************

	public static Boolean isAProgramInUse() {
		return current != null;
	}
	
	public static Program getCurrent() {
	    return current;
	}
	
	public static void unuseProgram(GL2 gl) {
		gl.glUseProgramObjectARB(0);    
	    current = null;
	}
	
// ************* Private variables *************
	
	private int id = -1;
	private VertexShader vertexShader;
	private FragmentShader fragmentShader;
	private GL2 gl;
	
	private HashMap<String, Uniform> uniforms;
		
// ************* Public interface *************
	
	public Program(GL2 glContext, String vertexSrcFile, 
			String fragmentSrcFile) throws GlslException {
		vertexShader = null;
		fragmentShader = null;
		gl = glContext;
		
		id = gl.glCreateProgramObjectARB();
		
		// Attach shaders and link the program (may throw exception)
		buildProgram(SHADERS_BASE_DIR + vertexSrcFile, 
				SHADERS_BASE_DIR + fragmentSrcFile);
		
		// Create a hash map from all the 'active' uniform variables
		initializeUniforms();
	}
	
	public int getId() {
		return id;
	}
	
	public Boolean isUsed() {
		return current == this;
	}
	
	public void use() {
		this.gl.glUseProgramObjectARB(this.id);
	    current = this;
	}
	
	public void unuse() {
	    unuseProgram(gl);
	}
	
	public HashMap<String, Uniform> GetUniforms() {
		return this.uniforms;
	}
	
	public Uniform getUniform(String name) {
		return uniforms.get(name);
	}
	
// ************* Protected functions *************
	
	protected void finalize() {
	}
	
	protected void buildProgram(String vertexSrcFile, String fragmentSrcFile) throws GlslException {
		
		vertexShader = new VertexShader(gl, vertexSrcFile);
		fragmentShader = new FragmentShader(gl, fragmentSrcFile);		
	    
	    // Attach the vertex shader
	    gl.glAttachShader(id, vertexShader.GetId());
	    
	    // Attach the fragment shader
	    gl.glAttachShader(id, fragmentShader.GetId());
	    
	    gl.glLinkProgramARB(id);
	    
	    // Check the linking status
		int[] linkCheck = new int[1];
		gl.glGetObjectParameterivARB(id,
				GL2.GL_OBJECT_LINK_STATUS_ARB, linkCheck, 0);
		
		if (linkCheck[0] == GL2.GL_FALSE) {
			throw new GlslException("Link error " + 
					Shader.getInfoLog(gl, id));
		}
	}
	
// ************* Private functions *************
	
	private void initializeUniforms() {		  
		uniforms = new HashMap<String, Uniform>();		   
	    
	    int[] uniformCount = new int[1];
	    gl.glGetProgramiv(id, GL2.GL_ACTIVE_UNIFORMS, uniformCount, 0);
	    
	    System.out.print("GLSL uniforms: ");
		for(int uniform_index = 0; uniform_index < uniformCount[0]; 
			uniform_index++) {
			Uniform currUniform = new Uniform(gl, this, uniform_index);
									
			if ( !currUniform.getName().startsWith("gl_") ) {
				System.out.print(currUniform.getName() + " ");
				uniforms.put(currUniform.getName(), currUniform);								
			}
		}		
		System.out.println();
	} 
}
