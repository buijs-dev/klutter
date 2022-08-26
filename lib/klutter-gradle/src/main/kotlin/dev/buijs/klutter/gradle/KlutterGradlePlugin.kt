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
package dev.buijs.klutter.gradle

import dev.buijs.klutter.gradle.dsl.KlutterGradleDSL
import dev.buijs.klutter.gradle.tasks.*
import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * Gradle plugin for Klutter Framework.
 */
class KlutterGradlePlugin: Plugin<Project> {
    override fun apply(project: Project) {
        project.extensions.add("klutter", KlutterGradleDSL(project))
        project.tasks.register("klutterBuild", BuildKlutterProjectGradleTask::class.java)
        project.tasks.register("klutterBuildAndroid", BuildAndroidWithFlutterGradleTask::class.java)
        project.tasks.register("klutterBuildAndroidIos", BuildAndroidAndIosWithFlutterGradleTask::class.java)
        project.tasks.register("klutterBuildIos", BuildIosWithFlutterGradleTask::class.java)
        project.tasks.register("klutterCopyAarFile", CopyAndroidAarFileGradleTask::class.java)
        project.tasks.register("klutterCopyFramework", CopyIosFrameworkGradleTask::class.java)
        project.tasks.register("klutterExcludeArchsPlatformPodspec", ExcludeArchsPlatformPodspecGradleTask::class.java)
        project.tasks.register("klutterGenerateAdapters", GenerateAdaptersGradleTask::class.java)
        project.tasks.register("klutterGenerateUI", UiGeneratorGradleTask::class.java)
        project.tasks.register("klutterStartAppiumServer", AppiumServerStartGradleTask::class.java)
        project.tasks.register("klutterStopAppiumServer", AppiumServerStopGradleTask::class.java)
    }
}