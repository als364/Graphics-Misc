package cs4621.ppa1.scene;

public class SearchForMeshIdVisitor extends AbstractSceneNodeVisitor
{
	int meshId;
	SceneNode answerNode = null;
	
	public SceneNode searchForMeshId(int meshId, SceneNode node)
	{
		this.meshId = meshId;
		this.answerNode = null;
		node.accept(this);
		return answerNode;
	}
	

	@Override
	public void visit(LightNode node)
	{
		visitChildren(node);		
	}

	@Override
	public void visit(MeshNode node)
	{
		if (node.getMesh().getId() == meshId)
			answerNode = node;
	}

	@Override
	public void visit(TransformationNode node)
	{
		visitChildren(node);
	}	
}
