#ifdef GL_ES
  precision highp float;
#endif

#define N_TRANS _N_TRANS_
uniform vec3 tPos[N_TRANS];
uniform vec3 tNorm[N_TRANS];
uniform vec4 tSpecs[N_TRANS];

uniform float simInvGain;
uniform vec4 eyePos;
uniform vec3 cubeSize;
uniform float densityDist;

#define N_ALPHA _N_ALPHA_
uniform float maxAlpha;
uniform float alphaMapSum;
uniform float alphaMap[N_ALPHA];

varying vec4 wPos;
varying vec3 cPos;

#define PI 3.1415926535897932384626433832795

vec3 hsv2rgb(vec3 c){
    vec4 K = vec4(1.0, 2.0 / 3.0, 1.0 / 3.0, 3.0);
    vec3 p = abs(fract(c.xxx + K.xyz) * 6.0 - K.www);
    return c.z * mix(K.xxx, clamp(p - K.xxx, 0.0, 1.0), c.y);
}

//TEMPLATE FIELD

void main()
{
    vec3 wDir = normalize( vec3(wPos - eyePos) );
    vec3 cDir = normalize( wDir / cubeSize );
    vec3 cInc = cDir * densityDist;
    vec3 wInc = wDir * densityDist * cubeSize;

    float totalAlpha = 0.0;
    vec3 totalColor = vec3(0.0);
    
    vec3 c = cPos;
    vec3 w = wPos.xyz;
    vec3 ones = vec3(1.0);
    vec3 zeroes = vec3(0.0);
    while(! any(bvec2(     any(greaterThan(c, ones)),   any(lessThan(c, zeroes))   ))){
        float amp = length( fieldAt(w)  );
        float tValue = clamp( amp / simInvGain, 0, 1);
        vec3 tColor = hsv2rgb(vec3(  2.0/3.0 * (1.0 - tValue), 1.0, 0.75 + tValue / 4.0));
       
        int alphaIndex = int(tValue * (N_ALPHA-1));
        float tAlpha = alphaMap[alphaIndex];
        totalAlpha += tAlpha;
        totalColor += tColor * tAlpha;
        
        w += wInc;
        c += cInc;
    }
    totalColor /= totalAlpha;
    gl_FragColor = vec4( totalColor, totalAlpha / alphaMapSum * maxAlpha);
}

