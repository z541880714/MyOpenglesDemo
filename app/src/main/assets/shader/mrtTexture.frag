#version 300 es
precision mediump float;
layout (location = 0) out vec4 fragData0;
layout (location = 1) out vec4 fragData1;
layout (location = 2) out vec4 fragData2;
layout (location = 3) out vec4 fragData3;

uniform sampler2D u_texture;
in vec2 v_texCoord;

const vec3 W = vec3(0.2125, 0.7154, 0.0721);
void main() {
    vec4 fragColor = texture(u_texture, v_texCoord);
    fragData0 = vec4(fragColor.r, 0., 0., 1.);
    fragData1 = vec4(0., fragColor.g, 0., 1.);
    fragData2 = vec4(0., 0., fragColor.b, 1.);

    float luminance = dot(fragColor.rgb, W);
    fragData3 = vec4(vec3(luminance), 1.);
}