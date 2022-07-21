#version 330 core

in vec3 in_positions;
in vec2 in_texCoords;

out vec2 blurTexCoords[11];

uniform float imgWidth;

void main() {

	gl_Position = vec4(in_positions.xy, 0.0, 1.0);
	vec2 centerTexCoords = in_positions.xy * 0.5 + 0.5;
	float pixelSize = 1.0 / imgWidth;

	for(int i = -5; i < 5; i++) {
		blurTexCoords[i+5] = centerTexCoords + vec2(pixelSize * i, 0.0);
	}

}
