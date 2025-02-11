#ifdef GL_ES
  precision highp float;
#endif

uniform float minColor;
uniform float maxColor;

uniform sampler3D preCube;

varying vec4 vPosition;
varying vec3 pPosition;

#define PI 3.1415926535897932384626433832795

void main()
{

    float col = clamp( (texture(preCube, pPosition).x - minColor) / (maxColor-minColor), 0, 1);
    gl_FragColor = vec4(col*3.0, col*3.0 - 1.0, col*3.0 - 2.0,  1.0); //linear fire gradient

}

