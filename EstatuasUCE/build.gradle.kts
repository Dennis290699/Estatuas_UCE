plugins {
   
    id("java")
}

repositories {
   
    mavenCentral()
  //  jcenter()
}
java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}
dependencies {
   
    implementation("org.lwjgl:lwjgl:3.3.1")
    implementation ("org.lwjgl:lwjgl-glfw:3.3.1")
    implementation ("org.lwjgl:lwjgl-opengl:3.3.1")
    implementation("org.joml:joml:1.10.5")
    
    runtimeOnly ("org.lwjgl:lwjgl:3.3.1:natives-windows")
    runtimeOnly ("org.lwjgl:lwjgl-glfw:3.3.1:natives-windows")
    runtimeOnly ("org.lwjgl:lwjgl-opengl:3.3.1:natives-windows")
    
    //iMonkeyEngine
    
    implementation("org.jmonkeyengine:jme3-core:3.3.2-stable")
    implementation("org.jmonkeyengine:jme3-desktop:3.3.2-stable")
    implementation("org.jmonkeyengine:jme3-lwjgl:3.3.2-stable")
    implementation("org.jmonkeyengine:jme3-lwjgl3:3.3.2-stable")
    implementation("org.jmonkeyengine:jme3-effects:3.3.2-stable")
    implementation("org.jmonkeyengine:jme3-jogg:3.3.2-stable")
    implementation("org.jmonkeyengine:jme3-core:3.6.1-stable")
  
   //otrasopciones a OBJLoader
   
  //  implementation ("com.github.wvxvw:objparser:1.2.1")
   // implementation ("com.github.syoyo:tinyobjloader:1.4.0")
  //  implementation("de.javagl:obj:0.4.0")
    
}

