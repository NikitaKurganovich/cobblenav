plugins {
    id("com.github.johnrengelman.shadow")
    alias(libs.plugins.cobblenav.convention.base)
}

architectury {
    platformSetupLoomIde()
    neoForge()
}

val common: Configuration by configurations.creating {
    isCanBeResolved = true
    isCanBeConsumed = false
}

val bundle: Configuration by configurations.creating {
    isCanBeResolved = true
    isCanBeConsumed = false
}

configurations {
    getByName("compileClasspath").extendsFrom(common)
    getByName("runtimeClasspath").extendsFrom(common)
    getByName("developmentNeoForge").extendsFrom(common)
}

repositories {
    maven {
        name = "NeoForged"
        url = uri("https://maven.neoforged.net/releases")
    }
    maven {
        name = "Kotlin for Forge"
        url = uri("https://thedarkcolour.github.io/KotlinForForge/")
    }
}

dependencies {
    neoForge(libs.neoforge)

    common(projects.common) {
        targetConfiguration = "namedElements"
        isTransitive = false
    }
    bundle(projects.common) {
        targetConfiguration = "transfromProductionNeoForge"
    }


    modImplementation(libs.cobblemon.neoforge)
    modCompileOnly(libs.cobblemon.mal.neoforge)
    modCompileOnly(libs.cobblemon.counter.neoforge)

    implementation(libs.kotlinforforge.neoforge) {
        exclude(group = "net.neoforged.fancymodloader", module = "loader")
    }
}

tasks {
    processResources {
        inputs.property("version", project.version)

        filesMatching("META-INF/neoforge.mods.toml") {
            expand(mapOf("version" to project.version))
        }
    }

    shadowJar {
        configurations = listOf(bundle)
        archiveClassifier = "dev-shadow"
    }

    remapJar {
        inputFile.set(shadowJar.flatMap { it.archiveFile })
    }
}

