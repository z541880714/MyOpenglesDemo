#version 300 es
precision highp float;

/**
 使用 ubo 存储 uniform 数据.. ,后直接读取..
*/
layout (std140) uniform UniformBlock {
    vec4 screenSize;
    float pointSize;
};

layout (location = 0) out vec4 frag_color;

in vec4 vColor;
in vec4 vPosition;
void main() {
    vec2 coord = gl_FragCoord.xy;
    coord.y = screenSize.y - coord.y;
    float dis = distance(coord, vPosition.xy);
    if (dis > pointSize / 2.) {
        discard;
    }
    frag_color = vColor;


}