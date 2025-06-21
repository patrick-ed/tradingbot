import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    kotlin("jvm") version "2.1.10"
    kotlin("plugin.serialization") version "1.9.22"
    id("application")
    id("com.github.johnrengelman.shadow") version "8.1.1"
    application
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

val ktorVersion = "2.3.9"
val logbackVersion = "1.5.3"
val kotlinCoroutinesVersion = "1.8.0"
val exposedVersion = "0.49.0"


dependencies {
    // Ktor for HTTP and WebSockets
    implementation("io.ktor:ktor-client-core:2.3.8")
    implementation("io.ktor:ktor-client-cio:2.3.8") // Engine for Ktor
    implementation("io.ktor:ktor-client-content-negotiation:2.3.8")
    implementation("io.ktor:ktor-serialization-kotlinx-json:2.3.8")
    implementation("io.ktor:ktor-client-websockets:2.3.8")
    implementation("io.ktor:ktor-client-logging:2.3.8")

    // Kotlinx Serialization for JSON
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3")

    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.0")

    // Logging with SLF4J and Logback
    implementation("org.slf4j:slf4j-api:2.0.12")
    implementation("ch.qos.logback:logback-classic:1.5.3")

    // Dotenv for loading .env files
    implementation("io.github.cdimascio:dotenv-kotlin:6.4.1")

    // Exposed for PostgreSQL database interaction
    implementation("org.jetbrains.exposed:exposed-core:0.49.0")
    implementation("org.jetbrains.exposed:exposed-dao:0.49.0")
    implementation("org.jetbrains.exposed:exposed-jdbc:0.49.0")
    implementation("org.postgresql:postgresql:42.7.2") // PostgreSQL driver
}

tasks.test {
    useJUnitPlatform()
}
application {
    mainClass.set("main.MainKt")
}
kotlin {
    jvmToolchain(21)
}

tasks.withType<JavaExec> {
    if (name == "run") {
        standardInput = System.`in`
    }
}

/**
 * Custom task to build the fat JAR and run it immediately.
 *
 * Usage: ./gradlew bootRun
 */
tasks.register<JavaExec>("bootRun") {
    group = "application"
    description = "Builds the fat JAR and runs the application, enabling graceful shutdown."
    dependsOn(tasks.named("shadowJar"))
    val shadowJarTask = tasks.getByName<ShadowJar>("shadowJar")
    classpath = files(shadowJarTask.archiveFile)
    standardInput = System.`in`
}