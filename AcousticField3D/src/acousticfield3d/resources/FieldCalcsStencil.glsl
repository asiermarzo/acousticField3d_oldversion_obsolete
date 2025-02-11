//BEGIN FIELD
vec2 fieldAt(vec3 point){
    vec2 field = vec2(0.0);

    for(int i = 0; i < N_TRANS; ++i){ //try loop unroll
        vec3 diffVec = point - tPos[i];
        
        float dist = length(diffVec);

        vec3 diffVecU = diffVec / dist;
        float sinAngle = length( (dot(diffVecU,tNorm[i])*tNorm[i]) -  diffVecU );

        /*
        float directivity = 1.0 - 0.44*sinAngle;
        */

        float directivity = 1.0 - 0.25*sinAngle - 0.6*sinAngle*sinAngle;
        //float directivity = 1.0 / (1.0 + 0.0*sinAngle + 128.0*sinAngle*sinAngle);

        /*float dum = 0.5 * tSpecs[i].x * 0.0077 * sinAngle;
        float directivity = sin(dum) / dum;*/

        float ampDirAtt = tSpecs[i].y * directivity / dist;
        float kdPlusPhase = tSpecs[i].x * dist + tSpecs[i].z;
        field.x += ampDirAtt * cos(kdPlusPhase);
        field.y += ampDirAtt * sin(kdPlusPhase);
    }

    return field;
}
//END

//BEGIN GORKOV
vec2 fieldAt(vec3 point){
    vec2 field = vec2(0.0);

    for(int i = 0; i < N_TRANS; ++i){ //try loop unroll
        vec3 diffVec = point - tPos[i];
        
        float dist = length(diffVec);

        vec3 diffVecU = diffVec / dist;
        float sinAngle = length( (dot(diffVecU,tNorm[i])*tNorm[i]) -  diffVecU );

        /*
        float directivity = 1.0 - 0.44*sinAngle;
        */

        float directivity = 1.0 - 0.25*sinAngle - 0.6*sinAngle*sinAngle;
        //float directivity = 1.0 / (1.0 + 0.0*sinAngle + 128.0*sinAngle*sinAngle);

        /*float dum = 0.5 * tSpecs[i].x * 0.0077 * sinAngle;
        float directivity = sin(dum) / dum;*/
        
        float ampDirAtt = tSpecs[i].y * directivity / dist;
        float kdPlusPhase = tSpecs[i].x * dist + tSpecs[i].z;
        field.x += ampDirAtt * cos(kdPlusPhase);
        field.y += ampDirAtt * sin(kdPlusPhase);
    }

    return field;
}

float gorkovAt(vec3 w){
    
    vec2 gX = vec2(0.0), gY = vec2(0.0), gZ = vec2(0.0);

    gX -= 1.0 * fieldAt(w - vec3(gSep*1,0,0) );
    gX += 1.0 * fieldAt(w + vec3(gSep*1,0,0) );
    gY -= 1.0 * fieldAt(w - vec3(0,gSep*1,0) );
    gY += 1.0 * fieldAt(w + vec3(0,gSep*1,0) );
    gZ -= 1.0 * fieldAt(w - vec3(0,0,gSep*1) );
    gZ += 1.0 * fieldAt(w + vec3(0,0,gSep*1) );

    float velAmp = ( length(vec3(length(gX),length(gY),length(gZ)))) * kPreToVel / 2.0 / gSep;
     
    vec2 pre = fieldAt(w);
    float gorkov =  pVol * (kPre * dot(pre,pre) -  kVel*velAmp*velAmp);

    return gorkov;
}
//END

//BEGIN GORKOVG
vec3 gorkovGradientAt(vec3 point, float k1s, float k2s){
    
    vec2 pre = vec2(0.0);
    vec2 gx = vec2(0.0), gy = vec2(0.0), gz = vec2(0.0);
    vec2 gxy = vec2(0.0), gxz = vec2(0.0), gyz = vec2(0.0);
    vec2 gxx = vec2(0.0), gyy = vec2(0.0), gzz = vec2(0.0);

    vec2 swap, tmp;

    for(int i = 0; i < N_TRANS; ++i){
        vec3 diffVec = point - tPos[i];
        float d = length(diffVec);
        float d2 = d*d;
        float d3 = d2*d;
        float d5 = d2*d3;
       
        //tSpecs[i].x -> k
        //tSpecs[i].y -> amp
        //tSpecs[i].z -> phase
        //tSpecs[i].w -> w
        
        float k = tSpecs[i].x;
        float kd = k*d;
        float k2 = k*k;
        float d2k2 = d2 * k2;
        float cosKD = cos(kd);
        float sinKD = sin(kd);
        float KDcosKD = kd * cosKD;
        float KDsinKD = kd * sinKD;
        
        vec3 diffVecS = diffVec*diffVec;
        float amp = tSpecs[i].y;
        float cosP = cos(tSpecs[i].z);
        float sinP = sin(tSpecs[i].z);
        
        //0 derivative
        swap = amp / d * vec2(cosKD, sinKD);
        pre.x += swap.x*cosP - swap.y*sinP;
        pre.y += swap.x*sinP + swap.y*cosP;

        //1 derivatives
        tmp = amp / d3 * vec2(-(cosKD+KDsinKD), KDcosKD-sinKD);
        swap =  diffVec.x  * tmp;
        gx.x += swap.x*cosP - swap.y*sinP;
        gx.y += swap.x*sinP + swap.y*cosP;
        swap = diffVec.y * tmp;
        gy.x += swap.x*cosP - swap.y*sinP;
        gy.y += swap.x*sinP + swap.y*cosP;
        swap = diffVec.z * tmp;
        gz.x += swap.x*cosP - swap.y*sinP;
        gz.y += swap.x*sinP + swap.y*cosP;

        //1,1 derivatives
        tmp = vec2(3.0-d2k2, 3.0);
        tmp = amp / d5 * vec2( tmp.x*cosKD+tmp.y*KDsinKD, -(tmp.y*KDcosKD-tmp.x*sinKD) );
        swap = diffVec.x * diffVec.y * tmp;
        gxy.x += swap.x*cosP - swap.y*sinP;
        gxy.y += swap.x*sinP + swap.y*cosP;
        swap = diffVec.x * diffVec.z * tmp;
        gxz.x += swap.x*cosP - swap.y*sinP;
        gxz.y += swap.x*sinP + swap.y*cosP;
        swap = diffVec.y * diffVec.z * tmp;
        gyz.x += swap.x*cosP - swap.y*sinP;
        gyz.y += swap.x*sinP + swap.y*cosP;

        //2 derivatives
        tmp = vec2(d2 + (d2k2 - 3.0) * diffVecS.x , d2 - 3.0 * diffVecS.x);
        swap =  amp / d5 * vec2(- (tmp.x*cosKD+tmp.y*KDsinKD), tmp.y*KDcosKD-tmp.x*sinKD );
        gxx.x += swap.x*cosP - swap.y*sinP;
        gxx.y += swap.x*sinP + swap.y*cosP;
        tmp = vec2(d2 + (d2k2 - 3.0) * diffVecS.y , d2 - 3.0 * diffVecS.y);
        swap =  amp / d5 * vec2(- (tmp.x*cosKD+tmp.y*KDsinKD), tmp.y*KDcosKD-tmp.x*sinKD );
        gyy.x += swap.x*cosP - swap.y*sinP;
        gyy.y += swap.x*sinP + swap.y*cosP;
        tmp = vec2(d2 + (d2k2 - 3.0) * diffVecS.z , d2 - 3.0 * diffVecS.z);
        swap =  amp / d5 * vec2(- (tmp.x*cosKD+tmp.y*KDsinKD), tmp.y*KDcosKD-tmp.x*sinKD );
        gzz.x += swap.x*cosP - swap.y*sinP;
        gzz.y += swap.x*sinP + swap.y*cosP;
    }

   
    float K1 = pVol * 2.0f * kPre * k1s;
    float K2 = pVol * 2.0f * kVel*kPreToVel*kPreToVel * k2s;


    vec3 gradientGorkov = vec3(
            K1 * dot(pre,gx) - K2*( dot(gx,gxx) + dot(gy,gxy) + dot(gz,gxz)),
            K1 * dot(pre,gy) - K2*( dot(gx,gxy) + dot(gy,gyy) + dot(gz,gyz)),
            K1 * dot(pre,gz) - K2*( dot(gx,gxz) + dot(gy,gyz) + dot(gz,gzz))
        );

    return gradientGorkov;
}
//END

//BEGIN GORKOVL
vec2 fieldAt(vec3 point){
    vec2 field = vec2(0.0);

    for(int i = 0; i < N_TRANS; ++i){ //try loop unroll
        vec3 diffVec = point - tPos[i];
        
        float dist = length(diffVec);

        vec3 diffVecU = diffVec / dist;
        float sinAngle = length( (dot(diffVecU,tNorm[i])*tNorm[i]) -  diffVecU );

        /*
        float directivity = 1.0 - 0.44*sinAngle;
        */

        float directivity = 1.0 - 0.25*sinAngle - 0.6*sinAngle*sinAngle;
        //float directivity = 1.0 / (1.0 + 0.0*sinAngle + 128.0*sinAngle*sinAngle);

        /*float dum = 0.5 * tSpecs[i].x * 0.0077 * sinAngle;
        float directivity = sin(dum) / dum;*/
        
        float ampDirAtt = tSpecs[i].y * directivity / dist;
        float kdPlusPhase = tSpecs[i].x * dist + tSpecs[i].z;
        field.x += ampDirAtt * cos(kdPlusPhase);
        field.y += ampDirAtt * sin(kdPlusPhase);
    }

    return field;
}

float sAbsOfGradient(vec2 mX, vec2 pX, vec2 mY, vec2 pY, vec2 mZ, vec2 pZ){
    float a = (length(vec3(length(pX - mX),length(pY - mY),length(pZ - mZ))));
    return a*a;
}

float laplacianGorkovAt(vec3 w){
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

    lGorkov *= pVol * 1000000.0;

    return lGorkov;
}

//END