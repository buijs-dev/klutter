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

package dev.buijs.klutter.plugins.gradle.tasks.adapter.protobuf


import dev.buijs.klutter.core.Klutter
import dev.buijs.klutter.core.KlutterLogger
import dev.buijs.klutter.core.KlutterWriter

/**
 * @author Gillian Buijs
 */
class ProtoWriter(val content: String, val klutter: Klutter): KlutterWriter {

    override fun write(): KlutterLogger {
        val logger = KlutterLogger()

        val file = klutter.file.resolve(".klutter").also {
            if(!it.exists()){
                it.mkdir()
            }
        }

        val protoFile = file.resolve("klutter.proto").also {
            if(it.exists()) {
                it.delete().also {
                    logger.debug("Deleted proto file: $file")
                }
            } else logger.error("Proto file not found. Creating a new file!")
        }

        protoFile.createNewFile().also { logger.debug("Created new proto file: $file") }
        protoFile.writeText(content).also { logger.debug("Written content to $file:\r\n$content") }
        return logger
    }

}