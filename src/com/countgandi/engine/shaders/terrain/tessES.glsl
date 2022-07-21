#version 450 core

layout(triangles, equal_spacing, ccw) in;

const float density = 0.001;
const float gradient = 2.0;

in vec3 WorldPos_ES_in[];
in vec2 TexCoord_ES_in[];
in vec3 Normal_ES_in[];
flat in vec3 Flat_Normal_ES_in[];

out vec3 WorldPos_FS_in;
out vec2 TexCoord_FS_in;
out vec3 Normal_FS_in;
flat out vec3 Flat_Normal_FS_in;
out float visibility;

uniform mat4 transformationMatrix;
uniform sampler2D heightMap;
uniform float UV;
uniform bool flatNormals;

layout (std140) uniform CameraData {
	mat4 projectionMatrix;
	mat4 viewMatrix;
	mat4 projectionViewMatrix;
	vec4 cameraLocation;
};

const float dispFactor = 1;
const float _texSize = 513;

vec2 interpolate2D(vec2 v0, vec2 v1, vec2 v2) {
    return vec2(gl_TessCoord.x) * v0 + vec2(gl_TessCoord.y) * v1 + vec2(gl_TessCoord.z) * v2;
}

vec3 interpolate3D(vec3 v0, vec3 v1, vec3 v2) {
    return vec3(gl_TessCoord.x) * v0 + vec3(gl_TessCoord.y) * v1 + vec3(gl_TessCoord.z) * v2;
}

vec3 normalsFromHeight(sampler2D heigthTex, vec2 uv) {
    vec4 h;
    h.x = texture(heigthTex, uv + vec2( 0,-1 / _texSize)).r * dispFactor;
    h.y = texture(heigthTex, uv + vec2(-1 / _texSize, 0)).r * dispFactor;
    h.z = texture(heigthTex, uv + vec2( 1 / _texSize, 0)).r * dispFactor;
    h.w = texture(heigthTex, uv + vec2( 0, 1 / _texSize)).r * dispFactor;
    vec3 n;
    n.z = h.w - h.x;
    n.x = h.z - h.y;
    n.y = 1;
    return normalize(n);
}

void main() {
    // Interpolate the attributes of the output vertex using the barycentric coordinates
    TexCoord_FS_in = interpolate2D(TexCoord_ES_in[0], TexCoord_ES_in[1], TexCoord_ES_in[2]);
    if (flatNormals) {
        Flat_Normal_FS_in = interpolate3D(Flat_Normal_ES_in[0], Flat_Normal_ES_in[1], Flat_Normal_ES_in[2]);
        Flat_Normal_FS_in = normalize(Flat_Normal_FS_in);
        WorldPos_FS_in = interpolate3D(WorldPos_ES_in[0], WorldPos_ES_in[1], WorldPos_ES_in[2]);
    
        float Displacement = texture(heightMap, TexCoord_FS_in.xy * UV).x;
        WorldPos_FS_in += Flat_Normal_FS_in * Displacement * dispFactor;
        Flat_Normal_FS_in += normalsFromHeight(heightMap, TexCoord_FS_in.xy * UV);
    } else {
        Normal_FS_in = interpolate3D(Normal_ES_in[0], Normal_ES_in[1], Normal_ES_in[2]);
        Normal_FS_in = normalize(Normal_FS_in);
        WorldPos_FS_in = interpolate3D(WorldPos_ES_in[0], WorldPos_ES_in[1], WorldPos_ES_in[2]);
    
        float Displacement = texture(heightMap, TexCoord_FS_in.xy * UV).x;
        WorldPos_FS_in += Normal_FS_in * Displacement * dispFactor;
        Normal_FS_in += normalsFromHeight(heightMap, TexCoord_FS_in.xy * UV);
    }

    gl_Position = projectionViewMatrix * vec4(WorldPos_FS_in, 1.0);

	float distance = length((viewMatrix * vec4(WorldPos_FS_in, 1.0)).xyz);
	visibility = exp(-pow((distance * density), gradient));
	visibility = clamp(visibility, 0.0, 1.0);
}
