#version 450 core

in vec2 positions;

out vec2 textureCoords;

void main() {

	textureCoords = vec2((positions.x+1.0)/2.0, (positions.y+1.0)/2.0);
	gl_Position = vec4(positions, 0.0, 1.0);

}
