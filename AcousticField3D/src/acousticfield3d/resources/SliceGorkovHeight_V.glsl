#ifdef GL_ES
  precision highp float;
#endif
  
attribute vec4 vertexPosition;

varying vec4 normal;
varying vec4 wPos;
varying vec4 color;

uniform mat4 modelViewProjectionMatrix;
uniform mat4 modelMatrix;

#define N_TRANS _N_TRANS_

#define PI 3.1415926535897932384626433832795

uniform float alphaValue;
uniform int colouring;
uniform float heightGain;
uniform float heightDiv;
uniform float minNegColor;
uniform float maxNegColor;
uniform float minPosColor;
uniform float maxPosColor;
uniform vec3 tPos[N_TRANS];
uniform vec3 tNorm[N_TRANS];
uniform vec4 tSpecs[N_TRANS];

uniform float gSep; //gradient separation
uniform float kPreToVel; //(1/(roh*omega)) | notice that the i is not present
uniform float kPre; //f_1_bruus*0.5*kapa*0.5
uniform float kVel; //f_2_bruus*(3/4)*roh*0.5
uniform float pVol; //(4/3)*pi*(r_p^3);

//TEMPLATE GORKOV
//INCLUDE Colouring.glsl

vec3 getNormalAt(vec3 px0, vec3 px1, vec3 py0, vec3 py1){
    
    float x = (gorkovAt(px0) - gorkovAt(px1)) * heightGain;
    float y = (gorkovAt(py0) - gorkovAt(py1)) * heightGain;

    vec3 vx = normalize ( vec3( distance(px0, px1) , 0 , x) );
    vec3 vy = normalize ( vec3(0.0, distance(py0, py1), y) );
    return cross(vx,vy);
}

void main()
{
    wPos = modelMatrix * vertexPosition;
    vec3 w = vec3(wPos);

    float gorkov =  gorkovAt(w);
    float divL = 1.0 / heightDiv;

    vec3 px0 = vec3( modelMatrix * (vertexPosition + vec4(-divL, 0.0, 0.0, 0.0)) );
    vec3 px1 = vec3( modelMatrix * (vertexPosition + vec4(divL,  0.0, 0.0, 0.0)) );
    vec3 py0 = vec3( modelMatrix * (vertexPosition + vec4(0.0, -divL, 0.0, 0.0)) );
    vec3 py1 = vec3( modelMatrix * (vertexPosition + vec4(0.0, divL, 0.0, 0.0)) );

    vec3 gorkovN = getNormalAt(px0, px1, py0, py1);

    vec4 modVertexPosition = vertexPosition;
    modVertexPosition.z = gorkov * heightGain;

    gl_Position = modelViewProjectionMatrix * modVertexPosition;
    normal = modelMatrix * vec4( gorkovN , 0.0);
    

    color = vec4( colorFunc(gorkov) , alphaValue);    
}