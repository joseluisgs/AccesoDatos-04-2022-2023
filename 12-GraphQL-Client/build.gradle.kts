import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.7.21"
    application
    // para serializar Json y otros
    kotlin("plugin.serialization") version "1.7.20"
    // Apollo Client
    id("com.apollographql.apollo3").version("3.7.1")
}

group = "es.joseluisgs"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))

    // Para hacer el logging
    implementation("io.github.microutils:kotlin-logging-jvm:3.0.4")
    implementation("ch.qos.logback:logback-classic:1.4.5")

    // Corrutinas
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")

    // Serializa Json con Kotlin
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.4.1")

    // Apollo Client
    implementation("com.apollographql.apollo3:apollo-runtime:3.7.1")
    // optional: if you want to use the normalized cache
    implementation("com.apollographql.apollo3:apollo-normalized-cache:3.7.1")
    // implementation("com.apollographql.apollo3:apollo-normalized-cache-sqlite:3.7.1")
    // optional: if you just want the generated models and parsers and write your own HTTP code/cache code, you can remove apollo-runtime
    // and use apollo-api instead
    // implementation("com.apollographql.apollo3:apollo-api:3.7.1")
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

// Le indicamos a Apollo que genere en Kotlin los modelos POKO
apollo {
    // Donde se encuentran los modelos POKO
    generateKotlinModels.set(true)
    packageName.set("graphql.rocket")
    // Si queremos que genere datos falsos para las pruebas
    generateTestBuilders.set(true)
}