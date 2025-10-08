plugins {
    id("com.github.johnrengelman.shadow")
}

architectury {
    platformSetupLoomIde()
    fabric()
}

val bundle: Configuration by configurations.creating {
    isCanBeConsumed = false
    isCanBeResolved = true
}

val fabric_loader_version: String by project
val fabric_kotlin_version: String by project
val fabric_api_version: String by project
val cobblemon_version: String by project
val mal_fabric_version: String by project
val counter_fabric_version: String by project

loom {
    mods.maybeCreate("main")
    mods.named("main") {
        sourceSet(project.sourceSets.main.get())
        sourceSet(project(":common").sourceSets.main.get())
    }
}

dependencies {
    modImplementation("net.fabricmc:fabric-loader:$fabric_loader_version")
    modImplementation("net.fabricmc:fabric-language-kotlin:$fabric_kotlin_version")

    modImplementation("net.fabricmc.fabric-api:fabric-api:$fabric_api_version")
    modImplementation("com.cobblemon:fabric:$cobblemon_version")
    modCompileOnly("maven.modrinth:cobblemon-myths-and-legends-sidemod:$mal_fabric_version")
    modCompileOnly("maven.modrinth:cobblemon-counter:$counter_fabric_version")

    implementation(project(path = ":common", configuration = "namedElements")) { isTransitive = false }
    bundle(project(path = ":common", configuration = "transformProductionFabric")){ isTransitive = false }
}

tasks {
    processResources {
        inputs.property("version", project.version)

        filesMatching("fabric.mod.json") {
            expand(
                "version" to project.version
            )
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

