#version 450 core

// define the number of CPs in the output patch
layout (vertices = 32) out;

layout (std140) uniform CameraData {
	mat4 projectionMatrix;
	mat4 viewMatrix;
	mat4 projectionViewMatrix;
	vec4 cameraLocation;
};

// attributes of the input CPs
in vec3 WorldPos_CS_in[];
in vec2 TexCoord_CS_in[];
in vec3 Normal_CS_in[];
flat in vec3 Flat_Normal_CS_in[];

// attributes of the output CPs
out vec3 WorldPos_ES_in[];
out vec2 TexCoord_ES_in[];
out vec3 Normal_ES_in[];
flat out vec3 Flat_Normal_ES_in[];

// y - y1 = slope(x-x1)   slope = x

float getEquation(float x, float x1, float x2, float y1, float y2) {
	return (y1-y2)/(x1-x2) * (x - x1) + y1;
}

float GetTessLevel(float Distance0, float Distance1) {
    float d = (Distance0 + Distance1) / 2.0;

	if(d > 2000) return 0;
	if(d > 600) return 1;
    //if(d > 400) return getEquation(d, 6, 1, 400, 600);
    //if(d > 200) return getEquation(d, 20, 6, 200, 400);
    //if(d > 140) return getEquation(d, 40, 20, 140, 200);
    //if(d > 100) return getEquation(d, 80, 40, 100, 140);

    return 120;
}

void main() {
    // Set the control points of the output patch
    TexCoord_ES_in[gl_InvocationID] = TexCoord_CS_in[gl_InvocationID];
    Normal_ES_in[gl_InvocationID] = Normal_CS_in[gl_InvocationID];
    WorldPos_ES_in[gl_InvocationID] = WorldPos_CS_in[gl_InvocationID];
    Flat_Normal_ES_in[gl_InvocationID] = Flat_Normal_CS_in[gl_InvocationID];
    // Calculate the distance from the camera to the three control points
    float EyeToVertexDistance0 = distance(cameraLocation.xyz, WorldPos_ES_in[0]);
    float EyeToVertexDistance1 = distance(cameraLocation.xyz, WorldPos_ES_in[1]);
    float EyeToVertexDistance2 = distance(cameraLocation.xyz, WorldPos_ES_in[2]);

    // Calculate the tessellation levels
    gl_TessLevelOuter[0] = GetTessLevel(EyeToVertexDistance1, EyeToVertexDistance2);
    gl_TessLevelOuter[1] = GetTessLevel(EyeToVertexDistance2, EyeToVertexDistance0);
    gl_TessLevelOuter[2] = GetTessLevel(EyeToVertexDistance0, EyeToVertexDistance1);
    gl_TessLevelInner[0] = gl_TessLevelOuter[2];
}