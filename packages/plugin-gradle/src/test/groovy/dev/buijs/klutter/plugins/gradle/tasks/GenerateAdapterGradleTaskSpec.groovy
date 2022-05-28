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
        PluginProject sut = plugin { project, resources ->
            resources.copyAll([
                    "platform_source_code"    : project.platformSourceClass,
                    "android_app_manifest"    : project.androidManifest,
                    "build_gradle_plugin"     : project.buildGradle,
                    "plugin_pubspec"          : project.pubspecYaml,
            ])
        }

        when:
        sut.test("generateAdapters")

        then:
        sut.verify("flutter library dart class is generated") { project, resources ->
            project.hasChild(
                    "${sut.flutter.absolutePath}",
                    "super_awesome.dart",
                    "flutter_plugin_library",
                    CompareMode.IGNORE_SPACES
            )
        }

        sut.verify("method handler boilerplate should be added") { project, resources ->
            project.hasChild(
                    "${sut.androidMain.absolutePath}/kotlin/foo/bar/super_awesome",
                    "SuperAwesomePlugin.kt",
                    "android_plugin_class",
                    CompareMode.IGNORE_SPACES
            )
        }
    }

}