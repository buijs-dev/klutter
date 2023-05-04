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

package dev.buijs.klutter.kore.common

import dev.buijs.klutter.kore.KlutterException
import dev.buijs.klutter.kore.KlutterPrinter
import dev.buijs.klutter.kore.KlutterWriter
import java.io.File

/**
 * Return the [File] or throw a [KlutterException] if it does not exist.
 */
fun File.verifyExists(): File {
    if(exists()) return this
    throw KlutterException("Path does not exist: $absolutePath")
}

/**
 * Create a new File if it does not exist.
 *
 * @throws [KlutterException] if file does not exist after creating it.
 * @returns [File] with path of this String.
 */
fun File.maybeCreate() = this.also {
    if(!it.exists()) it.createNewFile()
    if(!it.exists()) throw KlutterException("Failed to create file: $this")
}

/**
 * Create a new folder if it does not exist.
 *
 * @throws [KlutterException] if folder does not exist after creating it.
 * @returns [File] with path of this String.
 */
fun File.maybeCreateFolder() = this.also {
    if(!it.exists()) it.mkdirs()
    if(!it.exists()) throw KlutterException("Failed to create folders: $this")
}

/**
 * Default implementation of [KlutterWriter] which creates a new File and writes [content] to it.
 */
class FileWriter(
    private val file: File,
    private val content: String,
) : KlutterWriter {

    /**
     * Write content to the file:
     * - Delete the file if it exists.
     * - Create a new file.
     * - Write content to file.
     *
     * @throws [KlutterException] if file does not exist after creation.
     */
    override fun write() {
        if(file.exists()) file.delete()
        file.maybeCreate()
        file.writeText(content)
    }
}

infix fun File.write(printer: KlutterPrinter) =
    FileWriter(this, printer.print()).write()