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

vec2 fieldAt(vec3 point){
    vec2 field = vec2(0.0);

    for(int i = 0; i < N_TRANS; ++i){ //try loop unroll
        vec3 diffVec = point - tPos[i];
        
        float dist = length(diffVec);

        //float dum = 0.5 * tSpecs[i].x * tSpecs[i].w * sin ( acos( dot(diffVec, tNorm[i]) / dist) );
        vec3 diffVecU = diffVec / dist;
        float dum = 0.5 * tSpecs[i].x * tSpecs[i].w * length( (dot(diffVecU,tNorm[i])*tNorm[i]) -  diffVecU );
   
        float directivity = sin(dum) / dum;
        
        float ampDirAtt = tSpecs[i].y * directivity / dist;
        float kdPlusPhase = tSpecs[i].x * dist + tSpecs[i].z;
        field.x += ampDirAtt * cos(kdPlusPhase);
        field.y += ampDirAtt * sin(kdPlusPhase);
    }

    return field;
}

float sAbsOfGradient(vec2 mX, vec2 pX, vec2 mY, vec2 pY, vec2 mZ, vec2 pZ){
    float a = (length(vec3(length(pX - mX),length(pY - mY),length(pZ - mZ))));
    //float a = (length(pX - mX) + length(pY - mY) + length(pZ - mZ));
    return a*a;
}

void main()
{
    vec3 w = vec3(wPos);
    
    vec2 f000000 = fieldAt(w + vec3(0,0,0) * gSep);
    vec2 fp10000 = fieldAt(w + vec3(1,0,0) * gSep);
    vec2 fp20000 = fieldAt(w + vec3(2,0,0) * gSep);
    vec2 fn10000 = fieldAt(w + vec3(-1,0,0) * gSep);
    vec2 fn20000 = fieldAt(w + vec3(-2,0,0) * gSep);
    vec2 f0000p1 = fieldAt(w + vec3(0,0,1) * gSep);
    vec2 f0000n1 = fieldAt(w + vec3(0,0,-1) * gSep);
    vec2 fp100p1 = fieldAt(w + vec3(1,0,1) * gSep);
    vec2 fn100p1 = fieldAt(w + vec3(-1,0,1) * gSep);
    vec2 fp100n1 = fieldAt(w + vec3(1,0,-1) * gSep);
    vec2 fn100n1 = fieldAt(w + vec3(-1,0,-1) * gSep);
    vec2 f0000p2 = fieldAt(w + vec3(0,0,2) * gSep);
    vec2 f0000n2 = fieldAt(w + vec3(0,0,-2) * gSep);

    vec2 f00p100 = fieldAt(w + vec3(0,1,0) * gSep);
    vec2 fn1p100 = fieldAt(w + vec3(-1,1,0) * gSep);
    vec2 fp1p100 = fieldAt(w + vec3(1,1,0) * gSep);
    vec2 f00p1n1 = fieldAt(w + vec3(0,1,-1) * gSep);
    vec2 f00p1p1 = fieldAt(w + vec3(0,1,1) * gSep);

    vec2 f00n100 = fieldAt(w + vec3(0, -1,0) * gSep);
    vec2 fn1n100 = fieldAt(w + vec3(-1,-1,0) * gSep);
    vec2 fp1n100 = fieldAt(w + vec3(1, -1,0) * gSep);
    vec2 f00n1n1 = fieldAt(w + vec3(0, -1,-1) * gSep);
    vec2 f00n1p1 = fieldAt(w + vec3(0, -1,1) * gSep);

    vec2 f00p200 = fieldAt(w + vec3(0,2,0) * gSep);
    vec2 f00n200 = fieldAt(w + vec3(0,-2,0) * gSep);

    float lGorkov =  0.0;
    

    lGorkov += kPre * ( -6.0*dot(f000000,f000000) + 
        dot(fp10000,fp10000) + dot(fn10000,fn10000) + 
        dot(f00p100,f00p100) + dot(f00n100,f00n100) +
        dot(f0000p1,f0000p1) + dot(f0000n1,f0000n1)) ;

    lGorkov -= kVel / (4.0*gSep*gSep) * kPreToVel * kPreToVel * ( 
        -6.0*sAbsOfGradient(fp10000,fn10000,f00p100,f00n100,f0000p1,f0000n1) +
       sAbsOfGradient(fp20000,f000000,fp1p100,fp1n100,fp100p1,fp100n1) + 
       sAbsOfGradient(f000000,fn20000,fn1p100,fn1n100,fn100p1,fn100n1) +
       sAbsOfGradient(fp1p100,fn1p100,f00p200,f000000,f00p1p1,f00p1n1) + 
       sAbsOfGradient(fp1n100,fn1n100,f000000,f00n200,f00n1p1,f00n1n1) +
       sAbsOfGradient(fp100p1,fn100p1,f00p1p1,f00n1p1,f0000p2,f000000) + 
       sAbsOfGradient(fp100n1,fn100n1,f00p1n1,f00n1n1,f000000,f0000n2));

    lGorkov *= pVol;

    vec3 colNoAlpha;
    if (lGorkov > 0.0){ 
        float col = clamp( (lGorkov-minPosColor)/(maxPosColor-minPosColor) , 0, 1);
        if(colouring == 1){ //red
            float value = col;
            colNoAlpha = vec3(value, 0, 0); 
        }else if(colouring == 2){ //16 periods red
            float value = cos(2*PI*16.0*col)*0.5+0.5;
            colNoAlpha = vec3(value, 0, 0); 
        }else if(colouring == 3){ //cosine fire
            colNoAlpha = vec3(0.5) + cos( vec3(col*PI+PI) - vec3(-1.0,0.0,1.0) ); 
        }else{ //linear fire gradient
            colNoAlpha = vec3(col*3.0, col*3.0 - 1.0, col*3.0 - 2.0); 
        }
    }else{ 
        float col = clamp( (lGorkov-minNegColor)/(maxNegColor-minNegColor) , 0, 1);
        col = 1.0 - col;
        if(colouring == 1){ //blue
            float value = col;
            colNoAlpha = vec3(0, 0, value);
        }else if(colouring == 2){ //16 periods blue
            float value = cos(2*PI*16.0*col)*0.5+0.5;
            colNoAlpha = vec3(0, 0, value);
        }else if(colouring == 3){ //cosine ice
            colNoAlpha = vec3(0.5) + cos( vec3(col*PI+PI) - vec3(1.0,0.0,-1.0) ); 
        }else{ //linear ice gradient
            colNoAlpha = vec3(col*3.0 - 2.0, col*3.0 - 1.0, col*3.0); 
        }
    }
    gl_FragColor = vec4(colNoAlpha, alphaValue);
    
}

