package pipeline.scene;

import javax.media.opengl.GLAutoDrawable;

import java.io.File;

import pipeline.Pipeline;

/**
 * A Scene object represents a collection of geometry and texture data. A Scene
 * object should know how to render itself into both a GL context, as well as
 * the custom built context of this project framework.
 */
public abstract class Scene {
  /**
   * This method renders the scene onto a GL drawing region.
   * 
   * @param d The reference to the GL drawing area.
   */
  public abstract void render(GLAutoDrawable d);

  /**
   * This method renders the scene onto our custom built pipeline.
   * 
   * @param pipeline The custom pipeline.
   */
  public abstract void render(Pipeline pipeline);
}
