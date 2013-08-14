// Displacement fragment shader

// TODO(P4) Implement the displacement fragment shader.

// Control the light in the scene
uniform int enable_lights;

//
// Your global variables here.
//
varying vec4 color;
varying vec4 position;
varying vec3 normal;

void main() 
{
	if(enable_lights == 1)
	{
		vec4 diffuse; 
		vec3 lightVector = gl_LightSource[0].position.xyz;
		normal = normalize(normal);     
		lightVector = -normalize(lightVector);           
		float max = max(0.0, dot(normal, lightVector));  
		//diffuse = max;  
		gl_FragColor = max * color;
		//gl_FragColor = vec4(0, 1, 0, 1);
	}
	else
	{
		gl_FragColor = color;
	}
}