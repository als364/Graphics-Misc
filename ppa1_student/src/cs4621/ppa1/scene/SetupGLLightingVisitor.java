package cs4621.ppa1.scene;

import javax.media.opengl.GL2;

public class SetupGLLightingVisitor extends AbstractGLVisitor
{
	public SetupGLLightingVisitor()
	{
		// NOP
	}
	
	public void setupLighting(GL2 gl, SceneNode node)
	{
		GLLightManager.startSettingUpLighting(gl);
		visit(gl, node);
	}
	
	public void process(LightNode light)
	{				
		if (GLLightManager.hasNextLight())
		{
			int lightId = GLLightManager.getNextLightId();
			
			gl.glEnable(lightId);
			gl.glLightfv(lightId, GL2.GL_AMBIENT, light.ambient, 0);
			gl.glLightfv(lightId, GL2.GL_DIFFUSE, light.diffuse, 0);
			gl.glLightfv(lightId, GL2.GL_SPECULAR, light.specular, 0);
			gl.glLightfv(lightId, GL2.GL_POSITION, light.position, 0);
		}				
	}
}
