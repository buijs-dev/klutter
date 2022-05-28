package dev.buijs.klutter.plugins.gradle.tasks

import dev.buijs.klutter.core.test.CompareMode
import dev.buijs.klutter.core.test.PluginProject
import spock.lang.Specification

import static dev.buijs.klutter.core.test.KlutterTest.plugin

/**
 * @author Gillian Buijs
 */
class GenerateAdapterGradleTaskSpec extends Specification {

    def "Verify adapters are generated correctly for a plugin project"() {

        given:
        PluginProject sut = plugin {project, resources ->
            resources.copyAll([
                    "platform_source_code"    : project.platformSourceClass,
                    "android_app_manifest.xml": project.androidManifest,
                    "android_main_activity"   : project.androidMainActivity,
                    "build_gradle_plugin"     : project.buildGradle,
            ])
        }

        when:
        sut.test("generateAdapters")

        then:
        sut.verify("GeneratedKlutterAdapter.kt should exist") { project, resources ->
            project.hasChild(
                    "${sut.android.absolutePath}/src/main/java/dev/buijs/klutter/adapter",
                   "GeneratedKlutterAdapter.kt",
                     "android_adapter",
                   CompareMode.IGNORE_SPACES
            )
        }

    }

}