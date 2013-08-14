// Displacement vertex shader

// TODO(P4) Implement the displacement vertex shader

//
// Your global variables here.
//
uniform float time;
varying vec3 normal;
varying vec4 color;
float newY;
vec4 eyespaceVertex;

void main() {		
    //	
	// Your code here.
	//
	//gl_FrontColor = gl_Color;
	color = gl_Color;
	eyespaceVertex = gl_ModelViewMatrix * gl_Vertex;
	eyespaceVertex.y = eyespaceVertex.y + sin(time + eyespaceVertex.x) * cos(time + eyespaceVertex.z);
	gl_Position = gl_ProjectionMatrix * eyespaceVertex;
	normal = normalize(gl_NormalMatrix * gl_Normal);
}
