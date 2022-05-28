package dev.buijs.klutter.core.tasks.adapter

import dev.buijs.klutter.core.Android
import dev.buijs.klutter.core.Flutter
import dev.buijs.klutter.core.IOS
import dev.buijs.klutter.core.Platform
import dev.buijs.klutter.core.Root
import dev.buijs.klutter.core.test.CompareMode
import dev.buijs.klutter.core.test.PluginProject
import spock.lang.Specification

import static dev.buijs.klutter.core.test.KlutterTest.*

/**
 * @author Gillian Buijs
 */
class GenerateAdapterTaskSpec extends Specification {

    def "Verify adapters are generated correctly for a plugin project"() {

        given:
        PluginProject sut = plugin { project, resources ->
            resources.copyAll([
                    "platform_source_code"    : project.platformSourceClass,
                    "android_app_manifest": project.androidManifest,
                    "build_gradle_plugin"     : project.buildGradle,
                    "plugin_pubspec"     : project.pubspecYaml,
            ])
        }

        when:
        def root = new Root(sut.root.absolutePath, false)
        new GenerateAdapterTask(
                new Android(sut.android, root),
                new IOS(sut.ios, root),
                new Flutter(sut.flutter, root),
                new Platform(root, new File("${sut.root}/klutter/${sut.pluginName}"), "", ""),
                sut.pluginName,
                true,
        ).run()

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