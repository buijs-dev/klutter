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

import dev.buijs.klutter.core.project.toPubspec
import io.kotlintest.shouldBe
import io.kotlintest.specs.WordSpec
import java.nio.file.Files

class PubspecVisitorTest: WordSpec({

    "Using the IosInfoPlistVisitor" should {

        val projectDir = Files.createTempDirectory("")
        val pubspec = projectDir.resolve("plugin_pubspec").toFile()

        pubspec.createNewFile()

        "Set app name and display name" {
            pubspec.writeText(
                """
                  name: my app
                  description: this is my app
                  version: 1.0.0+1
            """.trimIndent()
            )

            pubspec.toPubspec().name shouldBe "my app"

        }

    }
})