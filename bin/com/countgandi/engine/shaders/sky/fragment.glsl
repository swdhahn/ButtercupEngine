#version 450 core

in vec2 pass_textureCoords;
in vec3 surfaceNormal;
in vec3 fposition;
in float visibility;

layout (location = 0) out vec4 out_Color;
layout (location = 1) out vec4 out_Normal;
layout (location = 2) out vec4 out_Position;

uniform vec3 skyColor;

void main(void){

	//out_Color = vec4(totalDiffuse, 1.0) * textureColor + vec4(totalSpecular, 1.0);
	//out_Color = mix(vec4(skyColor, 1.0), out_Color, visibility);
	out_Color = vec4(skyColor, 1);
	out_Normal = vec4(0, 0, 0, 0);
	out_Position = vec4(fposition, 0);
}
