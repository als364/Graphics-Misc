package pipeline.vertex;

import javax.vecmath.Color3f;
import javax.vecmath.Vector2f;
import javax.vecmath.Vector3f;

import pipeline.Pipeline;
import pipeline.misc.Vertex;

/**
 * This class is a wrapper for the code that does triangle processing -- or
 * vertex processing, in the more traditional pipeline terminology. Its mission
 * is to transform a triangle into screen space vertex coordinates for the
 * rasterizer and to translate the vertex attributes supplied by the caller into
 * a set of attributes that will provide the necessary information for the
 * fragment processing stage.
 */
public abstract class VertexProcessor {

    /** A list of valid Triangle Processors */
    public static Class[] classes = { ConstColorVP.class, SmoothShadedVP.class,
            FragmentShadedVP.class, };

    /**
     * Returns the compatibility index of this shader. Only a fragment processor
     * with the same compatibility index can be used with this vertex processor.
     * 
     * @return The compatibility index of this vertex processor.
     */
    public abstract int compatibilityIndex();

    /**
     * We can access everything we need to know about the pipeline state -- the
     * current transformation matrices and the lighting parameters -- via the
     * Pipeline reference above. But for efficiency we may want to do some
     * precomputation. This function will be called by the pipeline to notify
     * this object whenever the transforms parameters are changed.
     * 
     * @param pipe
     *            The reference to the pipeline object. Can be used to determine
     *            the necessary matrices.
     */
    public abstract void updateTransforms(Pipeline pipe);

    /**
     * We can access everything we need to know about the pipeline state -- the
     * current transformation matrices and the lighting parameters -- via the
     * Pipeline reference above. But for efficiency we may want to do some
     * precomputation. This function will be called by the pipeline to notify
     * this object whenever the lighting parameters are changed.
     * 
     * @param pipe
     *            The reference to the pipeline object. Cna be used to determine
     *            the viewing conditions.
     */
    public void updateLightModel(Pipeline pipe) {
    }

    /**
     * This is the main function of this class, which is called once for every
     * triangle in the scene. As input we get all the attributes of the
     * triangle's three vertices, and as a result this function should call the
     * rasterizer with appropriate vertex positions and attribute values.
     * 
     * @param v
     *            The three vertices in 3D object coordinates.
     * @param c
     *            Colors associated with each of the vertices.
     * @param n
     *            A normal for each vertex.
     * @param t
     *            Texture coordinates for each vertex.
     * @param output
     *            The array of 3 vertices that serves as the output for this
     *            method.
     */
    public abstract void triangle(Vector3f[] v, Color3f[] c, Vector3f[] n,
            Vector2f[] t, Vertex[] output);

    /**
     * This is the main function of this class, which is called once for every
     * vertex. This routine takes the provided vertex data and prepares a
     * transformed vertex with attributes, ready to be sent to the rasterizer.
     * 
     * @param v
     *            The vertex position in 3D object coordinates.
     * @param c
     *            The color associated with the vertex (null if unused).
     * @param n
     *            The vertex normal (null if unused).
     * @param t
     *            Texture coordinates for each vertex (null if unused).
     * @param output
     *            The processed vertex.
     */
    public abstract void vertex(Vector3f v, Color3f c, Vector3f n, Vector2f t,
            Vertex output);

}