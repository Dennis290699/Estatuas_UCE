#version 330 core
in vec4 fragPos;
out vec4 color;
uniform sampler2D shadowMap;
void main() {
    float shadow = texture(shadowMap, fragPos.xy).r;
    color = vec4(vec3(1.0 - shadow), 1.0);
}
