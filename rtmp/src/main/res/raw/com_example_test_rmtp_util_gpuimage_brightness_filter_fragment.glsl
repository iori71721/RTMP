precision mediump float;

varying vec2 vTextureCoord;
uniform sampler2D uSampler;
uniform lowp float brightness;
void main(){
    lowp vec4 textureColor = texture2D(uSampler, vTextureCoord);
    gl_FragColor = vec4((textureColor.rgb + vec3(brightness)), textureColor.w);
}