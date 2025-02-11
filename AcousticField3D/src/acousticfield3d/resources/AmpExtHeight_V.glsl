#ifdef GL_ES
  precision highp float;
#endif
  
attribute vec4 vertexPosition;

varying vec4 normal;
varying vec4 wPos;
varying vec4 color;

uniform mat4 modelViewProjectionMatrix;
uniform mat4 modelMatrix;


#define PI 3.1415926535897932384626433832795

uniform float alphaValue;
uniform int colouring;
uniform float heightGain;
uniform float heightDiv;
uniform float minNegColor;
uniform float maxNegColor;
uniform float minPosColor;
uniform float maxPosColor;

uniform float exGain;
uniform float exOffset;
uniform sampler2D text;

//INCLUDE Colouring.glsl

vec3 getNormalAt(vec2 px0, vec2 px1, vec2 py0, vec2 py1){
    
    float x = (length( texture(text, px0).xy ) - length( texture(text, px1).xy )) * heightGain;
    float y = (length( texture(text, py0).xy ) - length( texture(text, py1).xy )) * heightGain;

    vec3 vx = normalize ( vec3( distance(px0, px1) , 0 , x) );
    vec3 vy = normalize ( vec3(0.0, distance(py0, py1), y) );
    return cross(vx,vy);
}

void main()
{
    wPos = modelMatrix * vertexPosition;
   
    const vec2 tPos = vertexPosition.xy + 0.5;
    vec2 field = texture(text, tPos).xy; //text
    float amplitude =  length( field );

    float divL = 1.0 / heightDiv;

    vec2 px0 = tPos + vec2(-divL, 0.0);
    vec2 px1 = tPos + vec2( divL, 0.0);
    vec2 py0 = tPos + vec2(0.0, -divL);
    vec2 py1 = tPos + vec2(0.0,  divL);

    vec3 amplitudeNormal = getNormalAt(px0, px1, py0, py1);
    
    vec4 modVertexPosition = vertexPosition;
    modVertexPosition.z = amplitude * heightGain;

    gl_Position = modelViewProjectionMatrix * modVertexPosition;
    normal = modelMatrix * vec4(amplitudeNormal , 0.0);
   
    color = vec4( colorFunc(amplitude) , alphaValue);    
}