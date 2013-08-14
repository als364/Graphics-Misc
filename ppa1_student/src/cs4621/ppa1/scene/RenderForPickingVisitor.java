package cs4621.ppa1.scene;

public class RenderForPickingVisitor extends AbstractGLVisitor
{
	public RenderForPickingVisitor()
	{
		// NOP
	}
	
	@Override
	public void process(LightNode node)
	{
		// We don't render a light.
	}
	
	@Override
	public void process(MeshNode node)
	{
		gl.glLoadName(node.getMesh().getId());		
		node.getMesh().render(gl);		
	}	
}
