plugins {
    `kotlin-dsl`
}

dependencies {
    // Workaround for version catalog working inside precompiled scripts
    // Issue - https://github.com/gradle/gradle/issues/15383
    compileOnly(files(libs.javaClass.superclass.protectionDomain.codeSource.location))
    compileOnly(libs.bundles.gradleplugins)
}

gradlePlugin {
    plugins {
        register("base.convention") {
            id = libs.plugins.cobblenav.convention.base.get().pluginId
            implementationClass = "BaseConventionPlugin"
        }
    }
}

