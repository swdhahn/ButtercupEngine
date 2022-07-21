#version 450 core

const int MaxLightsForEachEntity = 1024;

in vec2 textureCoords;

out vec4 out_color;

uniform sampler2D diffuseTexture;
uniform sampler2D normalsTexture;
uniform sampler2D positionTexture;

uniform vec3 fogColor;
uniform float fogDensity;

uniform float time;

layout (std140) uniform CameraData {
	mat4 projectionMatrix;
	mat4 viewMatrix;
	mat4 projectionViewMatrix;
	vec4 cameraLocation;
};

layout (std140) uniform LightData {
	vec4 lightPosition[MaxLightsForEachEntity];
	vec4 lightColor[MaxLightsForEachEntity];
	vec4 attenuation[MaxLightsForEachEntity];
	vec4 lightCount;
};

float mod289(float x);
vec4 mod289(vec4 x);
vec4 perm(vec4 x);
float noise(vec3 p);

void main() {

	vec4 fposition = texture(positionTexture, textureCoords);
	vec4 normals = texture(normalsTexture, textureCoords);
	vec4 textureColor = texture(diffuseTexture, textureCoords);

	vec3 worldPosition = fposition.xyz;

	vec3 unitNormal = normalize(normals.xyz);
	float reflectivity = normals.w;
	float shineDamper = fposition.w;


	vec3 opCam = (inverse(viewMatrix) * vec4(0.0, 0.0, 0.0, 1.0)).xyz;
	vec3 unitVectorToCamera = normalize(opCam - worldPosition);

	vec3 totalDiffuse = vec3(0.0);
	vec3 totalSpecular = vec3(0.0);

	for(int i = 0; i < lightCount.x; i++) {
		vec3 toLightVector = lightPosition[i].xyz - (worldPosition) * min(ceil(attenuation[i].y + attenuation[i].z), 1.0);
		float distance = length(toLightVector);
		float attFactor = attenuation[i].x + ((attenuation[i].y * distance) + (attenuation[i].z * distance * distance));
		vec3 unitLightVector = normalize(toLightVector);
		float nDot1 = dot(unitNormal, unitLightVector);
		float bright = max(nDot1, lightColor[i].w) * lightPosition[i].w;
		vec3 lightDirection = -unitLightVector;
		vec3 reflectedLightDirection = reflect(lightDirection, unitNormal);
		float specularFactor = dot(reflectedLightDirection, unitVectorToCamera);
		specularFactor = max(specularFactor, 0.0);
		float dampedFactor = pow(specularFactor, shineDamper);
		totalDiffuse += (bright * lightColor[i].xyz) / attFactor;
		totalSpecular += (dampedFactor * reflectivity * lightColor[i].xyz) / attFactor;
	}
	
	totalDiffuse = max(totalDiffuse, 0.2);

	vec4 finalColor = vec4(totalDiffuse, 1.0) * textureColor + vec4(totalSpecular, 1.0);
	if(reflectivity + shineDamper == 0) {
		finalColor = textureColor;
	}
	/*vec3 camPos = cameraLocation.xyz;

	float fogDist = distance(worldPosition, cameraLocation.xyz);
	vec3 fogDir = opCam - worldPosition;
	vec3 fogFinalColor = vec3(0);
	for(float i = 0; i < fogDist; i += fogDist / 100.0) { // change this to like .2 or something
		fogFinalColor += noise(normalize(fogDir) * i - camPos + vec3(time, time / 2.0, time)) * fogDensity * fogColor * totalDiffuse;
	}*/

	out_color = finalColor;// + vec4(fogFinalColor, 1.0);

}

float mod289(float x){return x - floor(x * (1.0 / 289.0)) * 289.0;}
vec4 mod289(vec4 x){return x - floor(x * (1.0 / 289.0)) * 289.0;}
vec4 perm(vec4 x){return mod289(((x * 34.0) + 1.0) * x);}

float noise(vec3 p) {
	p /= 4.0;
    vec3 a = floor(p);
    vec3 d = p - a;
    d = d * d * (3.0 - 2.0 * d);

    vec4 b = a.xxyy + vec4(0.0, 1.0, 0.0, 1.0);
    vec4 k1 = perm(b.xyxy);
    vec4 k2 = perm(k1.xyxy + b.zzww);

    vec4 c = k2 + a.zzzz;
    vec4 k3 = perm(c);
    vec4 k4 = perm(c + 1.0);

    vec4 o1 = fract(k3 * (1.0 / 41.0));
    vec4 o2 = fract(k4 * (1.0 / 41.0));

    vec4 o3 = o2 * d.z + o1 * (1.0 - d.z);
    vec2 o4 = o3.yw * d.x + o3.xz * (1.0 - d.x);

    return ((o4.y * d.y + o4.x * (1.0 - d.y)) + 0.2) / 2.0;
}