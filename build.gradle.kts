group = "ru.greenbudgie.uhc"
version = "1.0.0"
description = "Ultra Hardcore minecraft plugin with many custom features"

plugins {
    id("java")
    kotlin("jvm") version "1.9.21"
}

repositories {
    mavenLocal()
    mavenCentral()
    maven {
        url = uri("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    }
}

dependencies {
    implementation("org.spigotmc:spigot:1.20.2-R0.1-SNAPSHOT")

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.10.1")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.10.1")
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
}

tasks.register("replacePlugin", Jar::class) {
    archiveBaseName.set("UHCPlugin")
    destinationDirectory.set(file("C:/Projects/Plugins/UHC/Server/plugins"))
    from(sourceSets.main.get().output)
}