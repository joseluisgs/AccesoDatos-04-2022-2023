import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.7.20"
    application
    // para serializar Json y otros
    kotlin("plugin.serialization") version "1.7.20"
}

group = "es.joseluisgs"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    // Instalamos KMongo para manejar MongoDB
    implementation("org.litote.kmongo:kmongo:4.7.1")

    // Para hacer el logging
    implementation("io.github.microutils:kotlin-logging-jvm:3.0.4")
    implementation("ch.qos.logback:logback-classic:1.4.5")

    // Serializa Json
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.4.1")

    // Testing
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

application {
    mainClass.set("MainKt")
}