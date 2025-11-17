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
    implementation("org.openjfx:javafx-controls:21.0.5")
    implementation("org.openjfx:javafx-fxml:21.0.5")
    implementation("org.openjfx:javafx-graphics:21.0.5")
    implementation("org.openjfx:javafx-media:21.0.5")
    implementation("org.openjfx:javafx-swing:21.0.5") // required for SwingNode

    // VLCJ
    // implementation("uk.co.caprica:vlcj:4.9.0")
    implementation("org.slf4j:slf4j-simple:2.0.9")
    // https://mvnrepository.com/artifact/uk.co.caprica/vlcj
    implementation("uk.co.caprica:vlcj:4.8.3")

    // https://mvnrepository.com/artifact/uk.co.caprica/vlcj-javafx
    implementation("uk.co.caprica:vlcj-javafx:1.2.0")

    // https://mvnrepository.com/artifact/fr.bmartel/jspeedtest
    implementation("fr.bmartel:jspeedtest:1.32.1")

    // https://mvnrepository.com/artifact/uk.co.caprica/vlcj-javafx
    // implementation("com.github.caprica:vlcj-javafx:1.2.0")
    // implementation("uk.co.caprica:vlcj-javafx:1.3.0")
    // implementation("com.github.caprica:vlcj-javafx:1.3.0")
    // Optional: VLCJ JavaFX integration if you want direct rendering
    // implementation("uk.co.caprica:vlcj-javafx:4.9.0")
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