plugins {
    kotlin("jvm") version "1.8.21"
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
    implementation("org.graalvm.sdk:graal-sdk:22.3.2")
    implementation("org.graalvm.js:js:22.3.2")
    annotationProcessor("org.graalvm.truffle:truffle-dsl-processor:22.3.2")
    implementation("org.graalvm.truffle:truffle-dsl-processor:22.3.2")
    implementation("commons-io:commons-io:2.12.0")
    implementation("org.apache.commons:commons-compress:1.23.0")
    implementation("com.google.guava:guava:31.1-jre")
    // https://mvnrepository.com/artifact/org.graalvm.tools/lsp
    implementation("org.graalvm.tools:lsp:22.3.2")
// https://mvnrepository.com/artifact/org.graalvm.tools/lsp_api
    implementation("org.graalvm.tools:lsp_api:22.3.2")
}


tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(11)
}

application {
    mainClass.set("MainKt")
}