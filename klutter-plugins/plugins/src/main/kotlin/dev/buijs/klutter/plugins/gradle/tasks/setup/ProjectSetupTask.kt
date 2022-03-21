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


package dev.buijs.klutter.plugins.gradle.tasks.setup

import dev.buijs.klutter.plugins.gradle.KlutterTask
import dev.buijs.klutter.plugins.gradle.tasks.adapter.flutter.AndroidManifestVisitor
import dev.buijs.klutter.plugins.gradle.tasks.adapter.flutter.IosAppFrameworkInfoPlistVisitor
import dev.buijs.klutter.plugins.gradle.tasks.adapter.flutter.IosInfoPlistVisitor
import dev.buijs.klutter.plugins.gradle.tasks.adapter.flutter.PupspecVisitor
import org.gradle.internal.logging.text.StyledTextOutputFactory
import javax.inject.Inject

/**
 * @author Gillian Buijs
 */
open class ProjectSetupTask
@Inject constructor(styledTextOutputFactory: StyledTextOutputFactory):
    KlutterTask(styledTextOutputFactory)
{

    override fun describe() {

        val project = project()
        val android = project.android
        val ios = project.ios
        val appName = PupspecVisitor(project.flutter.root.resolve("pubspec.yaml")).appName()

        val androidManifestVisitor = AndroidManifestVisitor(android.manifest(), appName)
        val iosInfoPlistVisitor = IosInfoPlistVisitor(
            appName = appName,
            infoPlist = ios.file.resolve("Runner/Info.plist"))

        val appFrameworkInfoPlistVisitor = IosAppFrameworkInfoPlistVisitor(
            ios.file.resolve("Flutter/AppFrameworkInfo.plist"),
            iosVersion(),
        )

        androidManifestVisitor.visit()
        iosInfoPlistVisitor.visit()
        appFrameworkInfoPlistVisitor.visit()

    }

}

