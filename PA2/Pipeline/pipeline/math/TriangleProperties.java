package pipeline.math;

public class TriangleProperties 
{   
    public float x0;
    public float y0;
    public float x1;
    public float y1;
    public float x2;
    public float y2;

    // Add any necessary state that will be shared between the cull test and the increment calculations.
    float dx1;
    float dx2;
    float dy1;
    float dy2;
    public float det;
    
    /**
     * Initializes Triangle Properties.
     * @param x0 x coordinate of point 0 in screen coordinates
     * @param y0 y coordinate of point 0 in screen coordinates
     * @param x1 x coordinate of point 1 in screen coordinates
     * @param y1 y coordinate of point 1 in screen coordinates
     * @param x2 x coordinate of point 2 in screen coordinates
     * @param y2 y coordinate of point 2 in screen coordinates
     */
    public TriangleProperties(float x0, float y0, float x1, float y1, float x2, float y2)
    {
        this.x0 = x0;
        this.y0 = y0;
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
    }
    
    public boolean initializeAndCullTest()
    {
        // Calculate the Cull Test and store extra state to calculate the increments.
        dx1 = x1 - x0;
        dx2 = x2 - x0;
        dy1 = y1 - y0;
        dy2 = y2 - y0;
        det = dx1 * dy2 - dx2 * dy1;
        return (det < 0);
    }
    

    /**
     * This method calculates the increments of x and y for each pixel.
     * @param v value of point 0 in screen coordinates
     * @param v value of point 1 in screen coordinates
     * @param v value of point 2 in screen coordinates
     * @param output
     */
    public void calculateInc(float v0, float v1, float v2, float[] output)
    {
        // Calculate the xIncrements and yIncrements per pixel based on the values 
        float dv1 = v1 - v0;
        float dv2 = v2 - v0;
        output[0] = (dv1 * dy2 - dv2 * dy1) / det;
        output[1] = (dv2 * dx1 - dv1 * dx2) / det;
    }
}
