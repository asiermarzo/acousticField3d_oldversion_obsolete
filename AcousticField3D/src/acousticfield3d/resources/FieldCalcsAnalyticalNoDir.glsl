//BEGIN FIELD
vec2 fieldAt(vec3 point){
    vec2 field = vec2(0.0);

    for(int i = 0; i < N_TRANS; ++i){ //try loop unroll
        vec3 diffVec = point - tPos[i];
        
        float dist = length(diffVec);
        
        float ampDirAtt = tSpecs[i].y / dist;
        float kdPlusPhase = tSpecs[i].x * dist + tSpecs[i].z;
        field.x += ampDirAtt * cos(kdPlusPhase);
        field.y += ampDirAtt * sin(kdPlusPhase);
    }

    return field;
}
//END

//BEGIN GORKOV
float gorkovAt(vec3 point){
    
    vec2 pre = vec2(0.0);
    vec2 gx = vec2(0.0), gy = vec2(0.0), gz = vec2(0.0);
    vec2 tmp;

    for(int i = 0; i < N_TRANS; ++i){
        vec3 diffVec = point - tPos[i];
        float dist = length(diffVec);
        float dist3 = dist*dist*dist;
   
        //tSpecs[i].x -> k
        //tSpecs[i].y -> amp
        //tSpecs[i].z -> phase
        //tSpecs[i].w -> w
        
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
float laplacianGorkovAt(vec3 point){
    
    vec2 pre = vec2(0.0);
    vec2 gx = vec2(0.0), gy = vec2(0.0), gz = vec2(0.0);
    vec2 gxy = vec2(0.0), gxz = vec2(0.0), gyz = vec2(0.0);
    vec2 gxx = vec2(0.0), gyy = vec2(0.0), gzz = vec2(0.0);
    vec2 gxxx = vec2(0.0), gyyy = vec2(0.0), gzzz = vec2(0.0);
    vec2 gxyy = vec2(0.0), gxzz = vec2(0.0);
    vec2 gyxx = vec2(0.0), gyzz = vec2(0.0);
    vec2 gzxx = vec2(0.0), gzyy = vec2(0.0);

    vec2 swap, tmp;

    for(int i = 0; i < N_TRANS; ++i){
        vec3 diffVec = point - tPos[i];
        float d = length(diffVec);
        float d2 = d*d;
        float d3 = d2*d;
        float d5 = d2*d3;
        float d7 = d5*d2;
   
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

        //1,2 derivatives
        swap = vec2( -15.0*diffVecS.x + d2*(3.0-k2*(d2-6.0*diffVecS.x)), -15.0*diffVecS.x + d2*(3.0 + k2*diffVecS.x ));
        tmp = amp / d7 * vec2( swap.x*cosKD+swap.y*KDsinKD, -(swap.y*KDcosKD-swap.x*sinKD) );
        swap = diffVec.y * tmp;
        gyxx.x += swap.x*cosP - swap.y*sinP;
        gyxx.y += swap.x*sinP + swap.y*cosP;
        swap = diffVec.z * tmp;
        gzxx.x += swap.x*cosP - swap.y*sinP;
        gzxx.y += swap.x*sinP + swap.y*cosP;
        swap = vec2( -15.0*diffVecS.y + d2*(3.0-k2*(d2-6.0*diffVecS.y)), -15.0*diffVecS.y + d2*(3.0 + k2*diffVecS.y ));
        tmp = amp / d7 * vec2( swap.x*cosKD+swap.y*KDsinKD, -(swap.y*KDcosKD-swap.x*sinKD) );
        swap = diffVec.x * tmp;
        gxyy.x += swap.x*cosP - swap.y*sinP;
        gxyy.y += swap.x*sinP + swap.y*cosP;
        swap = diffVec.z * tmp;
        gzyy.x += swap.x*cosP - swap.y*sinP;
        gzyy.y += swap.x*sinP + swap.y*cosP;
        swap = vec2( -15.0*diffVecS.z + d2*(3.0-k2*(d2-6.0*diffVecS.z)), -15.0*diffVecS.z + d2*(3.0 + k2*diffVecS.z ));
        tmp = amp / d7 * vec2( swap.x*cosKD+swap.y*KDsinKD, -(swap.y*KDcosKD-swap.x*sinKD) );
        swap = diffVec.x * tmp;
        gxzz.x += swap.x*cosP - swap.y*sinP;
        gxzz.y += swap.x*sinP + swap.y*cosP;
        swap = diffVec.y * tmp;
        gyzz.x += swap.x*cosP - swap.y*sinP;
        gyzz.y += swap.x*sinP + swap.y*cosP;

        //3 derivatives
        tmp = vec2( -3.0 * (5.0*diffVecS.x + d2 * (k2*(d2 - 2.0*diffVecS.x)-3.0)) , -15.0*diffVecS.x + d2*(9.0+k2*diffVecS.x) );
        swap =  amp / d7 * diffVec.x * vec2( tmp.x*cosKD+tmp.y*KDsinKD, -(tmp.y*KDcosKD-tmp.x*sinKD) );
        gxxx.x += swap.x*cosP - swap.y*sinP;
        gxxx.y += swap.x*sinP + swap.y*cosP;
        tmp = vec2(-3.0 * (5.0*diffVecS.y + d2 * (k2*(d2 - 2.0*diffVecS.y)-3.0)) , -15.0*diffVecS.y + d2*(9.0+k2*diffVecS.y) );
        swap =  amp / d7 * diffVec.y * vec2( tmp.x*cosKD+tmp.y*KDsinKD, -(tmp.y*KDcosKD-tmp.x*sinKD) );
        gyyy.x += swap.x*cosP - swap.y*sinP;
        gyyy.y += swap.x*sinP + swap.y*cosP;
        tmp = vec2(-3.0 * (5.0*diffVecS.z + d2 * (k2*(d2 - 2.0*diffVecS.z)-3.0)) , -15.0*diffVecS.z + d2*(9.0+k2*diffVecS.z) );
        swap =  amp / d7 * diffVec.z * vec2( tmp.x*cosKD+tmp.y*KDsinKD, -(tmp.y*KDcosKD-tmp.x*sinKD) );
        gzzz.x += swap.x*cosP - swap.y*sinP;
        gzzz.y += swap.x*sinP + swap.y*cosP;
    }

    float prePart = dot(gx,gx) + dot(pre,gxx) + dot(gy,gy) + dot(pre,gyy) + dot(gz,gz) + dot(pre,gzz);

    float velPart = dot(gxx,gxx) + dot(gx,gxxx) + dot(gxy,gxy) + dot(gy,gyxx) + dot(gxz,gxz) + dot(gz,gzxx);
    velPart += dot(gxy,gxy) + dot(gx,gxyy) + dot(gyy,gyy) + dot(gy,gyyy) + dot(gyz,gyz) + dot(gz,gzyy);
    velPart += dot(gxz,gxz) + dot(gx,gxzz) + dot(gyz,gyz) + dot(gy,gyzz) + dot(gzz,gzz) + dot(gz,gzzz);

    float lgorkov =  pVol * 2.0 * (kPre*prePart -  kVel*kPreToVel*kPreToVel*velPart);

    return lgorkov;
}
//END