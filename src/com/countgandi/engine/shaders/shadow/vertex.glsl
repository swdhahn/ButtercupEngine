#version 450 core

in vec3 position;
in vec2 textureCoords;

out vec2 FS_in_textureCoords;

uniform mat4 projectionViewMatrix;
uniform mat4 transformationMatrix;

void main(void){
	
	gl_Position = projectionViewMatrix * transformationMatrix * vec4(position, 1.0);
	FS_in_textureCoords = textureCoords;
	
}