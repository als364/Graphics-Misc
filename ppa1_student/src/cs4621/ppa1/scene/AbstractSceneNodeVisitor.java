package cs4621.ppa1.scene;

public abstract class AbstractSceneNodeVisitor implements SceneNodeVisitor
{
	public void visitChildren(SceneNode node)
	{
		for (int ctr = 0; ctr < node.getChildCount(); ctr++)
			((SceneNode)node.getChildAt(ctr)).accept(this);
	}
}
