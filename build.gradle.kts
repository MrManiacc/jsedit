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
    implementation("com.esotericsoftware:kryo:4.0.0")
    implementation("org.graalvm.js:js-scriptengine:22.3.2")
    implementation("org.graalvm.sdk:graal-sdk:22.3.2")
    implementation("org.graalvm.js:js:22.3.2")
    implementation("commons-io:commons-io:2.12.0")
    implementation("org.apache.commons:commons-compress:1.23.0")
    implementation("com.google.guava:guava:31.1-jre")
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