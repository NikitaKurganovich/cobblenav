plugins {
    id("com.github.johnrengelman.shadow")
    alias(libs.plugins.cobblenav.convention.base)
}

architectury {
    platformSetupLoomIde()
    neoForge()
}
val neoforge_version: String by project
val mal_neoforge_version: String by project
val counter_neoforge_version: String by project
val kotlinforforge_version: String by project
val cobblemon_version: String by project

configurations {
    create("common") {
        isCanBeResolved = true
        isCanBeConsumed = false
    }
    getByName("compileClasspath").extendsFrom(configurations.getByName("common"))
    getByName("runtimeClasspath").extendsFrom(configurations.getByName("common"))
    getByName("developmentNeoForge").extendsFrom(configurations.getByName("common"))

    // Files in this configuration will be bundled into your mod using the Shadow plugin.
    // Don't use the `shadow` configuration from the plugin itself as it's meant for excluding files.
    create("shadowBundle") {
        isCanBeResolved = true
        isCanBeConsumed = false
    }
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
    neoForge("net.neoforged:neoforge:$neoforge_version")

    add("common", project(path = ":common", configuration = "namedElements")) { isTransitive = false }
    add("shadowBundle", project(path = ":common", configuration = "transformProductionNeoForge"))

    modImplementation("com.cobblemon:neoforge:$cobblemon_version")
    modCompileOnly("maven.modrinth:cobblemon-myths-and-legends-sidemod:$mal_neoforge_version")
    modCompileOnly("maven.modrinth:cobblemon-counter:$counter_neoforge_version")

    implementation("thedarkcolour:kotlinforforge-neoforge:$kotlinforforge_version") {
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
    configurations = listOf(project.configurations.getByName("shadowBundle"))
        archiveClassifier = "dev-shadow"
    }

    remapJar {
        inputFile.set(shadowJar.flatMap { it.archiveFile })
    }
}

