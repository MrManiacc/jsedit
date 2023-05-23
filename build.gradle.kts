plugins {
    kotlin("jvm") version "1.8.0"
    java
    application
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://jitpack.io/")
}

dependencies {
    testImplementation(kotlin("test"))
    implementation("io.github.spair:imgui-java-app:1.86.10")
    implementation("party.iroiro.luajava:luajit:3.4.0")
    runtimeOnly("party.iroiro.luajava:luajit-platform:3.4.0:natives-desktop")
    implementation("org.luaj:luaj-jse:3.0.1")
    implementation("com.esotericsoftware:kryo:4.0.0")
    implementation("org.antlr:antlr4-runtime:4.11.1")
    implementation("org.graalvm.js:js-scriptengine:22.3.2")
    implementation("org.graalvm.sdk:graal-sdk:22.3.2")
    implementation("org.graalvm.js:js:22.3.2")
    implementation("commons-io:commons-io:2.12.0")


}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(17)
}

application {
    mainClass.set("MainKt")
}