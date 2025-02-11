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

uniform int gSize;
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

void main()
{
    vec3 w = vec3(wPos);

    vec2 gX = vec2(0.0), gY = vec2(0.0), gZ = vec2(0.0);
    float gDiv;
    if(gSize == 1){
        gDiv = 2.0;
        gX -= 1.0 * fieldAt(w - vec3(gSep*1,0,0) );
        gX += 1.0 * fieldAt(w + vec3(gSep*1,0,0) );
        gY -= 1.0 * fieldAt(w - vec3(0,gSep*1,0) );
        gY += 1.0 * fieldAt(w + vec3(0,gSep*1,0) );
        gZ -= 1.0 * fieldAt(w - vec3(0,0,gSep*1) );
        gZ += 1.0 * fieldAt(w + vec3(0,0,gSep*1) );
    }else if(gSize == 2){
        gDiv = 12.0;
        gX += 1.0 * fieldAt(w - vec3(gSep*2,0,0) );
        gX -= 8.0 * fieldAt(w - vec3(gSep*1,0,0) );
        gX += 8.0 * fieldAt(w + vec3(gSep*1,0,0) );
        gX -= 1.0 * fieldAt(w + vec3(gSep*2,0,0) );
        gY += 1.0 * fieldAt(w - vec3(0,gSep*2,0) );
        gY -= 8.0 * fieldAt(w - vec3(0,gSep*1,0) );
        gY += 8.0 * fieldAt(w + vec3(0,gSep*1,0) );
        gY -= 1.0 * fieldAt(w + vec3(0,gSep*2,0) );
        gZ += 1.0 * fieldAt(w - vec3(0,0,gSep*2) );
        gZ -= 8.0 * fieldAt(w - vec3(0,0,gSep*1) );
        gZ += 8.0 * fieldAt(w + vec3(0,0,gSep*1) );
        gZ -= 1.0 * fieldAt(w + vec3(0,0,gSep*2) );
    }else{
        gDiv = 60.0;
        gX -= 1.0 * fieldAt(w - vec3(gSep*3,0,0) );
        gX += 9.0 * fieldAt(w - vec3(gSep*2,0,0) );
        gX -= 45.0 * fieldAt(w - vec3(gSep*1,0,0) );
        gX += 45.0 * fieldAt(w + vec3(gSep*1,0,0) );
        gX -= 9.0 * fieldAt(w + vec3(gSep*2,0,0) );
        gX += 1.0 * fieldAt(w + vec3(gSep*3,0,0) );
        gY -= 1.0 * fieldAt(w - vec3(0,gSep*3,0) );
        gY += 9.0 * fieldAt(w - vec3(0,gSep*2,0) );
        gY -= 45.0 * fieldAt(w - vec3(0,gSep*1,0) );
        gY += 45.0 * fieldAt(w + vec3(0,gSep*1,0) );
        gY -= 9.0 * fieldAt(w + vec3(0,gSep*2,0) );
        gY += 1.0 * fieldAt(w + vec3(0,gSep*3,0) );
        gZ -= 1.0 * fieldAt(w - vec3(0,0,gSep*3) );
        gZ += 9.0 * fieldAt(w - vec3(0,0,gSep*2) );
        gZ -= 45.0 * fieldAt(w - vec3(0,0,gSep*1) );
        gZ += 45.0 * fieldAt(w + vec3(0,0,gSep*1) );
        gZ -= 9.0 * fieldAt(w + vec3(0,0,gSep*2) );
        gZ += 1.0 * fieldAt(w + vec3(0,0,gSep*3) );
    }
    float velAmp = ( length(vec3(length(gX),length(gY),length(gZ)))) * kPreToVel / gDiv / gSep;
     
    vec2 pre = fieldAt(w);
    float gorkov =  pVol * (kPre * dot(pre,pre) -  kVel*velAmp*velAmp);
 
    vec3 colNoAlpha;
    if (gorkov > 0.0){ 
        float col = clamp( (gorkov-minPosColor)/(maxPosColor-minPosColor) , 0, 1);
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
        float col = clamp( (gorkov-minNegColor)/(maxNegColor-minNegColor) , 0, 1);
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

