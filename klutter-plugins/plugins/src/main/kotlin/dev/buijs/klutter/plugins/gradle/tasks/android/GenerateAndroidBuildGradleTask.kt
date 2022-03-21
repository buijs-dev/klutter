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


package dev.buijs.klutter.plugins.gradle.tasks.android

import dev.buijs.klutter.plugins.gradle.KlutterTask
import dev.buijs.klutter.plugins.gradle.tasks.adapter.flutter.AndroidBuildGradleGenerator
import dev.buijs.klutter.plugins.gradle.tasks.adapter.flutter.AndroidRootBuildGradleGenerator
import org.gradle.api.logging.Logging
import org.gradle.internal.logging.text.StyledTextOutputFactory
import javax.inject.Inject

/**
 * @author Gillian Buijs
 */
open class GenerateAndroidBuildGradleTask
@Inject constructor(styledTextOutputFactory: StyledTextOutputFactory):
    KlutterTask(styledTextOutputFactory)
{

    override fun describe() {

        val android = project().android

        val androidBuildGradleGenerator = AndroidBuildGradleGenerator(android)
        val androidRootBuildGradleGenerator = AndroidRootBuildGradleGenerator(android)

        androidBuildGradleGenerator.generate()
        androidRootBuildGradleGenerator.generate()

    }

}

