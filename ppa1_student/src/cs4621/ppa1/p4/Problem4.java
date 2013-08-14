package cs4621.ppa1.p4;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;

import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.vecmath.Vector4f;

import cs4621.framework.CameraController;
import cs4621.framework.GLSceneDrawer;
import cs4621.ppa1.shader.BaseShader;
import cs4621.ppa1.shader.DisplacementShader;
import cs4621.ppa1.shader.NormalShader;
import cs4621.ppa1.shader.ToonShader;
import cs4621.ppa1.shape.Cube;
import cs4621.ppa1.shape.Cylinder;
import cs4621.ppa1.shape.Mesh;
import cs4621.ppa1.shape.Sphere;
import cs4621.ppa1.shape.Teapot;
import cs4621.ppa1.shape.Torus;
import cs4621.ppa1.ui.OneFourViewPanel;
import cs4621.ppa1.ui.ToleranceSliderPanel;

@SuppressWarnings("rawtypes")
public class Problem4 extends JFrame implements GLSceneDrawer, ChangeListener {
	private static final long serialVersionUID = 1L;
		
	ArrayList<Mesh> meshes;
	
	ArrayList<BaseShader> shaders;
	
	OneFourViewPanel oneFourViewPanel;
	ToleranceSliderPanel sliderPanel;
	
	JPanel comboBoxPanel;
	
	JComboBox shapeComboBox;
	JComboBox shaderComboBox;
	
	boolean showFourView = true;
	boolean showOneView = false;
	
	float[] objectDiffuseColor = new float[] {1.0f, 0.0f, 0.0f, 0.0f};
	
	// Directional light vector	
	private float[] lightDirection = {1f, 0.0f, -1f, 0.0f};	
		
	public Problem4() {
		super("CS 4621/5621 Programming Assignment 1 / Problem 4");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		addWindowListener( new WindowAdapter() {
            public void windowClosing( WindowEvent windowevent ) { 
            	terminate();
            }
        });		
					
		oneFourViewPanel = new OneFourViewPanel(this);
		getContentPane().add(oneFourViewPanel, BorderLayout.CENTER);
		
		sliderPanel = new ToleranceSliderPanel(this);
		getContentPane().add(sliderPanel, BorderLayout.EAST);			
		
		shapeComboBox = new JComboBox();				
		shaderComboBox = new JComboBox();
		
		comboBoxPanel = new JPanel(new GridLayout(1, 2));
		
		comboBoxPanel.add(shapeComboBox);
		comboBoxPanel.add(shaderComboBox);
		
		getContentPane().add(comboBoxPanel, BorderLayout.NORTH);
		
		initMesh();		
		
		createShaders();
	}	
	
	public void run()
	{
		setSize(800, 600);
		setLocationRelativeTo(null);
		setVisible(true);
		oneFourViewPanel.startAnimation();
	}
		
	public static void main(String[] args)
	{
		new Problem4().run();		
	}
		
	@SuppressWarnings("unchecked")
	private void initMesh()
	{
		meshes = new ArrayList<Mesh>();
		meshes.add(new Teapot());
		meshes.add(new Torus());
		meshes.add(new Sphere());
		meshes.add(new Cylinder());
		meshes.add(new Cube());					
								
		updateMeshTolerance(sliderPanel.getTolerance());
		
		shapeComboBox.addItem("Teapot");
		shapeComboBox.addItem("Torus");
		shapeComboBox.addItem("Sphere");
		shapeComboBox.addItem("Cylinder");
		shapeComboBox.addItem("Cube");				
	}
	
	private void createShaders()
	{
		shaders = new ArrayList<BaseShader>();
		shaders.add(new ToonShader());
		shaders.add(new NormalShader());
		shaders.add(new DisplacementShader());
												
		shaderComboBox.addItem("Toon Shader");
		shaderComboBox.addItem("Normal Shader");
		shaderComboBox.addItem("Displacement Shader");		
	}
	
	private Boolean initShaders(GL2 gl)
	{
		for (BaseShader shader : shaders) {
			if ( !shader.init(gl) ) {
				System.err.println("Can not initialzie " + shader.getName());
				
				return false;
			}
		}
		
		return true;
	}
	
	private void initLight(GL2 gl)
	{   
		// TODO(P4) Normalize the light direction, 
		// where the light direction is stored in "lightDirection"
		Vector4f ld = new Vector4f(lightDirection[0], lightDirection[1], lightDirection[2], lightDirection[3]);
		ld.normalize();
		lightDirection[0] = ld.x;
		lightDirection[1] = ld.y;
		lightDirection[2] = ld.z;
		lightDirection[3] = ld.w;
		
		// TODO(P4) Setup the GL_LIGHT0 unit as a directional light source, 
		// using the GL_POSITION attribute to store the normalized direction.;
		gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_POSITION, lightDirection, 0);
		
	}
	
	
	public void init(GLAutoDrawable drawable, CameraController cameraController) {
		final GL2 gl = drawable.getGL().getGL2();
		
		gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);		
		
		// Set depth buffer.
		gl.glClearDepth(1.0f);		
		gl.glDepthFunc(GL2.GL_LESS);
		gl.glEnable(GL2.GL_DEPTH_TEST);
		
		// Set blending mode.
		gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE_MINUS_SRC_ALPHA);
		gl.glDisable(GL2.GL_BLEND);		
				
		gl.glHint(GL2.GL_PERSPECTIVE_CORRECTION_HINT, GL2.GL_NICEST);		
		
		// Initialize the directional light source
		initLight(gl);
		
		// Try to initialize the GLSL shaders
		initShaders(gl);
			
		oneFourViewPanel.startAnimation();
	}


	public void draw(GLAutoDrawable drawable, CameraController cameraController) {
		final GL2 gl = drawable.getGL().getGL2();			
														
		int meshIndex = shapeComboBox.getSelectedIndex();
		int shaderIndex = shaderComboBox.getSelectedIndex();
		
		if (oneFourViewPanel.isWireframeMode())
			gl.glPolygonMode(GL2.GL_FRONT_AND_BACK, GL2.GL_LINE);
		else
			gl.glPolygonMode(GL2.GL_FRONT_AND_BACK, GL2.GL_FILL);
		
		if (oneFourViewPanel.isLightingMode()) 
			shaders.get(shaderIndex).enableLighting(gl, true);
		else
			shaders.get(shaderIndex).enableLighting(gl, false);
		
		gl.glColor4f(objectDiffuseColor[0], objectDiffuseColor[1], objectDiffuseColor[2], objectDiffuseColor[3]);
		
		shaders.get(shaderIndex).render(gl, meshes.get(meshIndex));
	}
		
	@Override
	public void stateChanged(ChangeEvent e) {
		if (e.getSource() == sliderPanel.getSlider())
		{
			updateMeshTolerance(sliderPanel.getTolerance());
		}
	}
	
	protected void updateMeshTolerance(float tolerance)
	{
		for(Mesh mesh : meshes)
			mesh.buildMesh(tolerance);
	}
	
	public void terminate()
	{		
		oneFourViewPanel.stopAnimation();
		dispose();
		System.exit(0);
	}

	@Override
	public void mousePressed(MouseEvent e, CameraController controller) {
		// NOP
	}

	@Override
	public void mouseReleased(MouseEvent e, CameraController controller) {
		// NOP
	}

	@Override
	public void mouseDragged(MouseEvent e, CameraController controller) {
		// NOP
	}
}
