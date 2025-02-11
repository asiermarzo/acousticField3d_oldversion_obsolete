#ifdef GL_ES
  precision highp float;
#endif

#define N_TRANS _N_TRANS_
uniform vec3 tPos[N_TRANS];
uniform vec3 tNorm[N_TRANS];
uniform vec4 tSpecs[N_TRANS];

uniform float simInvGain;

uniform float maxIso;
uniform float minIso;

uniform vec4 lightPos;
uniform vec4 eyePos;
uniform float ambient;
uniform float diffuse;
uniform float specular;
uniform float shininess;

uniform vec3 cubeSize;
uniform float densityDist;

uniform float maxAlpha;

varying vec4 wPos;
varying vec3 cPos;

#define PI 3.1415926535897932384626433832795

vec3 hsv2rgb(vec3 c){
    vec4 K = vec4(1.0, 2.0 / 3.0, 1.0 / 3.0, 3.0);
    vec3 p = abs(fract(c.xxx + K.xyz) * 6.0 - K.www);
    return c.z * mix(K.xxx, clamp(p - K.xxx, 0.0, 1.0), c.y);
}

//TEMPLATE FIELD

vec3 getNormalAt(vec3 p, float h){
    vec3 n = vec3( length(fieldAt(p - vec3(h,0.0,0.0) )) - length(fieldAt(p + vec3(h,0.0,0.0))),
                   length(fieldAt(p - vec3(0.0,h,0.0) )) - length(fieldAt(p + vec3(0.0,h,0.0))),
                   length(fieldAt(p - vec3(0.0,0.0,h) )) - length(fieldAt(p + vec3(0.0,0.0,h))) );
    return normalize(n);
}


void main()
{
    vec3 wDir = normalize( vec3(wPos - eyePos) );
    vec3 cDir = normalize( wDir / cubeSize );
    vec3 cInc = cDir * densityDist;
    vec3 wInc = wDir * densityDist * cubeSize;

    vec3 c = cPos;
    vec3 w = wPos.xyz;
    vec3 prevW = w;
    float prevAmp = 0.0;
    vec3 ones = vec3(1.0);
    vec3 zeroes = vec3(0.0);
    while(! any(bvec2(     any(greaterThan(c, ones)),   any(lessThan(c, zeroes))   ))){      
        float amp = length( fieldAt(w) );
        
        if (amp <= maxIso && amp >= minIso){
            vec3 pos;
            if (prevAmp > maxIso){
                pos = mix(w,prevW, (maxIso-amp) / (prevAmp-amp) );
            }else{ //prevAmp < minIso
                pos = mix(w,prevW, (minIso-amp) / (prevAmp-amp) );
            }


            float tValue = clamp( (maxIso+minIso)/2.0 / simInvGain, 0, 1);
            vec3 tColor = hsv2rgb(vec3(  2.0/3.0 * (1.0 - tValue), 1.0, 0.75 + tValue / 4.0));
            
            vec3 N = getNormalAt(pos, length(wInc) );
            vec3 L = normalize(lightPos.xyz - wPos.xyz);
            vec3 E = -wDir; //normalize(eyePos.xyz - wPos.xyz);
            vec3 HV = normalize(L + E);

            float lambertTerm = abs( dot(N,L) );
            float specularTerm = pow( abs( dot(N, HV) ), shininess);
            vec3 fColor = (ambient + diffuse * lambertTerm) * tColor + specularTerm * specular * vec3(1.0);
            gl_FragColor = vec4(fColor, maxAlpha);
            return;
        }
        prevAmp = amp;
        prevW = w;
        w += wInc;
        c += cInc;
    }
   
    gl_FragColor = vec4(0.0);
}

