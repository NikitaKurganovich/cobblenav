plugins {
    id("com.github.johnrengelman.shadow")
    alias(libs.plugins.cobblenav.convention.base)
}

architectury {
    platformSetupLoomIde()
    fabric()
}

val bundle: Configuration by configurations.creating {
    isCanBeConsumed = false
    isCanBeResolved = true
}

loom {
    mods.maybeCreate("main")
    mods.named("main") {
        sourceSet(project.sourceSets.main.get())
        sourceSet(projects.common.dependencyProject.sourceSets.main.get())
    }
}

dependencies {
    modImplementation(libs.fabric.loader)
    modImplementation(libs.fabric.kotlin)
    modImplementation(libs.fabric.api)

    modImplementation(libs.cobblemon.fabric)

    modCompileOnly(libs.cobblemon.mal.fabric)
    modCompileOnly(libs.cobblemon.counter.fabric)

    implementation(projects.common) {
        targetConfiguration = "namedElements"
        isTransitive = false
    }
    bundle(projects.common) {
        targetConfiguration = "transformProductionFabric"
        isTransitive = false
    }
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

