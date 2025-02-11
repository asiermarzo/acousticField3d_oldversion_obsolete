#ifdef GL_ES
  precision highp float;
#endif

#define PI 3.1415926535897932384626433832795

uniform float alphaValue;
uniform int useAmp;
uniform int colouring;
uniform float minNegColor;
uniform float maxNegColor;
uniform float minPosColor;
uniform float maxPosColor;

varying vec4 wPos;
varying vec4 mPos;

vec3 hsv2rgb(vec3 c){
    vec4 K = vec4(1.0, 2.0 / 3.0, 1.0 / 3.0, 3.0);
    vec3 p = abs(fract(c.xxx + K.xyz) * 6.0 - K.www);
    return c.z * mix(K.xxx, clamp(p - K.xxx, 0.0, 1.0), c.y);
}

//INCLUDE Colouring.glsl

void main()
{
    float value = mPos.x * 2.0;
 
    if(useAmp == 1){
        if(value > 0.0){
            value = (value*(maxPosColor-minPosColor))+minPosColor;
        }else{
            value = (value*(maxNegColor-minNegColor))+minPosColor;
        }
        gl_FragColor = vec4(colorFunc( value ),  alphaValue);

    }else{

        vec3 rgb = hsv2rgb(vec3( (value+1.0)/2.0 ,1.0,1.0));    
        gl_FragColor = vec4( rgb , alphaValue);

    }
}

