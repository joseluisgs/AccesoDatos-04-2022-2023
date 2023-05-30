plugins {
    kotlin("jvm") version "1.8.21"
    application
    // para serializar Json y otros
    kotlin("plugin.serialization") version "1.8.21"
    // KSP Para ktorfit y Kotlin Annotations que usa KSP
    id("com.google.devtools.ksp") version "1.8.21-1.0.11"
    id("de.jensklingenberg.ktorfit") version "1.0.0"
    // SQLdelight
    id("app.cash.sqldelight") version "2.0.0-alpha05"
}

// Para Ktorfit
configure<de.jensklingenberg.ktorfit.gradle.KtorfitGradleConfiguration> {
    version = "1.4.0"
}

group = "dev.joseluisgs"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    // Para hacer el logging
    implementation("io.github.microutils:kotlin-logging:3.0.4")
    implementation("ch.qos.logback:logback-classic:1.4.5")

    // Corrutinas
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.0")

    // Serializa Json con Kotlin
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.5.1")

    // Ktorfit, es decir Ktor client modificado para parecerse a Retrofit
    ksp("de.jensklingenberg.ktorfit:ktorfit-ksp:1.4.0")
    implementation("de.jensklingenberg.ktorfit:ktorfit-lib:1.4.0")

    // Para serializar en Json con Ktor
    implementation("io.ktor:ktor-client-serialization:2.3.0")
    implementation("io.ktor:ktor-client-content-negotiation:2.3.0")
    implementation("io.ktor:ktor-serialization-kotlinx-json:2.3.0")

    // SqlDeLight
    implementation("app.cash.sqldelight:sqlite-driver:2.0.0-alpha05")
    // SQLite para SqlDeLight native
    // implementation("com.squareup.sqldelight:sqlite-driver:1.5.4")
    // Para poder usar corrutias en SqlDeLight y conectarnos a la base de datos para cambios
    implementation("app.cash.sqldelight:coroutines-extensions:2.0.0-alpha05")

    // Koin
    implementation("io.insert-koin:koin-core:3.4.0")
    implementation("io.insert-koin:koin-annotations:1.2.0")
    ksp("io.insert-koin:koin-ksp-compiler:1.2.0")
    implementation("io.insert-koin:koin-logger-slf4j:3.4.0")

    // Result
    implementation("com.michael-bull.kotlin-result:kotlin-result:1.1.17")


    testImplementation(kotlin("test"))
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

sqldelight {
    databases {
        // Nombre de la base de datos
        create("AppDatabase") {
            // Paquete donde se generan las clases
            packageName.set("dev.joseluisgs.database")
        }
    }
}

