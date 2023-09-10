#version 300 es

/**
 使用 ubo 存储 uniform 数据.. ,后直接读取..
*/
layout (std140) uniform UniformBlock {
    vec4 screenSize;
    float pointSize;
};

layout (location = 0) in vec4 aPosition;
layout (location = 1) in vec4 aColor;

out vec4 vColor;
out vec4 vPosition;


out vec4 position; //当前的位置..
out vec2 speed; // 当前的速度..
out float lifeTime; //生命周期.. 由 1.0 -> 0.0, 与 alpha 对应.,  每一帧 减 0.05,


// 模拟随机数..
float random(float x)
{
    float y = fract(sin(x) * 100000.0);
    return y;
}



void main() {
    // 以屏幕坐标 转换为 中心坐标.. 范围为 [-1 , 1]


    if (all(equal(position.xy, vec2(0., 0.)))) {
        speed = vec2(1.) + speed;
        lifeTime = 100.;
        position = aPosition;
    }

    vec2 coord = position.xy / screenSize.xy * 2. - 1.;
    coord.y *= -1.;

    gl_Position = vec4(coord, 0., 1.);

    vPosition = position;
    vColor = vec4(1., 1., 1., 1.);
    gl_PointSize = pointSize;

    speed = vec2(1., 1.) + speed;
    lifeTime = 100.;
    position = position + 1.;
}