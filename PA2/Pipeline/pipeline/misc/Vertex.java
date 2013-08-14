package pipeline.misc;

import javax.vecmath.Color3f;
import javax.vecmath.Vector2f;
import javax.vecmath.Vector3f;
import javax.vecmath.Vector4f;

/**
 * @author Beowulf
 */
public class Vertex {

  /** The 4D homogenous coordinate location. */
  public Vector4f v = new Vector4f();
  public Vector3f n = new Vector3f();
  public Color3f c = new Color3f();
  public Vector3f vEye = new Vector3f(); // Vector in eye space
  
}
