import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import net.fabricmc.loom.task.RemapJarTask
import org.gradle.kotlin.dsl.getByName

plugins {
    id("com.github.johnrengelman.shadow")
    alias(libs.plugins.cobblenav.convention.base)
}

architectury {
    platformSetupLoomIde()
    neoForge()
}

val commonBundle: Configuration by configurations.creating {
    isCanBeConsumed = false
    isCanBeResolved = true
}

val shadowBundle: Configuration by configurations.creating {
    isCanBeConsumed = false
    isCanBeResolved = true
}

// TODO ask about this
//configurations {
//    compileClasspath.extendsFrom common
//    runtimeClasspath.extendsFrom common
//    developmentNeoForge.extendsFrom common
//}

dependencies {
    neoForge(libs.neoforge)

    //common(project(path: ':common', configuration: 'namedElements')) { transitive false }
    shadowBundle(project(path = ":common", configuration = "transformProductionNeoForge"))

    modImplementation(libs.cobblemon.neoforge)
    modCompileOnly(libs.cobblemon.mal.neoforge)
    modCompileOnly(libs.cobblemon.counter.neoforge)

    implementation(libs.kotlin.forge) {
        exclude(group = "net.neoforged.fancymodloader", module = "loader")
    }
}

tasks.getByName<ProcessResources>("processResources") {
    inputs.property("version", project.version)

    filesMatching("META-INF/neoforge.mods.toml") {
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
