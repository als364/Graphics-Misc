package cs4621.ppa1.scene;

public class BuildMeshVisitor extends AbstractSceneNodeVisitor
{
	protected float tolerance;
	
	public BuildMeshVisitor()
	{
		// NOP
	}
	
	public void buildMesh(SceneNode node, float tolerance)
	{
		this.tolerance = tolerance;
		node.accept(this);
	}

	@Override
	public void visit(LightNode node)
	{
		visitChildren(node);		
	}

	@Override
	public void visit(MeshNode node)
	{
		node.getMesh().buildMesh(tolerance);
		visitChildren(node);
	}

	@Override
	public void visit(TransformationNode node)
	{
		visitChildren(node);		
	}
}
