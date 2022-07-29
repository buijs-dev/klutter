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

package dev.buijs.klutter.plugins.gradle.tasks

import dev.buijs.klutter.core.KlutterException
import dev.buijs.klutter.plugins.gradle.KlutterGradleTask
import dev.buijs.klutter.plugins.gradle.klutterExtension
import dev.buijs.klutter.ui.tasks.AndroidArtifactBuildTask
import dev.buijs.klutter.ui.tasks.IosArtifactBuildTask
import org.gradle.api.Project

/**
 * Task to build debug .apk for Android and Runner.app for IOS.
 */
internal open class CopyIosFramework : KlutterGradleTask() {
    override fun describe() {
        if(project.klutterExtension().application != null) {
            project.copyKomposeFramework()
        } else {
            project.copyPlatformFramework()
        }
    }
}

internal fun Project.copyPlatformFramework() {
    project.rootDir.resolve("platform/build/fat-framework/release").also {
        if(!it.exists()) {
            throw KlutterException("File not found: $it")
        }
        it.copyTo(project.rootDir.resolve("ios/Klutter"))
    }
}

internal fun Project.copyKomposeFramework() {
    project.rootDir.resolve("lib/build/fat-framework/release").also {
        if(!it.exists()) {
            throw KlutterException("File not found: $it")
        }
        it.renameTo(project.rootDir.resolve("app/backend/ios/Klutter"))
    }
}