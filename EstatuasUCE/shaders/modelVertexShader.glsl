#version 330 core
layout (location = 0) in vec3 aPos;
out vec4 fragPos;
uniform mat4 viewMatrix;
uniform mat4 projectionMatrix;
void main() {
    fragPos = viewMatrix * vec4(aPos, 1.0);
    gl_Position = projectionMatrix * fragPos;
}
