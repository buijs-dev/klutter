/* Copyright (c) 2021 - 2022 Buijs Software
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 */

package dev.buijs.klutter.core.shared

import dev.buijs.klutter.core.Android
import dev.buijs.klutter.core.IOS
import dev.buijs.klutter.core.Platform
import dev.buijs.klutter.core.Root
import dev.buijs.klutter.core.tasks.AdapterGeneratorTask
import dev.buijs.klutter.core.test.CompareMode
import dev.buijs.klutter.core.test.PluginProject
import spock.lang.Specification

import static dev.buijs.klutter.core.test.KlutterTest.*

class AdapterGeneratorTaskSpec extends Specification {

    def "Verify adapters are generated correctly for a plugin project"() {

        given:
        PluginProject sut = plugin { project, resources ->
            resources.copyAll([
                    "platform_source_code"    : project.platformSourceClass,
                    "android_app_manifest"    : project.androidManifest,
                    "build_gradle_plugin"     : project.platformBuildGradle,
                    "settings_gradle_plugin"  : project.rootSettingsGradle,
                    "plugin_pubspec"          : project.pubspecYaml,
                    "plugin_ios_podspec"      : project.iosPodspec,
            ])
        }

        when:
        def root = new Root(sut.root)
        new AdapterGeneratorTask(
                new Android(sut.android),
                new IOS(sut.ios, sut.pluginName),
                root,
                new Platform(sut.platform, sut.pluginName),
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

        sut.verify("method handler boilerplate should be added to android") { project, resources ->
            project.hasChild(
                    "${sut.androidMain.absolutePath}/kotlin/foo/bar/super_awesome",
                   "SuperAwesomePlugin.kt",
                     "android_plugin_class",
                   CompareMode.IGNORE_SPACES
            )
        }

        sut.verify("method handler boilerplate should be added to ios") { project, resources ->
            project.hasChild(
                    sut.iosClasses.absolutePath,
                    "SwiftSuperAwesomePlugin.swift",
                    "ios_swift_plugin",
                    CompareMode.IGNORE_SPACES
            )
        }

        sut.verify("plugin ios podspec has excluded SDK") { project, resources ->
            project.hasChild(
                    sut.ios.absolutePath,
                    "super_awesome.podspec",
                    "plugin_ios_podspec_excluded",
                    CompareMode.IGNORE_SPACES
            )
        }

    }

}