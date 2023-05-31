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
    implementation("io.github.spair:imgui-java-app:1.86.10")
    implementation("commons-io:commons-io:2.12.0")
    implementation("com.esotericsoftware:kryo:4.0.0")
    implementation("org.apache.commons:commons-compress:1.23.0")
    implementation("com.google.guava:guava:31.1-jre")
    testImplementation("io.mockk:mockk:1.13.5")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.1")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.1")
    testImplementation("org.junit.jupiter:junit-jupiter:5.8.1")
    runtimeOnly("org.jetbrains.kotlinx:kotlinx-coroutines-core-jvm:1.7.1")
}



tasks.test {
    useJUnitPlatform()
}
//Shades all of the dependencies into the jar
tasks.jar {
    manifest {
        attributes(
            "Main-Class" to "MainKt"
        )
    }
    from(configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) })
    archiveFileName.set("osgui-${project.version}.jar")
    destinationDirectory.set(file("build/libs"))
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

kotlin {
    jvmToolchain(11)
}

