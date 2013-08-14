package cs4621.ppa1.scene;

public interface SceneNodeVisitor
{
	public void visit(LightNode node);
	public void visit(MeshNode node);
	public void visit(TransformationNode node);	
}
