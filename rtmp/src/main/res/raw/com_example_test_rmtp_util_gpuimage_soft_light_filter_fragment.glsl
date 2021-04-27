varying highp vec2 vTextureCoord;
varying highp vec2 textureCoordinate2;

uniform sampler2D uSampler;
uniform sampler2D inputImageTexture2;

void main(){
    mediump vec4 base = texture2D(uSampler, vTextureCoord);
    mediump vec4 overlay = texture2D(inputImageTexture2, textureCoordinate2);

    lowp float alphaDivisor = base.a + step(base.a, 0.0);
    gl_FragColor = base * (overlay.a * (base / alphaDivisor) + (2.0 * overlay * (1.0 - (base / alphaDivisor)))) + overlay * (1.0 - base.a) + base * (1.0 - overlay.a);

}