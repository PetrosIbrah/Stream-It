plugins {
    java
    kotlin("jvm") version "1.9.22"
    application
}

group = "com.app"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.google.code.gson:gson:2.13.2")
    implementation("net.bramp.ffmpeg:ffmpeg:0.8.0")

    implementation("org.hibernate.orm:hibernate-core:6.6.38.Final")
    implementation("jakarta.persistence:jakarta.persistence-api:3.2.0")
    implementation("com.mysql:mysql-connector-j:9.5.0")

    // Logging
    implementation("org.apache.logging.log4j:log4j-core:2.25.2")
    implementation("org.apache.logging.log4j:log4j-api:2.25.2")
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