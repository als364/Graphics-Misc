package pipeline.fragment;

import pipeline.Pipeline;
import pipeline.misc.Fragment;
import pipeline.misc.FrameBuffer;

/**
 * A fragment processor describes what happens in order to render fragments of a
 * triangle on screen. Things that happen here include shading calculations, z
 * buffering, texturing, etc.
 * 
 * @author ags
 */
public abstract class FragmentProcessor {
	
	/** A list of valid Fragment Processors */
	static public Class[] classes = {
		TrivialColorFP.class,
		ColorZBufferFP.class,
		PhongShadedFP.class,
		};
	
	/**
	 * Returns the compatibility index of this shader.  Only a vertex processor 
	 * with the same compatibility index can be used with this fragment processor.
	 * 
	 * @return The compatibility index of this fragment processor.
	 */
	public abstract int compatibilityIndex();
	
	/**
	 * This is the main function of this class, which is called once for every
	 * fragment in the scene. As input we get all the fragment. This method should
	 * update the values in the frame buffer using FrameBuffer.set(int, int,
	 * float, float, float, float). The two ints are the (x, y) coordinate on
	 * screen. The next three floats form the (r, g, b) triple. The last float is
	 * the z value of the fragment (for use in z-buffering).
	 * 
	 * @param f The fragment to render.
	 * @param fb The framebuffer in which to render the fragment.
	 */
	public abstract void fragment(Fragment f, FrameBuffer fb);
	
	/**
	 * We can access everything we need to know about the pipeline state -- the
	 * current transformation matrices and the lighting parameters -- via the
	 * Pipeline reference above. But for efficiency we may want to do some
	 * precomputation. This function will be called by the pipeline to notify this
	 * object whenever the lighting parameters are changed.
	 * 
	 * @param pipe The reference to the pipeline object. Cna be used to determine
	 *          the viewing conditions.
	 */
	public void updateLightModel(Pipeline pipe) { }
}