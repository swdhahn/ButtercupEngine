#version 450 core

in vec3 WorldPos_FS_in;
in vec2 TexCoord_FS_in;
in vec3 Normal_FS_in;
flat in vec3 Flat_Normal_FS_in;
in float visibility;

layout (location = 0) out vec4 out_Color;
layout (location = 1) out vec4 out_Normal;
layout (location = 2) out vec4 out_Position;

uniform sampler2D diffuse0;
uniform float shineDamper;
uniform float reflectivity;
uniform vec3 skyColor;
uniform float UV;
uniform bool flatNormals;

void main(){
	out_Color = vec4(texture(diffuse0, TexCoord_FS_in * UV).xyz, 1);
	if(flatNormals) {
		out_Normal = vec4(Flat_Normal_FS_in, reflectivity);
	} else {
		out_Normal = vec4(Normal_FS_in, reflectivity);
	}
	out_Position = vec4(WorldPos_FS_in, shineDamper);
}