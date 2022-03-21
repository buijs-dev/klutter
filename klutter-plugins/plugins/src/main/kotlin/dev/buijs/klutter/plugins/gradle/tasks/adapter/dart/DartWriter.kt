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

package dev.buijs.klutter.plugins.gradle.tasks.adapter.dart


import dev.buijs.klutter.core.Flutter
import dev.buijs.klutter.core.KlutterLogger
import dev.buijs.klutter.core.KlutterWriter
import dev.buijs.klutter.plugins.gradle.tasks.adapter.flutter.AndroidManifestVisitor
import org.gradle.api.logging.Logging

/**
 * @author Gillian Buijs
 */
class DartWriter(
    val content: String,
    val flutter: Flutter,
): KlutterWriter {

    private val log = Logging.getLogger(DartWriter::class.java)

    override fun write() {
        val file = flutter.file.resolve("generated").also {
            if(!it.exists()){
                it.mkdir()
            }
        }

        val dartFile = file.resolve("messages.dart").also {
            if(it.exists()) {
                it.delete().also {
                    log.lifecycle("Deleted file: $file")
                }
            } else log.error("File not found. Creating a new file!")
        }

        dartFile.createNewFile().also { log.lifecycle("Created new file: $file") }
        dartFile.writeText(content).also { log.lifecycle("Written content to $file:\r\n$content") }

    }

}