#ifdef GL_ES
  precision highp float;
#endif
  
attribute vec4 vertexPosition;
               
uniform mat4 modelViewProjectionMatrix;
uniform mat4 modelMatrix;
uniform vec3 cubeCenter;
uniform vec3 cubeSize;

varying vec4 vPosition;
varying vec3 pPosition;

void main()
{
    vPosition = modelMatrix * vertexPosition;
    
    pPosition = (((vec3(vPosition) - cubeCenter) / cubeSize) + vec3(0.5));
    
    gl_Position =  modelViewProjectionMatrix * vertexPosition;
}