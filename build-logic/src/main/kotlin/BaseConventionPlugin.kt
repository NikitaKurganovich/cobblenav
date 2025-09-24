import dev.architectury.plugin.ArchitectPluginExtension
import internal.ProjectPlugin
import internal.buildJavaVersion
import internal.buildLibs
import internal.mappings
import internal.minecraft
import net.fabricmc.loom.api.LoomGradleExtensionAPI
import org.gradle.api.Project
import org.gradle.api.plugins.BasePluginExtension
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.withType

internal class BaseConventionPlugin : ProjectPlugin {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply(buildLibs.plugins.kotlin.jvm.get().pluginId)
                apply("dev.architectury.loom")
                apply("maven-publish")
                apply("architectury-plugin")
            }
            configure<BasePluginExtension> {
                archivesName.set("${rootProject.name}-${project.name}")
            }

            configure<LoomGradleExtensionAPI> {
                silentMojangMappingsLicense()
            }
            val loom = extensions.getByType<LoomGradleExtensionAPI>()
            dependencies {
                minecraft(buildLibs.minecraft)
                mappings(loom.officialMojangMappings())
            }
            configure<JavaPluginExtension> {
                withSourcesJar()

                sourceCompatibility = buildJavaVersion
                targetCompatibility = buildJavaVersion
            }

            configure<ArchitectPluginExtension> {
                minecraft = buildLibs.versions.minecraft.get()
            }
            tasks.withType<JavaCompile>().configureEach {
                options.release.set(21)
            }
            configure<PublishingExtension> {
                // TODO add proper configuration later
            }
        }
    }
}