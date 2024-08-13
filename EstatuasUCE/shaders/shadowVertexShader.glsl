#version 330 core
layout (location = 0) in vec3 aPos;
uniform mat4 lightViewMatrix;
uniform mat4 lightProjMatrix;
void main() {
    gl_Position = lightProjMatrix * lightViewMatrix * vec4(aPos, 1.0);
}

