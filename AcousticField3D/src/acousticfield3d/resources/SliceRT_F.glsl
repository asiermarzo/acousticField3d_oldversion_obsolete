#ifdef GL_ES
  precision highp float;
#endif

#define N_TRANS _N_TRANS_

_USE_AMP_ #define USE_AMP 1
_USE_PHASE_ #define USE_PHASE 1
_USE_AMPPHASE_ #define USE_AMPPHASE 1
_USE_TAMP_ #define USE_TAMP 1
_USE_TAMPDIFFFR_ #define USE_TAMPDIFFFR 1

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

uniform float timeBottomMin;
uniform float timeBottomMax;
uniform float timeTopMin;
uniform float timeTopMax;
uniform float time;

varying vec4 wPos;

vec3 hsv2rgb(vec3 c){
    vec4 K = vec4(1.0, 2.0 / 3.0, 1.0 / 3.0, 3.0);
    vec3 p = abs(fract(c.xxx + K.xyz) * 6.0 - K.www);
    return c.z * mix(K.xxx, clamp(p - K.xxx, 0.0, 1.0), c.y);
}

vec3 rainbowSin(float c){
    vec3 tmp = vec3(0.0, 2.0*PI/3.0, 4.0*PI/3.0) + c; //rainbow sin
    return ( sin(tmp)+1.0 ) / 2.0;
}

//TEMPLATE FIELD
//INCLUDE Colouring.glsl

void main()
{
    vec2 field = fieldAt(wPos.xyz);
    
#ifdef USE_AMP //Amplitude
    float value =length(field);
    gl_FragColor = vec4(colorFunc(value),  alphaValue);
#endif

#ifdef USE_TAMP //Amplitude at an instant of time
    float value = 0.0;
    float phase = atan(field.y, field.x);
    
    if( (phase > timeBottomMin && phase < timeBottomMax) ||
        (phase > timeTopMin && phase < timeTopMax)){
        value = length(field);
    }
    
    gl_FragColor = vec4(colorFunc(value),  alphaValue);
#endif

#ifdef USE_PHASE //Phase
     //float phase = atan(field.y, field.x);
     //vec3 rgb = rainbowSin(phase);
       
     float phase = clamp( (atan(field.y, field.x) / PI + 1.0) / 2.0, 0, 1);
     vec3 rgb = hsv2rgb(vec3(phase,1.0,1.0));
     
    gl_FragColor = vec4( rgb , alphaValue);

#endif 

#ifdef USE_TAMPDIFFFR //Instant amplitude different 
//sorry about the previous unnecesary  call to fieldAt    
    float value = 0.0;
    const vec3 point = wPos.xyz;

    for(int i = 0; i < N_TRANS; ++i){ //try loop unroll
        const vec3 diffVec = point - tPos[i];
        
        const float dist = length(diffVec);
        
        const float ampDirAtt = tSpecs[i].y / dist;
        const float kdPlusPhasePlusOmegaTime = tSpecs[i].x * dist + tSpecs[i].z + tSpecs[i].w;
        
        value += ampDirAtt * cos(kdPlusPhasePlusOmegaTime);
    }

    gl_FragColor = vec4(colorFunc(value),  alphaValue);
#endif 

#ifdef USE_AMPPHASE //Amp + Phase
    float amplitude = clamp( (length(field)-minPosColor) / (maxPosColor-minPosColor), 0, 1);
    float phase = clamp( (atan(field.y, field.x) / PI + 1.0) / 2.0, 0, 1);
    vec3 rgb = hsv2rgb(vec3(phase,1,amplitude));
    gl_FragColor = vec4( rgb.x, rgb.y, rgb.z, alphaValue);
#endif

}

