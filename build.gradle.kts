group = "ru.greenbudgie.uhc"
version = "4.0.0"
description = "Ultra Hardcore minecraft plugin with many custom features"

plugins {
    id("java")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

repositories {
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    mavenLocal()
    mavenCentral()
}

dependencies {
    implementation("org.spigotmc:spigot:26.2-R0.1-SNAPSHOT")
    implementation("org.spigotmc:spigot-api:26.2-R0.1-SNAPSHOT")

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.10.1")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.10.1")
    testImplementation("org.mockito:mockito-core:5.8.0")
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
    options.compilerArgs.addAll(listOf("-Xmaxerrs", "1000", "-Xmaxwarns", "1000"))
}

tasks.register("replacePlugin", Jar::class) {
    archiveBaseName.set("UHCPlugin")
    destinationDirectory.set(file("C:/Projects/Plugins/UHC/Server-26.2/plugins"))
    from(sourceSets.main.get().output)
}

tasks.test {
    useJUnitPlatform()
}