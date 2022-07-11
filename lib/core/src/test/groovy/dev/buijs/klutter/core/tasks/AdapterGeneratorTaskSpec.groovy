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

package dev.buijs.klutter.core.tasks

import dev.buijs.klutter.core.CoreTestUtil
import dev.buijs.klutter.core.project.ProjectKt
import dev.buijs.klutter.core.test.TestPlugin
import dev.buijs.klutter.core.test.TestResource
import spock.lang.Shared
import spock.lang.Specification

class AdapterGeneratorTaskSpec extends Specification {

    @Shared
    def resources = new TestResource()

    def "Verify adapters are generated correctly for a plugin project"() {

        given:
        def sut = new TestPlugin()

        and:
        resources.copyAll([
                "platform_source_code"  : sut.platformSourceFile,
                "android_app_manifest"  : sut.manifest,
                "build_gradle_plugin"   : sut.platformBuildGradle,
                "settings_gradle_plugin": sut.rootSettingsGradle,
                "plugin_pubspec"        : sut.pubspecYaml,
                "plugin_ios_podspec"    : sut.iosPodspec,
        ])

        when:
        with(ProjectKt.plugin(sut.root, sut.pluginName)) { project ->
            new AdapterGeneratorTask(
                    project.android,
                    project.ios,
                    project.root,
                    project.platform,
            ).run()
        }

        then: "Verify lib/plugin.dart file is created"
        with(new File("${sut.libFolder}/${sut.pluginName}.dart")) {
            it.exists()
            CoreTestUtil.verify(it.text, resources.load("flutter_plugin_library"))
        }

        and: "Verify SuperAwesomePlugin.kt file is generated in android folder"
        with(new File("${sut.androidSrcMain}/kotlin/foo/bar/${sut.pluginName}/${sut.pluginClassName}.kt")) {
            it.exists()
            CoreTestUtil.verify(it.text, resources.load("android_plugin_class"))
        }

        and: "Verify SwiftSuperAwesomePlugin.kt file is generated in ios folder"
        with(new File("${sut.iosClasses.absolutePath}/Swift${sut.pluginClassName}.swift")) {
            it.exists()
            CoreTestUtil.verify(it.text, resources.load("ios_swift_plugin"))
        }

        and: "Verify ios podspec has excluded SDK"
        with(new File("${sut.ios.absolutePath}/${sut.pluginName}.podspec")) {
            it.exists()
            CoreTestUtil.verify(it.text, resources.load("plugin_ios_podspec_excluded"))
        }
    }
}