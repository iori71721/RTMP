attribute vec4 aPosition;
attribute vec4 aTextureCoord;
attribute vec4 inputTextureCoordinate2;

varying vec2 vTextureCoord;
varying vec2 textureCoordinate2;

void main(){
    gl_Position = aPosition;
    vTextureCoord = aTextureCoord.xy;
    textureCoordinate2 = inputTextureCoordinate2.xy;
}