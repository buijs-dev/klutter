package dev.buijs.klutter.plugins.gradle.tasks

import dev.buijs.klutter.core.test.CompareMode
import spock.lang.Specification

import static dev.buijs.klutter.core.test.KlutterTest.plugin

/**
 * @author Gillian Buijs
 */
class GenerateAdapterGradleTaskSpec extends Specification {

    def "Verify adapters are generated correctly for a plugin project"() {

        given:
        def plugin = plugin { project, resources ->
            resources.copyAll([
                    "platform_source_code"    : project.platformSourceClass,
                    "android_app_manifest"    : project.androidManifest,
                    "build_gradle_plugin"     : project.platformBuildGradle,
                    "settings_gradle_plugin"  : project.rootSettingsGradle,
                    "plugin_pubspec"          : project.pubspecYaml,
            ])
        }

        when:
        GenerateAdapterGradleTaskKt.asPlugin(plugin.pluginName, plugin.root.path)

        then:
        plugin.verify("flutter library dart class is generated") { project, resources ->
            project.hasChild(
                    "${plugin.flutter.absolutePath}",
                    "super_awesome.dart",
                    "flutter_plugin_library",
                    CompareMode.IGNORE_SPACES
            )
        }

        plugin.verify("method handler boilerplate should be added") { project, resources ->
            project.hasChild(
                    "${plugin.androidMain.absolutePath}/kotlin/foo/bar/super_awesome",
                    "SuperAwesomePlugin.kt",
                    "android_plugin_class",
                    CompareMode.IGNORE_SPACES
            )
        }
    }

}