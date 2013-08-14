// Normal fragment shader

// TODO(P4) Implement the normal fragment shader
 
//
// Your global variables here.
//
varying vec3 color;
void main() {
    //
    // Your code here.
    //
    gl_FragColor = vec4(color, 1.0);
}