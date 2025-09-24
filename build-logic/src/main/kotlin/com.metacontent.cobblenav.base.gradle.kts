import internal.buildLibs
import internal.buildJavaVersion

plugins {
    kotlin("jvm")
    id("dev.architectury.loom")
    id("maven-publish")
    id("architectury-plugin")
}

base {
    archivesName = "${rootProject.name}-${project.name}"
}

loom {
    silentMojangMappingsLicense()
}

dependencies {
    minecraft(buildLibs.minecraft)
    mappings(loom.officialMojangMappings())
}

java {
    withSourcesJar()

    sourceCompatibility = buildJavaVersion
    targetCompatibility = buildJavaVersion
}

architectury {
    minecraft = buildLibs.versions.minecraft.get()
}
tasks.withType<JavaCompile>().configureEach {
    options.release.set(21)
}

publishing {
    // TODO add proper configuration later
}