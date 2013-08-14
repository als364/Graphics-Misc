package pipeline;

import javax.vecmath.Vector4f;

import pipeline.fragment.FragmentProcessor;
import pipeline.math.TriangleProperties;
import pipeline.misc.Fragment;
import pipeline.misc.FrameBuffer;
import pipeline.misc.Vertex;

/**
 * This class is responsible for interpolating the attributes given to it across
 * the triangle, and handing off the correctly interpolated values to the
 * fragment processor. Clipping also happens within this class.
 * 
 * @author ags
 */
public class Rasterizer {
	
	/** Number of user-supplied attributes */
//	protected int na;
	
	/** Width of the image */
	protected int nx;
	
	/** Height of the image */
	protected int ny;
	
	
	// All the following arrays are preallocated for efficiency.
	
	/** Vertex data for triangle setup */
	protected float[][] vData;
	
	/** State arrays for rasterization.
	  * Data is [e0, e1, e2, z', a0/w, a1/w, ..., 1/w]. */
	protected float[] xInc;
	protected float[] yInc;
	protected float[] rowData;
	protected float[] pixData;

	/** A pre-allocated fragment */
	Fragment frag;
	
	/** Scratch space for post-perspective vertex positions */
	Vector4f[] posn = { new Vector4f(), new Vector4f(), new Vector4f() };
	
	
	/**
	 * The only constructor.
	 * 
	 * @param newNa The number of user defined attributes.
	 * @param newNx The width of the image.
	 * @param newNy The height of the image.
	 */
	public Rasterizer(int newNx, int newNy) {
		
//		na = newNa;
		nx = newNx;
		ny = newNy;
		
		vData = new float[3][16];
		xInc = new float[16];
		yInc = new float[16];
		rowData = new float[16];
		pixData = new float[16];
		
		frag = new Fragment();
	}
	
	
	protected void rasterize(Vertex[] vs, FragmentProcessor fp, FrameBuffer fb) {
		
		// Assemble the vertex data.  Entries 0--2 are barycentric
		// coordinates; entry 3 is the screen-space depth; entry 
		// 4 through 6 is the color; entry 7 through 9 is the normal,
		// entry 10 through 11 is the texture, and entry 12 through 14
		// is the eye space position.
		// entries 15 through 15 + (na-1) are the attributes provided in the
		// vertices; and entry 15 + na is the inverse w coordinate.
		// The caller-provided attributes are all interpolated with
		// perspective correction.
		for (int iv = 0; iv < 3; iv++) {
			float invW = 1.0f / vs[iv].v.w;
			posn[iv].scale(invW, vs[iv].v);
			for (int k = 0; k < 3; k++)
				vData[iv][k] = (k == iv ? 1 : 0);
			vData[iv][3] = posn[iv].z;
			vData[iv][4] = invW * vs[iv].c.x;
			vData[iv][5] = invW * vs[iv].c.y;
			vData[iv][6] = invW * vs[iv].c.z;
			vData[iv][7] = invW * vs[iv].n.x;
			vData[iv][8] = invW * vs[iv].n.y;
			vData[iv][9] = invW * vs[iv].n.z;
			//vData[iv][10] = invW * vs[iv].t.x;
			//vData[iv][11] = invW * vs[iv].t.y;
			vData[iv][10] = vData[iv][11] = 0.0f;
			vData[iv][12] = invW * vs[iv].vEye.x;
			vData[iv][13] = invW * vs[iv].vEye.y;
			vData[iv][14] = invW * vs[iv].vEye.z;
//			for (int ia = 0; ia < na; ia++)
//				vData[iv][15 + ia] = invW * vs[iv].attrs[ia];
			vData[iv][15] = invW;
		}
		
		// Compute the bounding box of the triangle; bail out if it is empty.
		int ixMin = Math.max(0, ceil(min(posn[0].x, posn[1].x, posn[2].x)));
		int ixMax = Math.min(nx - 1, floor(max(posn[0].x, posn[1].x, posn[2].x)));
		int iyMin = Math.max(0, ceil(min(posn[0].y, posn[1].y, posn[2].y)));
		int iyMax = Math.min(ny - 1, floor(max(posn[0].y, posn[1].y, posn[2].y)));
		if (ixMin > ixMax || iyMin > iyMax)
			return;
		
		// Compute the determinant for triangle setup.  If it is negative, the
		// triangle is back-facing and we cull it.		
		TriangleProperties triProp = new TriangleProperties(posn[0].x, posn[0].y, posn[1].x, posn[1].y, posn[2].x, posn[2].y); 
		if (triProp.initializeAndCullTest())
		{
			return;
		}
		
		float[] incOutput = new float[2];
		// Triangle setup: compute the initial values and the x and y increments
		// for each attribute.
		for (int k = 0; k < 16; k++) {
			triProp.calculateInc(vData[0][k], vData[1][k], vData[2][k], incOutput);
			xInc[k] = incOutput[0];
			yInc[k] = incOutput[1];
			
			rowData[k] = vData[0][k] + (ixMin - posn[0].x) * xInc[k] + (iyMin - posn[0].y) * yInc[k];
		}
		
		// Rasterize: loop over the bounding box, updating the attribute values.
		// For each pixel where the barycentric coordinates are in range, emit 
		// a fragment.  In our case this means calling the fragment processor to
		// process it immediately.
		for (frag.y = iyMin; frag.y <= iyMax; frag.y++) {
			for (int k = 0; k < 16; k++)
				pixData[k] = rowData[k];
			for (frag.x = ixMin; frag.x <= ixMax; frag.x++) {
				if (pixData[0] >= 0 && pixData[1] >= 0 && pixData[2] >= 0) {
					frag.depth = pixData[3];
					float w = 1.0f / pixData[15];
					frag.c.x = pixData[4] * w;
					frag.c.y = pixData[5] * w;
					frag.c.z = pixData[6] * w;
					frag.n.x = pixData[7] * w;
					frag.n.y = pixData[8] * w;
					frag.n.z = pixData[9] * w;
					frag.t.x = pixData[10] * w;
					frag.t.y = pixData[11] * w;
					frag.vEye.x = pixData[12] * w;
					frag.vEye.y = pixData[13] * w;
					frag.vEye.z = pixData[14] * w;
//					for (int ia = 0; ia < na; ia++)
//						frag.attrs[ia] = pixData[15 + ia] * w;
					fp.fragment(frag, fb);
				}
				for (int k = 0; k < 16; k++)
					pixData[k] += xInc[k];
			}
			for (int k = 0; k < 16; k++)
				rowData[k] += yInc[k];
		}
	}
	
	// Utility routines for clarity
	
	protected static int ceil(float x) {
		
		return (int) Math.ceil(x);
	}
	
	protected static int floor(float x) {
		
		return (int) Math.floor(x);
	}
	
	protected static float min(float a, float b, float c) {
		
		return Math.min(Math.min(a, b), c);
	}
	
	protected static float max(float a, float b, float c) {
		
		return Math.max(Math.max(a, b), c);
	}
	
}
