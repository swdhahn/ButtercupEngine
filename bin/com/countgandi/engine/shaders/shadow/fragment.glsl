#version 450 core

in vec2 FS_in_textureCoords;

out vec4 out_Color;

uniform sampler2D modelTexture;

void main(void){

	vec4 textureColor = texture(modelTexture, FS_in_textureCoords);
	
	if(textureColor.a < 0.5) {
		discard;
	}
	
	out_Color = vec4(1.0);
}
