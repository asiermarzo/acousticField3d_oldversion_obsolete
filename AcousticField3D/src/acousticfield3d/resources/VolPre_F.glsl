#ifdef GL_ES
  precision highp float;
#endif


uniform int renderMethod;

uniform int colouring;
uniform float minNegColor;
uniform float maxNegColor;
uniform float minPosColor;
uniform float maxPosColor;
uniform float alphaValue;

uniform vec4 eyePos;
uniform vec3 cubeSize;
uniform float densityDist;

uniform sampler3D preCube;

#define N_ALPHA _N_ALPHA_
uniform float alphaMapSum;
uniform float alphaMap[N_ALPHA];

varying vec4 wPos;
varying vec3 cPos;

uniform vec4 lightPos;
uniform float ambient;
uniform float diffuse;
uniform float specular;
uniform float shininess;

#define PI 3.1415926535897932384626433832795

//INCLUDE Colouring.glsl

void main()
{
    vec3 wDir = normalize( vec3(wPos - eyePos) );
    vec3 cDir = normalize( wDir / cubeSize );
    vec3 cInc = cDir * densityDist;
    vec3 wInc = wDir * densityDist * cubeSize;
  
    vec3 c = cPos;
    vec3 ones = vec3(1.0);
    vec3 zeroes = vec3(0.0);

    float maxValue = 0.0;
    float totalAlpha = 0.0;
    vec3 totalColor = vec3(0.0);

    if(renderMethod == 0){ //alpha map
        while(! any(bvec2(     any(greaterThan(c, ones)),   any(lessThan(c, zeroes))   ))){
            float value = texture(preCube, c).x;
            float tValue = clamp( (value - minPosColor) / (maxPosColor - minPosColor) , 0.0, 1.0);
            vec3 tColor = colorFunc(value);
            int alphaIndex = int(tValue * (N_ALPHA-1));
            float tAlpha = alphaMap[alphaIndex];

            totalColor += tColor * tAlpha;
            totalAlpha += tAlpha;
            c += cInc;
        }
        totalColor /= totalAlpha;
        gl_FragColor = vec4( totalColor, alphaValue);
    }else{ //1 Max projection intensity
        while(! any(bvec2(     any(greaterThan(c, ones)),   any(lessThan(c, zeroes))   ))){
            float value = texture(preCube, c).x;
            maxValue = max(maxValue, value);

            c += cInc;
        }
        gl_FragColor = vec4(colorFunc(maxValue),  alphaValue);
    }
    

}

