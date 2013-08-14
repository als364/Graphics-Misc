package cs4621.ppa1.scene;

import javax.media.opengl.GL2;

public abstract class AbstractGLVisitor extends AbstractSceneNodeVisitor
{
	protected GL2 gl = null;
	
	public void visit(GL2 gl, SceneNode node)
	{
		this.gl = gl;
		node.accept(this);
	}
	
	@Override
	public void visit(LightNode node)
	{
		gl.glPushMatrix();
		node.glTransform(gl);
		process(node);		
		visitChildren(node);				
		gl.glPopMatrix();		
	}
	
	@Override
	public void visit(MeshNode node)
	{
		gl.glPushMatrix();
		node.glTransform(gl);
		process(node);		
		visitChildren(node);					
		gl.glPopMatrix();
	}
	
	@Override
	public void visit(TransformationNode node)
	{
		gl.glPushMatrix();
		node.glTransform(gl);
		process(node);
		visitChildren(node);					
		gl.glPopMatrix();
	}
	
	public void process(LightNode node)
	{
		// NOP
	}
	
	public void process(MeshNode node)
	{
		// NOP
	}
	
	public void process(TransformationNode node)
	{
		// NOP
	}
}
