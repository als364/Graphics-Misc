package cs4621.ppa1.scene;

public class RenderVisitor extends AbstractGLVisitor
{	
	public RenderVisitor()
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
		node.getMaterial().use(gl);		
		node.getMesh().render(gl);
		node.getMaterial().unuse(gl);
	}	
}
