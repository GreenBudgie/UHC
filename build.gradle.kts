group = "ru.greenbudgie.uhc"
version = "4.0.0"
description = "Ultra Hardcore minecraft plugin with many custom features"

plugins {
    id("java")
}

repositories {
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    mavenLocal()
    mavenCentral()
}

dependencies {
    compileOnly("org.spigotmc:spigot:1.20.2-R0.1-SNAPSHOT")
    compileOnly("org.spigotmc:spigot-api:1.20.2-R0.1-SNAPSHOT")

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.10.1")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.10.1")
    testImplementation("org.mockito:mockito-core:5.8.0")
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
}

tasks.register("replacePlugin", Jar::class) {
    archiveBaseName.set("UHCPlugin")
    destinationDirectory.set(file("C:/Projects/Plugins/UHC/Server/plugins"))
    from(sourceSets.main.get().output)
}

tasks.test {
    useJUnitPlatform()
}