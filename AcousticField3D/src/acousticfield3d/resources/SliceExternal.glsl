#ifdef GL_ES
  precision highp float;
#endif


_USE_AMP_ #define USE_AMP 1
_USE_PHASE_ #define USE_PHASE 1
_USE_AMPPHASE_ #define USE_AMPPHASE 1

#define PI 3.1415926535897932384626433832795

uniform float alphaValue;
uniform int colouring;
uniform float minNegColor;
uniform float maxNegColor;
uniform float minPosColor;
uniform float maxPosColor;

uniform float exGain;
uniform float exOffset;
uniform sampler2D text;

varying vec4 wPos;
varying vec4 mPos;

vec3 hsv2rgb(vec3 c){
    vec4 K = vec4(1.0, 2.0 / 3.0, 1.0 / 3.0, 3.0);
    vec3 p = abs(fract(c.xxx + K.xyz) * 6.0 - K.www);
    return c.z * mix(K.xxx, clamp(p - K.xxx, 0.0, 1.0), c.y);
}

vec3 rainbowSin(float c){
    vec3 tmp = vec3(0.0, 2.0*PI/3.0, 4.0*PI/3.0) + c; //rainbow sin
    return ( sin(tmp)+1.0 ) / 2.0;
}

//INCLUDE Colouring.glsl

void main()
{

    vec2 field = texture(text, (mPos.xy + 0.5)).xy; //text
    
#ifdef USE_AMP //Amplitude
    gl_FragColor = vec4(colorFunc( (length(field)+exOffset)*exGain ),  alphaValue);
#endif

#ifdef USE_PHASE //Phase
     //float phase = atan(field.y, field.x);
     //vec3 rgb = rainbowSin(phase);
       
     float phase = clamp( mod(((atan(field.y, field.x)+PI)+exOffset)*exGain, 2*PI ) / PI / 2.0, 0.0, 1.0);
     vec3 rgb = hsv2rgb(vec3(phase,1.0,1.0));
     
    gl_FragColor = vec4( rgb , alphaValue);

#endif 


#ifdef USE_AMPPHASE //Amp + Phase
    float amplitude = clamp( (length(field)-minPosColor) / (maxPosColor-minPosColor), 0, 1);
    float phase = clamp( (atan(field.y, field.x) / PI + 1.0) / 2.0, 0, 1);
    vec3 rgb = hsv2rgb(vec3(phase,1,amplitude));
    gl_FragColor = vec4( rgb.x, rgb.y, rgb.z, alphaValue);
#endif

}

