#version 450 core

out vec4 out_color;

in vec2 blurTexCoords[11];

uniform sampler2D tex;

void main() {

	out_color = vec4(0.0);
	out_color += texture(tex, blurTexCoords[0]) * 0.0093;
    out_color += texture(tex, blurTexCoords[1]) * 0.028002;
    out_color += texture(tex, blurTexCoords[2]) * 0.065984;
    out_color += texture(tex, blurTexCoords[3]) * 0.121703;
    out_color += texture(tex, blurTexCoords[4]) * 0.175713;
    out_color += texture(tex, blurTexCoords[5]) * 0.198596;
    out_color += texture(tex, blurTexCoords[6]) * 0.175713;
    out_color += texture(tex, blurTexCoords[7]) * 0.121703;
    out_color += texture(tex, blurTexCoords[8]) * 0.065984;
    out_color += texture(tex, blurTexCoords[9]) * 0.028002;
    out_color += texture(tex, blurTexCoords[10]) * 0.0093;

}
