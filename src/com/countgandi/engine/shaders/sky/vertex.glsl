#version 450 core


in vec2 positions;

out vec2 pass_textureCoords;
out vec3 fposition;

layout (std140) uniform CameraData {
	mat4 projectionMatrix;
	mat4 viewMatrix;
	mat4 projectionViewMatrix;
	vec4 cameraLocation;
};

void main(void){
	
	pass_textureCoords = vec2((positions.x+1.0)/2.0, (positions.y+1.0)/2.0);
	gl_Position = vec4(positions, 0, 1.0);
	fposition = vec3(positions, -100);
	
	//float distance = length(positionRelativeToCam.xyz);
	//visibility = exp(-pow((distance * density), gradient));
	//visibility = clamp(visibility, 0.0, 1.0);
	
}