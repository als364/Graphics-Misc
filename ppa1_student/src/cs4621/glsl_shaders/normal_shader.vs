// Normal vertex shader

// TODO(P4) Implement the normal vertex shader.

//
// Your global variables here.
//
varying vec3 color;
varying vec3 normal;
float r;
float g;
float b;

void main() {
    //
    // Your code here.
    //
    gl_Position = gl_ModelViewProjectionMatrix * gl_Vertex;
    //normal = normalize(gl_NormalMatrix * gl_Normal);
    normal = gl_Normal; 
    r = (normal.x + 1.0) / 2.0;
    g = (normal.y + 1.0) / 2.0;
    b = (normal.z + 1.0) / 2.0;
    //color = gl_Normal.xyz;
    color = vec3(r, g, b);
}
