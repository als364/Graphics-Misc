package cs4621.ppa1.p2;

import java.awt.BorderLayout;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;

import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import cs4621.framework.CameraController;
import cs4621.framework.GLSceneDrawer;
import cs4621.ppa1.shape.Cone;
import cs4621.ppa1.shape.Cylinder;
import cs4621.ppa1.shape.Sphere;
import cs4621.ppa1.shape.TriangleMesh;
import cs4621.ppa1.ui.OneFourViewPanel;
import cs4621.ppa1.ui.ToleranceSliderPanel;

@SuppressWarnings("rawtypes")
public class Problem2 extends JFrame implements GLSceneDrawer, ChangeListener {
	private static final long serialVersionUID = 1L;
		
	ArrayList<TriangleMesh> meshes;
	
	OneFourViewPanel oneFourViewPanel;
	ToleranceSliderPanel sliderPanel;				
	JComboBox shapeComboBox;
	
	boolean showFourView = true;
	boolean showOneView = false;

	float[] lightAmbient = new float[] { 1.0f, 1.0f, 1.0f, 1.0f };
	float[] lightDiffuse = new float[] { 1.0f, 1.0f, 1.0f, 1.0f };
	float[] lightSpecular = new float[] { 1.0f, 1.0f, 1.0f, 1.0f };
	float[] lightPosition = new float[] { 5, 5, 5, 1.0f};
	
	float[] ambient = new float[] {0.05f, 0.05f, 0.05f, 0.05f};
	float[] diffuse = new float[] {1.0f, 0.0f, 0.0f, 0.0f};
	float[] specular = new float[] {1.0f, 1.0f, 1.0f, 1.0f};
	float   shininess = 50.0f;
		
	public Problem2() {
		super("CS 4621/5621 Programming Assignment 1 / Problem 2");
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
		getContentPane().add(shapeComboBox, BorderLayout.NORTH);
		
		initMesh();		
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
		new Problem2().run();		
	}
		
	@SuppressWarnings("unchecked")
	private void initMesh()
	{
		meshes = new ArrayList<TriangleMesh>();	
		meshes.add(new Sphere());
		meshes.add(new Cylinder());
		meshes.add(new Cone());
		updateMeshTolerance(sliderPanel.getTolerance());
				
		shapeComboBox.addItem("Sphere");
		shapeComboBox.addItem("Cylinder");
		shapeComboBox.addItem("Cone");
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
		
		// Forces OpenGL to normalize transformed normals to be of
		// unit length before using the normals in OpenGL's lighting equations.
		gl.glEnable(GL2.GL_NORMALIZE);
		
		// Cull back faces.
		gl.glDisable(GL2.GL_CULL_FACE);
				
		gl.glHint(GL2.GL_PERSPECTIVE_CORRECTION_HINT, GL2.GL_NICEST);
			
		oneFourViewPanel.startAnimation();
	}


	public void draw(GLAutoDrawable drawable, CameraController cameraController) {
		final GL2 gl = drawable.getGL().getGL2();	
		
		gl.glColorMaterial(GL2.GL_FRONT_AND_BACK, GL2.GL_DIFFUSE);
		gl.glEnable(GL2.GL_COLOR_MATERIAL);
		
		if (oneFourViewPanel.isLightingMode())
		{			
			gl.glEnable(GL2.GL_LIGHTING);
			gl.glEnable(GL2.GL_LIGHT0);
			
			gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_AMBIENT, lightAmbient, 0 );
			gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_DIFFUSE, lightDiffuse, 0 );
			gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_SPECULAR, lightSpecular, 0 );
			gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_POSITION, lightPosition, 0);				
		}
		else
		{		
			gl.glDisable(GL2.GL_LIGHTING);
			gl.glDisable(GL2.GL_LIGHT0);	
		}
		
		gl.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_AMBIENT, ambient, 0);
		gl.glColor4f(diffuse[0], diffuse[1], diffuse[2], diffuse[3]);
		gl.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_SPECULAR, specular, 0);
		gl.glMaterialf(GL2.GL_FRONT_AND_BACK, GL2.GL_SHININESS, shininess);
				
		int meshIndex = shapeComboBox.getSelectedIndex();
		
		if (oneFourViewPanel.isWireframeMode())
			gl.glPolygonMode(GL2.GL_FRONT_AND_BACK, GL2.GL_LINE);
		else
			gl.glPolygonMode(GL2.GL_FRONT_AND_BACK, GL2.GL_FILL);
		
		meshes.get(meshIndex).render(gl);
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
		for(TriangleMesh mesh : meshes)
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
