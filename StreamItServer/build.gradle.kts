plugins {
    java
    kotlin("jvm") version "1.9.22"
    application
    id("org.openjfx.javafxplugin") version "0.0.14"
}

group = "com.app"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

javafx {
    version = "21.0.8"
    modules = listOf("javafx.controls", "javafx.fxml", "javafx.graphics")
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    implementation("com.esotericsoftware:kryonet:2.22.0-RC1")
    // https://mvnrepository.com/artifact/com.google.code.gson/gson
    implementation("com.google.code.gson:gson:2.13.2")
    // https://mvnrepository.com/artifact/net.bramp.ffmpeg/ffmpeg
    implementation("net.bramp.ffmpeg:ffmpeg:0.8.0")

    implementation("org.hibernate.orm:hibernate-core:7.1.4.Final")

    implementation("jakarta.persistence:jakarta.persistence-api:3.2.0")

    implementation("com.mysql:mysql-connector-j:9.4.0")
}

tasks.test {
    useJUnitPlatform()
}

application {
    mainClass.set("com.app.Main")
    applicationDefaultJvmArgs = listOf(
        "-Djava.util.logging.config.file=src/main/resources/logging.properties"
    )
}


java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions.jvmTarget = "21"
}