// Toon fragment shader

// TODO(P4) Implement the Toon fragment shader.

// Control the light in the scene
uniform int enable_lights;

//
// Your global variables here.
//
varying vec4 color;
varying vec3 normal;

void main() {
    //
	// Your code here.
	//
	
	//gl_FragColor = color * enable_lights;
	
	if(enable_lights == 0)
	{
		//gl_FragColor = vec4(1, 1, 1, 1);
		gl_FragColor = color;
	}
	else
	{
		vec3 lightVector = gl_LightSource[0].position.xyz;
		lightVector = normalize(-lightVector);
		normal = normalize(normal);
		float intensity = dot(normal, lightVector);
		if(intensity < 0)
		{
			intensity = 0;
		}
		else if(intensity < 0.5)
		{
			intensity = 0.25;
		}
		else if(intensity < 0.75)
		{
			intensity = 0.5;
		}
		else
		{
			intensity = 0.75;
		}
		gl_FragColor = intensity * color;
	}
}