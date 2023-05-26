plugins {
    kotlin("jvm")
    java
}

group = "me.jraynor"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://jitpack.io/")
}

dependencies {
    testImplementation(kotlin("test"))
    implementation("commons-io:commons-io:2.12.0")
    implementation("org.apache.commons:commons-compress:1.23.0")
    implementation("com.google.guava:guava:31.1-jre")
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

