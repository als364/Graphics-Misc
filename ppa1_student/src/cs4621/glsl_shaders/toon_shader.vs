// Toon vertex shader

// TODO(P4) Implement the Toon vertex shader, described in the problem statement.

//
// Your global variables here.
//

varying vec3 normal;
varying vec4 color;

void main() {
    //
	// Your code here.
	//
	normal = normalize(gl_NormalMatrix * gl_Normal);
	color = gl_Color;
	gl_Position = gl_ModelViewProjectionMatrix * gl_Vertex;
}
