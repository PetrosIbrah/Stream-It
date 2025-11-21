plugins {
    java
    kotlin("jvm") version "1.9.22"
    application
    id("org.openjfx.javafxplugin") version "0.1.0"
}

group = "com.app"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

javafx {
    version = "21.0.8"
    modules = listOf("javafx.controls", "javafx.fxml", "javafx.graphics", "javafx.media", "javafx.swing")
}

dependencies {
    // Testing
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")

    // UI Libraries
    implementation("com.jfoenix:jfoenix:9.0.10")
    implementation("io.github.mkpaz:atlantafx-base:2.1.0")

    // JavaFX Modules
    implementation("org.openjfx:javafx-controls:21.0.8")
    implementation("org.openjfx:javafx-fxml:21.0.8")
    implementation("org.openjfx:javafx-graphics:21.0.8")
    implementation("org.openjfx:javafx-media:21.0.8")
    implementation("org.openjfx:javafx-swing:21.0.8")

    // implementation("org.slf4j:slf4j-simple:2.0.9")
    // VLCJ Libraries
    implementation("uk.co.caprica:vlcj:4.8.3")
    implementation("uk.co.caprica:vlcj-javafx:1.2.0")

    // Speedtest
    implementation("fr.bmartel:jspeedtest:1.32.1")

    // Logging
    implementation("org.apache.logging.log4j:log4j-core:2.25.2")
    implementation("org.apache.logging.log4j:log4j-api:2.25.2")

}


tasks.test {
    useJUnitPlatform()
}

application {
    mainClass.set("com.app.Main")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions.jvmTarget = "21"
}