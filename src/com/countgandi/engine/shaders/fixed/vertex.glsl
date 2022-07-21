#version 450 core

const float density = 0.001;
const float gradient = 2.0;

in vec3 position;
in vec2 textureCoords;
in vec3 normal;
in float materialIndex;

out vec2 pass_textureCoords;
out vec3 surfaceNormal;
out float visibility;
out vec3 fposition;
flat out float f_materialIndex;

uniform mat4 transformationMatrix;

uniform float useFakeLighting;

uniform float numberOfRows;
uniform vec2 offset;

uniform vec4 plane;

layout (std140) uniform CameraData {
	mat4 projectionMatrix;
	mat4 viewMatrix;
	mat4 projectionViewMatrix;
	vec4 cameraLocation;
};

void main(void){
	
	vec4 worldPosition = transformationMatrix * vec4(position, 1.0);
	fposition = (viewMatrix * worldPosition).xyz;
	
	gl_ClipDistance[0] = dot(worldPosition, plane);
	
	vec4 positionRelativeToCam = viewMatrix * worldPosition;
	gl_Position = projectionMatrix * positionRelativeToCam;
	pass_textureCoords	= (textureCoords / numberOfRows) + offset;
	
	vec3 actualNormal = normal;
	if(useFakeLighting > 0.5) {
		actualNormal = vec3(0.0, 1.0, 0.0);
	}
	
	surfaceNormal = (transformationMatrix * vec4(actualNormal, 0.0)).xyz;
	
	float distance = length(positionRelativeToCam.xyz);
	visibility = exp(-pow((distance * density), gradient));
	visibility = clamp(visibility, 0.0, 1.0);

	f_materialIndex = materialIndex;
	
}