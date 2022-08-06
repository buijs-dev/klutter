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
package dev.buijs.klutter.gradle.tasks

import dev.buijs.klutter.core.KlutterException
import dev.buijs.klutter.gradle.KlutterGradleTask
import dev.buijs.klutter.gradle.klutterExtension
import org.gradle.api.Project

/**
 * Task to build debug .apk for Android and Runner.app for IOS.
 */
internal open class CopyAndroidAarFile : KlutterGradleTask() {
    override fun describe() {
        if(project.klutterExtension().application != null) {
            project.copyKomposeAar()
        } else {
            project.copyPlatformAar(
                project.klutterExtension().plugin?.name
                    ?: throw KlutterException("Build.gradle.kts is missing a plugin in klutter config")
            )
        }
    }
}

internal fun Project.copyPlatformAar(pluginName: String) {
    project.rootDir.resolve("platform/build/outputs/aar/$pluginName-release.aar").also {
        if(!it.exists()) {
            throw KlutterException("File not found: $it")
        }
        it.renameTo(project.rootDir.resolve("android/klutter/platform.aar"))
    }
}

internal fun Project.copyKomposeAar() {
    project.rootDir.resolve("lib/build/outputs/aar/lib-release.aar").also {
        if(!it.exists()) {
            throw KlutterException("File not found: $it")
        }
        it.renameTo(project.rootDir.resolve("app/backend/android/klutter/platform.aar"))
    }
}