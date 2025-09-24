package internal

import org.gradle.accessors.dm.LibrariesForLibs
import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.the
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

internal val Project.buildLibs: LibrariesForLibs
    get() = the<LibrariesForLibs>()

internal val Project.buildJavaVersion: JavaVersion
    get() = JavaVersion.toVersion(buildLibs.versions.java.get().toInt())

internal val Project.buildJVMTarget: JvmTarget
    get() = JvmTarget.fromTarget(this.buildJavaVersion.majorVersion)

typealias ProjectPlugin = Plugin<Project>
