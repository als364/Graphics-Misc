package pipeline.fragment;

import pipeline.misc.Fragment;
import pipeline.misc.FrameBuffer;

/**
 * This fragment processor will place the indicated color into the framebuffer
 * only if the fragment passes the z buffer test (ie - it isn't occluded by
 * another fragment).
 * 
 * @author ags
 */
public class ColorZBufferFP extends FragmentProcessor {
    public int compatibilityIndex() {
        return 3;
    }

    public void fragment(Fragment f, FrameBuffer fb) {
        // TODO : Update the color and Z value of fragment specified by f in the
        // framebuffer.

        // Depth test
    	if(f.depth >= fb.getZ(f.x, f.y)) {
            return;
        }
    	// Sets the color (r, g, b) and z value for a given (x, y) location.
        fb.set(f.x, f.y, Math.min(f.c.x, 1.0f), Math.min(f.c.y, 1.0f), Math.min(f.c.z, 1.0f), f.depth);
    }
}
