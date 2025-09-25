import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import net.fabricmc.loom.task.RemapJarTask
import org.gradle.kotlin.dsl.getByName

plugins {
    id("com.github.johnrengelman.shadow")
    alias(libs.plugins.cobblenav.convention.base)
}

architectury {
    platformSetupLoomIde()
    fabric()
}

val commonBundle: Configuration by configurations.creating {
    isCanBeConsumed = false
    isCanBeResolved = true
}

val shadowBundle: Configuration by configurations.creating {
    isCanBeConsumed = false
    isCanBeResolved = true
}

configurations {
    // TODO ask about this
//    compileClasspath.extendsFrom(commonBundle)
//    runtimeClasspath.extendsFrom(commonBundle)
//    developmentFabric.extendsFrom(common)
}

dependencies {
    modImplementation(libs.fabric.loader)
    modImplementation(libs.fabric.kotlin)

    modImplementation(libs.fabric.api)
    modImplementation(libs.cobblemon.fabric)

    modCompileOnly(libs.cobblemon.mal.fabric)
    modCompileOnly(libs.cobblemon.counter.fabric)
        // TODO ask about this
//    common(project(path = ":common", configuration = "namedElements")) {
//        transitive = false
//    }
    shadowBundle(project(path = ":common", configuration = "transformProductionFabric"))
}

tasks.getByName<ProcessResources>("processResources") {
    inputs.property("version", project.version)

    filesMatching("fabric.mod.json") {
        expand(mapOf("version" to project.version))
    }
}

val shadowJar = tasks.getByName<ShadowJar>("shadowJar")

shadowJar.apply {
    configurations.add(shadowBundle)
    archiveClassifier.set("dev-shadow")
}

tasks.getByName<RemapJarTask>("remapJar") {
    inputFile = shadowJar.archiveFile
}
