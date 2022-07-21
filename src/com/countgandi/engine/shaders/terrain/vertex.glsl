#version 450 core

in vec3 position;
in vec2 textureCoords;
in vec3 normal;

uniform mat4 transformationMatrix;

out vec3 WorldPos_CS_in;
out vec2 TexCoord_CS_in;
out vec3 Normal_CS_in;
flat out vec3 Flat_Normal_CS_in;

void main() {
    WorldPos_CS_in = (transformationMatrix * vec4(position, 1.0)).xyz;
    TexCoord_CS_in = textureCoords;
    Normal_CS_in = (transformationMatrix * vec4(normal, 0.0)).xyz;
    Flat_Normal_CS_in = Normal_CS_in;
}