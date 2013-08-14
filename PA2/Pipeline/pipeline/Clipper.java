package pipeline;

import pipeline.misc.Vertex;

/**
 * @author Beowulf
 */
public class Clipper {
    
	/** Temporaries for clipping */
	protected Vertex[] fClip = { new Vertex(), new Vertex(), new Vertex() };
	
	/**
	 * Initializes a new clipper with a given number of attributes.
	 * 
	 * @param newNa The number of attributes.
	 */
	public Clipper() {
	}
	
	/**
	 * The interface for the clipper. Each triangle will be clipped against the
	 * near plane, resulting in either 0, 1, or 2 triangles. The number of
	 * triangles will be returned, and the resulting vertices will be stored into
	 * fOut1 if only one triangle results, or in both fOut1 and fOut2 if two triangles
	 * result.
	 * 
	 * @param f The vertices of the triangle to be clipped.
	 * @param fOut1 The vertices of the first resulting triangle, if any.
	 * @param fOut2 The vertices of the second resulting triangle, if any.
	 * @return The number of resulting triangles.
	 */
	public int clip(Vertex[] f, Vertex[] fOut1, Vertex[] fOut2) {
		
		// Clip the triangle against the near plane, which is z == 0 in homogeneous
		// screen space.
		
		int code = ((f[0].v.z > 0) ? 1 : 0) | ((f[1].v.z > 0) ? 2 : 0) | ((f[2].v.z > 0) ? 4 : 0);
		
		if (code == 0) // all three out
			return 0;
		
		else if (code == 1 || code == 2 || code == 4) { // one in, two out
			int kIn, kOut1, kOut2;
			
			if (code == 1) { // only v[0] in
				kIn = 0;
				kOut1 = 1;
				kOut2 = 2;
			}
			else if (code == 2) { // only v[1] in
				kIn = 1;
				kOut1 = 2;
				kOut2 = 0;
			}
			else if (code == 4) { // only v[2] in
				kIn = 2;
				kOut1 = 0;
				kOut2 = 1;
			}
			else { // error
				return -1;
			}
			
			float a1 = -f[kIn].v.z / (f[kOut1].v.z - f[kIn].v.z);
			float a2 = -f[kIn].v.z / (f[kOut2].v.z - f[kIn].v.z);
			
			fClip[kIn].v.set(f[kIn].v);
			fClip[kOut1].v.set((1 - a1) * f[kIn].v.x + a1 * f[kOut1].v.x, (1 - a1) * f[kIn].v.y + a1 * f[kOut1].v.y, 0.0f, (1 - a1) * f[kIn].v.w + a1 * f[kOut1].v.w);
			fClip[kOut2].v.set((1 - a2) * f[kIn].v.x + a2 * f[kOut2].v.x, (1 - a2) * f[kIn].v.y + a2 * f[kOut2].v.y, 0.0f, (1 - a2) * f[kIn].v.w + a2 * f[kOut2].v.w);
			fClip[kIn].c.set(f[kIn].c);
			fClip[kOut1].c.set((1 - a1) * f[kIn].c.x + a1 * f[kOut1].c.x, (1 - a1) * f[kIn].c.y + a1 * f[kOut1].c.y, (1 - a1) * f[kIn].c.z + a1 * f[kOut1].c.z);
			fClip[kOut2].c.set((1 - a2) * f[kIn].c.x + a2 * f[kOut2].c.x, (1 - a2) * f[kIn].c.y + a2 * f[kOut2].c.y, (1 - a2) * f[kIn].c.z + a2 * f[kOut2].c.z);
			fClip[kIn].n.set(f[kIn].n);
			fClip[kOut1].n.set((1 - a1) * f[kIn].n.x + a1 * f[kOut1].n.x, (1 - a1) * f[kIn].n.y + a1 * f[kOut1].n.y, (1 - a1) * f[kIn].n.z + a1 * f[kOut1].n.z);
			fClip[kOut2].n.set((1 - a2) * f[kIn].n.x + a2 * f[kOut2].n.x, (1 - a2) * f[kIn].n.y + a2 * f[kOut2].n.y, (1 - a2) * f[kIn].n.z + a2 * f[kOut2].n.z);
			fClip[kIn].vEye.set(f[kIn].vEye);
			fClip[kOut1].vEye.set((1 - a1) * f[kIn].vEye.x + a1 * f[kOut1].vEye.x, (1 - a1) * f[kIn].vEye.y + a1 * f[kOut1].vEye.y, (1 - a1) * f[kIn].vEye.z + a1 * f[kOut1].vEye.z);
			fClip[kOut2].vEye.set((1 - a2) * f[kIn].vEye.x + a2 * f[kOut2].vEye.x, (1 - a2) * f[kIn].vEye.y + a2 * f[kOut2].vEye.y, (1 - a2) * f[kIn].vEye.z + a2 * f[kOut2].vEye.z);
			
			fOut1[kIn].v.set(fClip[kIn].v);
			fOut1[kOut1].v.set(fClip[kOut1].v);
			fOut1[kOut2].v.set(fClip[kOut2].v);
			fOut1[kIn].c.set(fClip[kIn].c);
			fOut1[kOut1].c.set(fClip[kOut1].c);
			fOut1[kOut2].c.set(fClip[kOut2].c);
			fOut1[kIn].n.set(fClip[kIn].n);
			fOut1[kOut1].n.set(fClip[kOut1].n);
			fOut1[kOut2].n.set(fClip[kOut2].n);
			fOut1[kIn].vEye.set(fClip[kIn].vEye);
			fOut1[kOut1].vEye.set(fClip[kOut1].vEye);
			fOut1[kOut2].vEye.set(fClip[kOut2].vEye);
			
			return 1;
		}
		
		else if (code == 6 || code == 5 || code == 3) { // two in, one out
			int kOut, kIn1, kIn2;
			if (code == 6) { // only v[0] out
				kOut = 0;
				kIn1 = 1;
				kIn2 = 2;
			}
			else if (code == 5) { // only v[1] out
				kOut = 1;
				kIn1 = 2;
				kIn2 = 0;
			}
			else if (code == 3) { // only v[2] out
				kOut = 2;
				kIn1 = 0;
				kIn2 = 1;
			}
			else { // error
				return -1;
			}
			
			float a1 = -f[kOut].v.z / (f[kIn1].v.z - f[kOut].v.z);
			float a2 = -f[kOut].v.z / (f[kIn2].v.z - f[kOut].v.z);
			
			fClip[kOut].v.set((1 - a1) * f[kOut].v.x + a1 * f[kIn1].v.x, (1 - a1) * f[kOut].v.y + a1 * f[kIn1].v.y, 0.0f, (1 - a1) * f[kOut].v.w + a1 * f[kIn1].v.w);
			fClip[kIn1].v.set(f[kIn1].v);
			fClip[kIn2].v.set(f[kIn2].v);
			fClip[kOut].c.set((1 - a1) * f[kOut].c.x + a1 * f[kIn1].c.x, (1 - a1) * f[kOut].c.y + a1 * f[kIn1].c.y, (1 - a1) * f[kOut].c.z + a1 * f[kIn1].c.z);
			fClip[kIn1].c.set(f[kIn1].c);
			fClip[kIn2].c.set(f[kIn2].c);
			fClip[kOut].n.set((1 - a1) * f[kOut].n.x + a1 * f[kIn1].n.x, (1 - a1) * f[kOut].n.y + a1 * f[kIn1].n.y, (1 - a1) * f[kOut].n.z + a1 * f[kIn1].n.z);
			fClip[kIn1].n.set(f[kIn1].n);
			fClip[kIn2].n.set(f[kIn2].n);
			fClip[kOut].vEye.set((1 - a1) * f[kOut].vEye.x + a1 * f[kIn1].vEye.x, (1 - a1) * f[kOut].vEye.y + a1 * f[kIn1].vEye.y, (1 - a1) * f[kOut].vEye.z + a1 * f[kIn1].vEye.z);
			fClip[kIn1].vEye.set(f[kIn1].vEye);
			fClip[kIn2].vEye.set(f[kIn2].vEye);
			
			// Set up the first triangle
			fOut1[kIn1].v.set(fClip[kIn1].v);
			fOut1[kIn2].v.set(fClip[kIn2].v);
			fOut1[kOut].v.set(fClip[kOut].v);
			fOut1[kIn1].c.set(fClip[kIn1].c);
			fOut1[kIn2].c.set(fClip[kIn2].c);
			fOut1[kOut].c.set(fClip[kOut].c);
			fOut1[kIn1].n.set(fClip[kIn1].n);
			fOut1[kIn2].n.set(fClip[kIn2].n);
			fOut1[kOut].n.set(fClip[kOut].n);
			fOut1[kIn1].vEye.set(fClip[kIn1].vEye);
			fOut1[kIn2].vEye.set(fClip[kIn2].vEye);
			fOut1[kOut].vEye.set(fClip[kOut].vEye);
			
			fClip[kOut].v.set((1 - a1) * f[kOut].v.x + a1 * f[kIn1].v.x, (1 - a1) * f[kOut].v.y + a1 * f[kIn1].v.y, 0.0f, (1 - a1) * f[kOut].v.w + a1 * f[kIn1].v.w);
			fClip[kIn1].v.set(f[kIn2].v);
			fClip[kIn2].v.set((1 - a2) * f[kOut].v.x + a2 * f[kIn2].v.x, (1 - a2) * f[kOut].v.y + a2 * f[kIn2].v.y, 0.0f, (1 - a2) * f[kOut].v.w + a2 * f[kIn2].v.w);
			fClip[kOut].c.set((1 - a1) * f[kOut].c.x + a1 * f[kIn1].c.x, (1 - a1) * f[kOut].c.y + a1 * f[kIn1].c.y, (1 - a1) * f[kOut].c.z + a1 * f[kIn1].c.z);
			fClip[kIn1].c.set(f[kIn2].c);
			fClip[kIn2].c.set((1 - a2) * f[kOut].c.x + a2 * f[kIn2].c.x, (1 - a2) * f[kOut].c.y + a2 * f[kIn2].c.y, (1 - a2) * f[kOut].c.z + a2 * f[kIn2].c.z);
			fClip[kOut].n.set((1 - a1) * f[kOut].n.x + a1 * f[kIn1].n.x, (1 - a1) * f[kOut].n.y + a1 * f[kIn1].n.y, (1 - a1) * f[kOut].n.z + a1 * f[kIn1].n.z);
			fClip[kIn1].n.set(f[kIn2].n);
			fClip[kIn2].n.set((1 - a2) * f[kOut].n.x + a2 * f[kIn2].n.x, (1 - a2) * f[kOut].n.y + a2 * f[kIn2].n.y, (1 - a2) * f[kOut].n.z + a2 * f[kIn2].n.z);
			fClip[kOut].vEye.set((1 - a1) * f[kOut].vEye.x + a1 * f[kIn1].vEye.x, (1 - a1) * f[kOut].vEye.y + a1 * f[kIn1].vEye.y, (1 - a1) * f[kOut].vEye.z + a1 * f[kIn1].vEye.z);
			fClip[kIn1].vEye.set(f[kIn2].vEye);
			fClip[kIn2].vEye.set((1 - a2) * f[kOut].vEye.x + a2 * f[kIn2].vEye.x, (1 - a2) * f[kOut].vEye.y + a2 * f[kIn2].vEye.y, (1 - a2) * f[kOut].vEye.z + a2 * f[kIn2].vEye.z);
			
			// Set up the other triangle
			fOut2[kIn1].v.set(fClip[kIn1].v);
			fOut2[kIn2].v.set(fClip[kIn2].v);
			fOut2[kOut].v.set(fClip[kOut].v);
			fOut2[kIn1].c.set(fClip[kIn1].c);
			fOut2[kIn2].c.set(fClip[kIn2].c);
			fOut2[kOut].c.set(fClip[kOut].c);
			fOut2[kIn1].n.set(fClip[kIn1].n);
			fOut2[kIn2].n.set(fClip[kIn2].n);
			fOut2[kOut].n.set(fClip[kOut].n);
			fOut2[kIn1].vEye.set(fClip[kIn1].vEye);
			fOut2[kIn2].vEye.set(fClip[kIn2].vEye);
			fOut2[kOut].vEye.set(fClip[kOut].vEye);
			
			return 2;
		}
		
		else { // code == 7 => all three in
			
			// Copy straight to output
			fOut1[0].v.set(f[0].v);
			fOut1[1].v.set(f[1].v);
			fOut1[2].v.set(f[2].v);
			fOut1[0].c.set(f[0].c);
			fOut1[1].c.set(f[1].c);
			fOut1[2].c.set(f[2].c);
			fOut1[0].n.set(f[0].n);
			fOut1[1].n.set(f[1].n);
			fOut1[2].n.set(f[2].n);
			fOut1[0].vEye.set(f[0].vEye);
			fOut1[1].vEye.set(f[1].vEye);
			fOut1[2].vEye.set(f[2].vEye);
			
			return 1;
		}
	}
	
}
