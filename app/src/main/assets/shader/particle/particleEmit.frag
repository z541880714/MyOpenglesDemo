#version 300 es
precision mediump float;

layout(location = 0) out vec4 fragColor;

in vec4 vColor;
in vec4 vPosition;
void main() {
    fragColor = vec4(1.0);
}