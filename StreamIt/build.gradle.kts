plugins {
    java
    kotlin("jvm") version "1.9.22"
    application
    id("org.openjfx.javafxplugin") version "0.1.0"
    id("org.panteleyev.jpackageplugin") version "1.6.1"
}

group = "com.app"
version = "1.0"

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

    // Switch Button
    implementation("org.controlsfx:controlsfx:11.2.1")

    // JavaFX Modules
    // implementation("org.openjfx:javafx-controls:21.0.8")
    // implementation("org.openjfx:javafx-fxml:21.0.8")
    // implementation("org.openjfx:javafx-graphics:21.0.8")
    // implementation("org.openjfx:javafx-media:21.0.8")
    // implementation("org.openjfx:javafx-swing:21.0.8")

    // VLCJ Libraries
    implementation("uk.co.caprica:vlcj:4.8.3")
    implementation("uk.co.caprica:vlcj-javafx:1.2.0")

    // Speedtest
    implementation("fr.bmartel:jspeedtest:1.32.1")

    // Logging
    implementation("org.apache.logging.log4j:log4j-core:2.25.2")
    implementation("org.apache.logging.log4j:log4j-api:2.25.2")

    // Recordings
    implementation("org.bytedeco:javacv:1.5.12")
    implementation("org.bytedeco:javacv-platform:1.5.12")
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

tasks.jar {
    manifest {
        attributes(
            "Main-Class" to "com.app.Main"
        )
    }
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    from(configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) })
    exclude("module-info.class")
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions.jvmTarget = "21"
}


tasks.register<org.panteleyev.jpackage.JPackageTask>("jpackageWin") {
    type = org.panteleyev.jpackage.ImageType.MSI
    appName = "StreamIt"
    appVersion = "1.0.0"
    vendor = "com.app"
    mainJar = "StreamIt-1.0.jar"
    mainClass = "com.app.Main"
    input = "build/libs"
    destination = "build/dist/windows"
    modulePaths = listOf("build/libs")
    winShortcut = true
    icon = "src/main/resources/com/app/StreamIt.ico"
    addModules = listOf(
        "javafx.controls",
        "javafx.fxml",
        "javafx.graphics",
        "javafx.media",
        "javafx.swing",
        "java.management",
        "java.naming"
    )
}

tasks.register<org.panteleyev.jpackage.JPackageTask>("jpackageLinux") {
    type = org.panteleyev.jpackage.ImageType.DEB
    appName = "StreamIt"
    appVersion = "1.0.0"
    vendor = "com.app"
    mainJar = "StreamIt-1.0.jar"
    mainClass = "com.app.Main"
    input = "build/libs"
    destination = "build/dist/linux"
    modulePaths = listOf("build/libs")
    linuxShortcut = true
    icon = "src/main/resources/com/app/StreamIt.png"
    addModules = listOf(
        "javafx.controls",
        "javafx.fxml",
        "javafx.graphics",
        "javafx.media",
        "javafx.swing",
        "java.management",
        "java.naming"
    )
}

tasks.register<Copy>("copyDeps") {
    from(configurations.runtimeClasspath)
    into("build/libs")
}

tasks.named("jpackageWin") {
    dependsOn("copyDeps", "jar")
}

tasks.named("jpackageLinux") {
    dependsOn("copyDeps", "jar")
}