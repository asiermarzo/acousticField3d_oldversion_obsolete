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

float gorkovAt(vec3 point){
    
    vec2 pre = vec2(0.0);
    vec2 gx = vec2(0.0), gy = vec2(0.0), gz = vec2(0.0);
    vec2 tmp;

    for(int i = 0; i < N_TRANS; ++i){
        vec3 diffVec = point - tPos[i];
        float dist = length(diffVec);
        float dist3 = dist*dist*dist;
        
        float kd = tSpecs[i].x * dist;
        float cosKD = cos(kd);
        float sinKD = sin(kd);
       
        float amp = tSpecs[i].y;
        float cosP = cos(tSpecs[i].z);
        float sinP = sin(tSpecs[i].z);
        float cosKDdist = cosKD / dist;
        float sinKDdist = sinKD / dist;
        
        float aDevCosDir = -(cosKD + kd*sinKD);
        float aDevSinDir = (kd*cosKD - sinKD) ;
     
        tmp = amp * diffVec.x / dist3 * vec2(aDevCosDir, aDevSinDir);
        gx.x += tmp.x*cosP - tmp.y*sinP;
        gx.y += tmp.x*sinP + tmp.y*cosP;

        tmp = amp * diffVec.y / dist3 * vec2(aDevCosDir, aDevSinDir);
        gy.x += tmp.x*cosP - tmp.y*sinP;
        gy.y += tmp.x*sinP + tmp.y*cosP;

        tmp = amp * diffVec.z  / dist3 * vec2(aDevCosDir, aDevSinDir);
        gz.x += tmp.x*cosP - tmp.y*sinP;
        gz.y += tmp.x*sinP + tmp.y*cosP;

        tmp = amp * vec2(cosKDdist, sinKDdist);
        pre.x += tmp.x*cosP - tmp.y*sinP;
        pre.y += tmp.x*sinP + tmp.y*cosP;
    }

    float velAmpSquare = (dot(gx,gx) +  dot(gy,gy) + dot(gz,gz)) * kPreToVel*kPreToVel;
    float gorkov =  pVol * (kPre * dot(pre,pre) -  kVel*velAmpSquare);
 
    return gorkov;
}

void main()
{
    vec3 w = vec3(wPos);
    
    float f000 = gorkovAt(w + vec3(0.0,0.0,0.0) * gSep);
    float fn00 = gorkovAt(w + vec3(-1.0,0.0,0.0) * gSep);
    float fp00 = gorkovAt(w + vec3(1.0,0.0,0.0) * gSep);
    float f0n0 = gorkovAt(w + vec3(0.0,-1.0,0.0) * gSep);
    float f0p0 = gorkovAt(w + vec3(0.0,1.0,0.0) * gSep);
    float f00n = gorkovAt(w + vec3(0.0,0.0,-1.0) * gSep);
    float f00p = gorkovAt(w + vec3(0.0,0.0,1.0) * gSep);

    float lGorkov =  fn00 + fp00 + f0n0 + f0p0 + f00n + f00p - 6.0 * f000;
    
    vec3 colNoAlpha;
    if (lGorkov > 0.0){ 
        float col = clamp( (lGorkov-minPosColor)/(maxPosColor-minPosColor) , 0.0, 1.0);
        if(colouring == 1){ //red
            float value = col;
            colNoAlpha = vec3(value, 0.0, 0.0); 
        }else if(colouring == 2){ //16 periods red
            float value = cos(2*PI*16.0*col)*0.5+0.5;
            colNoAlpha = vec3(value, 0.0, 0.0); 
        }else if(colouring == 3){ //cosine fire
            colNoAlpha = vec3(0.5) + cos( vec3(col*PI+PI) - vec3(-1.0,0.0,1.0) ); 
        }else{ //linear fire gradient
            colNoAlpha = vec3(col*3.0, col*3.0 - 1.0, col*3.0 - 2.0); 
        }
    }else{ 
        float col = clamp( (lGorkov-minNegColor)/(maxNegColor-minNegColor) , 0.0, 1.0);
        col = 1.0 - col;
        if(colouring == 1){ //blue
            float value = col;
            colNoAlpha = vec3(0.0, 0.0, value);
        }else if(colouring == 2){ //16 periods blue
            float value = cos(2*PI*16.0*col)*0.5+0.5;
            colNoAlpha = vec3(0.0, 0.0, value);
        }else if(colouring == 3){ //cosine ice
            colNoAlpha = vec3(0.5) + cos( vec3(col*PI+PI) - vec3(1.0,0.0,-1.0) ); 
        }else{ //linear ice gradient
            colNoAlpha = vec3(col*3.0 - 2.0, col*3.0 - 1.0, col*3.0); 
        }
    }
    gl_FragColor = vec4(colNoAlpha, alphaValue);
    
}

