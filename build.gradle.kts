plugins {
    id("dev.architectury.loom") version "1.10-SNAPSHOT" apply false
    id("architectury-plugin") version "3.4-SNAPSHOT"
    id("com.github.johnrengelman.shadow") version "8.1.1" apply false
    id("org.jetbrains.kotlin.jvm") version "2.1.0"
}

// Access properties using the proper Kotlin DSL syntax - variable names must match gradle.properties exactly
val mod_version: String by project
val maven_group: String by project
val minecraft_version: String by project

architectury {
    minecraft = minecraft_version
}

allprojects {
    group = maven_group
    version = mod_version
}