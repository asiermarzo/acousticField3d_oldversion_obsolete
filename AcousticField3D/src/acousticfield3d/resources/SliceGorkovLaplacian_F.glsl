#ifdef GL_ES
  precision highp float;
#endif

#define N_TRANS _N_TRANS_

#define PI 3.1415926535897932384626433832795

uniform float alphaValue;
uniform int colouring;
uniform float minNegColor;
uniform float maxNegColor;
uniform float minPosColor;
uniform float maxPosColor;
uniform vec3 tPos[N_TRANS];
uniform vec3 tNorm[N_TRANS];
uniform vec4 tSpecs[N_TRANS];

varying vec4 wPos;

uniform float gSep; //gradient separation
uniform float kPreToVel; //(1/(roh*omega)) | notice that the i is not present
uniform float kPre; //f_1_bruus*0.5*kapa*0.5
uniform float kVel; //f_2_bruus*(3/4)*roh*0.5
uniform float pVol; //(4/3)*pi*(r_p^3);

//TEMPLATE GORKOVL
//INCLUDE Colouring.glsl

void main()
{
    vec3 w = vec3(wPos);
   
    float lGorkov =  laplacianGorkovAt(w) / 1000000.0;
    

    gl_FragColor = vec4(colorFunc(lGorkov), alphaValue);
}

