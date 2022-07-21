#version 450 core

in vec2 pass_textureCoords;
in vec3 surfaceNormal;
in vec3 fposition;
in float visibility;
flat in float f_materialIndex;

layout (location = 0) out vec4 out_Color;
layout (location = 1) out vec4 out_Normal;
layout (location = 2) out vec4 out_Position;

uniform sampler2DArray diffuseTex;
uniform sampler2DArray normalTex;
uniform float shineDamper;
uniform float reflectivity;
uniform vec3 skyColor;

void main(void){
	vec3 texCoords = vec3(pass_textureCoords, f_materialIndex);
	vec4 textureColor = texture(diffuseTex, texCoords);
	
	if(textureColor.a < 0.5) {
		discard;
	}
	
	out_Color = vec4(textureColor.xyz, 1);
	out_Normal = vec4(normalize(surfaceNormal + texture(normalTex, texCoords).xyz), reflectivity);
	out_Position = vec4(fposition, shineDamper);
}
