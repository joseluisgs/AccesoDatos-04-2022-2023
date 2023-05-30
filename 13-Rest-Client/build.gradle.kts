import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.8.10"
    application
    // para serializar Json y otros
    kotlin("plugin.serialization") version "1.8.10"

    // Para ktorfit que usa KSP
    // Plugin KSP para generar código en tiempo de compilación ktorfit
    id("com.google.devtools.ksp") version "1.8.10-1.0.9"
    id("de.jensklingenberg.ktorfit") version "1.0.0"

    // SQLdelight
    id("app.cash.sqldelight") version "2.0.0-alpha05"
}

// Para Ktorfit
configure<de.jensklingenberg.ktorfit.gradle.KtorfitGradleConfiguration> {
    version = "1.3.0"
}

group = "es.joseluisgs"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))

    // Para hacer el logging
    implementation("io.github.microutils:kotlin-logging:3.0.4")
    implementation("ch.qos.logback:logback-classic:1.4.5")

    // Corrutinas
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")

    // Serializa Json con Kotlin
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.5.1")

    // Retrofit
    // https://mvnrepository.com/artifact/com.squareup.retrofit2/retrofit
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    // Para serializar con Kotlin Serialization en Retrofit
    implementation("com.jakewharton.retrofit:retrofit2-kotlinx-serialization-converter:0.8.0")

    // Ktorfit, es decir Ktor client modificado para parecerse a Retrofit
    ksp("de.jensklingenberg.ktorfit:ktorfit-ksp:1.3.0")
    implementation("de.jensklingenberg.ktorfit:ktorfit-lib:1.3.0")

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
    // Result
    implementation("com.michael-bull.kotlin-result:kotlin-result:1.1.17")

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

sqldelight {
    databases {
        // Nombre de la base de datos
        create("AppDatabase") {
            // Paquete donde se generan las clases
            packageName.set("dev.joseluisgs.database")
        }
    }
}
