package pipeline.fragment;

import pipeline.misc.Fragment;
import pipeline.misc.FrameBuffer;

/**
 * This fragment program will render the fragments color into the framebuffer
 * regardless of whether it is in front of an earlier fragment.
 * 
 * @author ags
 */
public class TrivialColorFP extends FragmentProcessor {
    /**
     * @see FragmentProcessor#compatibilityIndex()
     */
    public int compatibilityIndex() {
        return 3;
    }

    /**
     * @see FragmentProcessor#fragment(Fragment, FrameBuffer)
     */
    public void fragment(Fragment f, FrameBuffer fb) {
        fb.set(f.x, f.y, f.c.x, f.c.y, f.c.z, 0);
    }
}
