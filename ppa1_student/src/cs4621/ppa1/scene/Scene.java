package cs4621.ppa1.scene;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.Vector;

import javax.media.opengl.GL2;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import org.yaml.snakeyaml.Yaml;

import cs4621.ppa1.shape.Sphere;
import cs4621.ppa1.shape.TriangleMesh;

public class Scene
{
	protected DefaultTreeModel treeModel;
	
	RenderVisitor renderer = new RenderVisitor();
	RenderForPickingVisitor rendererForPicking = new RenderForPickingVisitor();
	BuildMeshVisitor meshBuilder = new BuildMeshVisitor();
	SetupGLLightingVisitor glLightSetter = new SetupGLLightingVisitor();
	SearchForMeshIdVisitor meshIdSearcher = new SearchForMeshIdVisitor();
	
	public Scene()
	{
		SceneNode root = new TransformationNode("Root");
		
		MeshNode sphereNode = new MeshNode("Sphere");
		sphereNode.setMesh(new Sphere());		
		root.add(sphereNode);
			
		LightNode lightNode = new LightNode("Light");
		lightNode.setAmbient(1.0f, 1.0f, 1.0f);
		lightNode.setDiffuse(1.0f, 1.0f, 1.0f);
		lightNode.setSpecular(1.0f, 1.0f, 1.0f);
		lightNode.setPosition(5, 5, 5);
		root.add(lightNode);
		
		treeModel = new DefaultTreeModel(root);			
	}
	
	public void rebuildMeshes(float tolerance)
	{
		meshBuilder.buildMesh((SceneNode)treeModel.getRoot(), tolerance);
	}
	
	public void renderForPicking(GL2 gl)
	{
		rendererForPicking.visit(gl, (SceneNode)treeModel.getRoot());
	}
	
	public void setupLighting(GL2 gl)
	{
		glLightSetter.setupLighting(gl, (SceneNode)treeModel.getRoot());
	}
	
	public void render(GL2 gl)
	{
		renderer.visit(gl, (SceneNode)treeModel.getRoot());
	}
	
	/**
	 * Save the current scene to a file.	 
	 */
	public void save(String filename) throws IOException 
	{	
		Yaml yaml = new Yaml();
		Object rep = ((SceneNode)treeModel.getRoot()).getYamlObjectRepresentation();
		String output = yaml.dump(rep);
		
		FileWriter fstream = new FileWriter(filename);				
		BufferedWriter out = new BufferedWriter(fstream);
		out.write(output);
		out.close();			
	}
	
	/**
	 * Stolen from http://snippets.dzone.com/posts/show/1335
	 */
	private static String readFileAsString(String filePath) throws IOException{
	    byte[] buffer = new byte[(int) new File(filePath).length()];
	    BufferedInputStream f = null;
	    try {
	        f = new BufferedInputStream(new FileInputStream(filePath));
	        f.read(buffer);
	    } finally {
	        if (f != null) try { f.close(); } catch (IOException ignored) { }
	    }
	    return new String(buffer);
	}
	
	public void load(String filename) throws java.io.IOException 
	{
		String fileContent = readFileAsString(filename);
		Yaml yaml = new Yaml();
		Object yamlObject = yaml.load(fileContent);
		
		SceneNode newRoot = SceneNode.fromYamlObject(yamlObject);
		treeModel.setRoot(newRoot);
	}
	
	private static String getBaseClassName(Class<? extends Object> c)
	{
		String classname = c.getName();
		String name = classname.substring(classname.lastIndexOf('.') + 1, classname.length());	
		return name;
	}
	
	/**
	 * Add a new shape to the tree 
	 */
	public void addNewShape(TreePath path, Class<? extends TriangleMesh> c) throws Exception
	{						
		//Get the node to insert into
		SceneNode selected = (SceneNode)treeModel.getRoot();
		if (path != null && path.getLastPathComponent() instanceof TransformationNode)
			selected = (SceneNode) path.getLastPathComponent();
		
		TriangleMesh newObject = c.newInstance();			
		String nodeName = getBaseClassName(c);
		MeshNode node = new MeshNode(nodeName, newObject);
		treeModel.insertNodeInto(node, selected, selected.getChildCount());		
	}
	
	/**
	 * Add a new light to the tree at the end of the given path.
	 */
	public void addNewLight(TreePath path) 
	{	
		//Create a light
		LightNode node = new LightNode("Light");
		
		//Get the node to insert into		
		SceneNode selected = (SceneNode)treeModel.getRoot();
		if (path != null && path.getLastPathComponent() instanceof TransformationNode)
			selected = (SceneNode) path.getLastPathComponent();
		
		treeModel.insertNodeInto(node, selected, selected.getChildCount());		
	}
	
	/**
	 * Filters out extraneous child nodes from the 
	 * supplied array of Treeables.  
	 */
	public Vector<SceneNode> filterChildren(SceneNode[] nodes) 
	{
		// Trim extraneous child nodes
		Vector<SceneNode> filtered = new Vector<SceneNode>();
		for (int i = 0; i < nodes.length; i++)
			filtered.add(nodes[i]);
		
		//Find the redundant children
		for (Iterator<SceneNode> j = filtered.iterator(); j.hasNext();) {
			SceneNode tj = j.next();
			for (int i = 0; i < nodes.length; i++) {
				SceneNode ti = nodes[i];
				if (ti == tj) continue;
				if (tj.isNodeAncestor(ti)) j.remove();
			}
		}
		return filtered;
	}
	
	public void deleteNodes(SceneNode[] nodes) 
	{
		Vector<SceneNode> filtered = filterChildren(nodes);
		for (int i=0; i<filtered.size(); i++) {
			SceneNode t = (SceneNode)filtered.get(i);
			if(t == treeModel.getRoot())
				continue;
			treeModel.removeNodeFromParent(t);
		}
		treeModel.reload();
	}
	
	/**
	 * Groups a set of selected nodes into a new parent. Return
	 * the new parent node.
	 * @param nodes
	 * @param groupName
	 */
	public SceneNode groupNodes(SceneNode[] nodes, String groupName) 
	{	
		Vector<SceneNode> filtered = filterChildren(nodes);
		
		if (filtered.size() == 0) return null;
		
		//Form the new group and add it to the tree
		SceneNode groupNode = new TransformationNode(groupName);
		SceneNode firstSelected = (SceneNode)filtered.get(0);
		SceneNode groupParent = (SceneNode)firstSelected.getParent();
		if (groupParent == null) return null;
		int groupIdx = groupParent.getIndex(firstSelected);
		
		treeModel.insertNodeInto(groupNode, groupParent, groupIdx);
		for (int i = 0; i < filtered.size(); i++) 
		{
			SceneNode node = (SceneNode) filtered.get(i);			
			treeModel.removeNodeFromParent(node);
			treeModel.insertNodeInto(node, groupNode, groupNode.getChildCount());
		}
		treeModel.reload();
		
		return groupNode;
	}
	
	/**
	 * Reparents the items in nodesToReparent underneath
	 * the currently selected node.
	 */
	public void reparent(SceneNode[] nodesToReparent, SceneNode parent) 
	{
		// Invalid children selected?
		for (int i=0; i<nodesToReparent.length; i++) {
			if (parent.isNodeAncestor(nodesToReparent[i])) return;
		}
		
		Vector<SceneNode> filtered = filterChildren(nodesToReparent);
		
		//reparent the filtered children each seperately
		for (int i=0; i<filtered.size(); i++) {
			SceneNode t = (SceneNode)filtered.get(i);
			t.removeFromParent();
			parent.insert(t,0);
		}
		
		treeModel.reload();
	}
	
	public DefaultTreeModel getTreeModel()
	{
		return treeModel;
	}
	
	public SceneNode searchForMeshId(int meshId)
	{		
		return meshIdSearcher.searchForMeshId(meshId, (SceneNode)treeModel.getRoot());
	}
}
