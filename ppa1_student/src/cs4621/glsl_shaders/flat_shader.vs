// Flat vertex shader example

// Transformation matrix (must be set from the OpenGL app)
uniform mat4 mvpMatrix;

void main() {        
    gl_FrontColor = gl_Color;
    gl_Position = mvpMatrix * gl_Vertex;
}
