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
    testImplementation("org.junit.jupiter:junit-jupiter:5.8.1")
    annotationProcessor("org.graalvm.truffle:truffle-dsl-processor:22.3.2")
    implementation("org.graalvm.truffle:truffle-dsl-processor:22.3.2")
    implementation("commons-io:commons-io:2.12.0")
    implementation("org.apache.commons:commons-compress:1.23.0")
    implementation("com.google.guava:guava:31.1-jre")
    implementation("org.graalvm.tools:lsp:22.3.2")
    implementation("org.graalvm.tools:lsp_api:22.3.2")
    testImplementation("io.mockk:mockk:1.13.5")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.1")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.1")
    runtimeOnly("org.jetbrains.kotlinx:kotlinx-coroutines-core-jvm:1.7.1")

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