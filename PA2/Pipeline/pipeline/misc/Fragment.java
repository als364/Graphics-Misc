package pipeline.misc;

import javax.vecmath.Color3f;
import javax.vecmath.Vector2f;
import javax.vecmath.Vector3f;

/**
 * @author Beowulf
 */
public class Fragment {

  /** The screen space x coordinate of this fragment. */
  public int x;

  /** The screen space y coordinate of this fragment. */
  public int y;
  
  /** the depth coordinate */
  public float depth;

  /** The user-interpolated normal */
  public Vector3f n = new Vector3f();
  
  /** The user-interpolated color */
  public Color3f c = new Color3f();
  
  /** The user-interpolated texture coordinate */
  public Vector2f t = new Vector2f();
  
  /** The user-interpolated eye-space position.  Use this to do lighting calculations in the fragment shader */
  public Vector3f vEye = new Vector3f();
  
  /** The attributes associated with this fragment. */
//  public float[] attrs;

  /**
   * Creates an empty fragment
   */
  public Fragment() {
//	  attrs = new float[na];
  }

}
