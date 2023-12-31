#version 300 es
precision mediump float;

layout (location = 0) out vec4 fragColor;
in vec2 v_texCoord;

uniform sampler2D u_texture;
void main() {
    fragColor = texture(u_texture, v_texCoord);
}

