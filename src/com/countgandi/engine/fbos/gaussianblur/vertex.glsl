#version 450 core

in vec3 positions;

out vec2 blurTexCoords[11];

uniform float imgWidthHeight;
uniform vec2 mask;

void main() {

	gl_Position = vec4(positions.xy, 0.0, 1.0);
	vec2 centerTexCoords = positions.xy * 0.5 + 0.5;
	float pixelSize = 1.0 / imgWidthHeight;

	for(int i = -5; i < 5; i++) {
		blurTexCoords[i+5] = centerTexCoords + vec2(pixelSize * i, pixelSize * i) * mask;
	}

}
