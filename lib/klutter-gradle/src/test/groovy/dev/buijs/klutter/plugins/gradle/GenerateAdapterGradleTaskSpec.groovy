package dev.buijs.klutter.plugins.gradle

import dev.buijs.klutter.core.test.TestUtil
import dev.buijs.klutter.core.test.TestPlugin
import dev.buijs.klutter.core.test.TestResource
import spock.lang.Ignore
import spock.lang.Shared
import spock.lang.Specification

class GenerateAdapterGradleTaskSpec extends Specification {

    @Shared
    def resources = new TestResource()

    def "Verify adapters are generated correctly for a plugin project"() {

        given:
        def plugin = new TestPlugin()

        and:
        with(resources){
            it.copy( "platform_source_code", plugin.platformSourceFile)
            it.copy( "android_app_manifest", plugin.manifest)
            it.copy( "settings_gradle_plugin", plugin.rootSettingsGradle)
            it.copy( "plugin_pubspec", plugin.pubspecYaml)
            it.copy("plugin_ios_podspec", plugin.iosPodspec)
        }

        and:
        plugin.platformBuildGradle.write(klutterConfig)

        when:
        plugin.test(plugin.platform, "klutterGenerateAdapters")

        then: "flutter library dart class is generated"
        with(new File("${plugin.libFolder.absolutePath}/super_awesome.dart")){
            it.exists()
            TestUtil.verify(it.text, resources.load("flutter_plugin_library"))
        }

        then: "method handler boilerplate should be added"
        with(new File("${plugin.androidSrcMain.absolutePath}/kotlin/foo/bar/super_awesome/SuperAwesomePlugin.kt")){
            it.exists()
            TestUtil.verify(it.text, resources.load("android_plugin_class"))
        }

        where:
        klutterConfig << [
                // No Klutter DSL block
                """
                plugins {
                  id("dev.buijs.klutter.gradle")
                } """,

                // Empty Klutter DSL block
                """
                    plugins {
                      id("dev.buijs.klutter.gradle")
                    }

                    klutter {  }""",

                // Empty plugin block
                """
                    plugins {
                      id("dev.buijs.klutter.gradle")
                    }

                    klutter {
                        plugin { }
                    }""",

                // Specified plugin name
                """
                    plugins {
                      id("dev.buijs.klutter.gradle")
                    }

                    klutter {
                        root = rootProject.rootDir
                        plugin {
                            name = "super_awesome"
                        }
                    }""",

                // Specified root folder and plugin name
                """
                    plugins {
                      id("dev.buijs.klutter.gradle")
                    }

                    klutter {
                        root = rootProject.rootDir
                        plugin {
                            name = "super_awesome"
                        }
                    }"""
        ]
    }

    def "Verify klutter dependency handler applies correct dependencies"() {

        given:
        def plugin = new TestPlugin()

        and:
        with(resources){
            it.copy( "platform_source_code", plugin.platformSourceFile)
            it.copy( "android_app_manifest", plugin.manifest)
            it.copy( "settings_gradle_plugin", plugin.rootSettingsGradle)
            it.copy( "plugin_pubspec", plugin.pubspecYaml)
            it.copy("plugin_ios_podspec", plugin.iosPodspec)
        }

        and:
        plugin.platformBuildGradle.write(klutterConfig)

        when:
        plugin.test(plugin.platform, "klutterGenerateAdapters")

        then: "flutter library dart class is generated"
        with(new File("${plugin.libFolder.absolutePath}/super_awesome.dart")){
            it.exists()
            TestUtil.verify(it.text, resources.load("flutter_plugin_library"))
        }

        then: "method handler boilerplate should be added"
        with(new File("${plugin.androidSrcMain.absolutePath}/kotlin/foo/bar/super_awesome/SuperAwesomePlugin.kt")){
            it.exists()
            TestUtil.verify(it.text, resources.load("android_plugin_class"))
        }

        where:
        klutterConfig << [
                // No Klutter DSL block
                """
                plugins {
                  id("dev.buijs.klutter.gradle")
                } 
                """
        ]
    }
}