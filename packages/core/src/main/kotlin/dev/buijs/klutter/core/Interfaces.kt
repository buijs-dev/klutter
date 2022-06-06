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

package dev.buijs.klutter.core

/**
 * The purpose of this class is to create new files.
 * A KlutterFileGenerator uses a [KlutterPrinter] and a [KlutterWriter]
 * to encapsulate the functionality for creating the content of a file
 * and actually writing the content to a file location.
 */
abstract class KlutterFileGenerator {

 /**
  * Use the [printer] function to create the file content
  * and pass it to the [writer] fun to create a new file.
  *
  * If this method is not overridden it will function under the
  * assumption that the [writer] function calls the [printer] function
  * and writes it output to a location specified in the [KlutterWriter] class.
  *
  * @return logger with info about the steps done by the generator.
  */
 open fun generate() = writer().write()

 /**
  * @return a [KlutterPrinter] to create the content of a file.
  */
 abstract fun printer(): KlutterPrinter

 /**
  * @return a [KlutterWriter] to write the content to a file location.
  */
 abstract fun writer(): KlutterWriter

}

/**
 * Utility interface which takes (generated) file content and writes it to a file.
 * Used for editing and/or creating classes and configuration files.
 *
 * KlutterWriter is an class to be used by [KlutterFileGenerator] and
 * should not be exposed for external usage.
 */
interface KlutterWriter {

 /**
  * Creates a new file and writes the content to said file.
  */
 fun write()

}

/**
 * The purpose of this class is to output the content of a file.
 *
 * KlutterPrinter is an class to be used by [KlutterFileGenerator] and
 * should not be exposed for external usage.
 */
interface KlutterPrinter {

 /**
  * @return the created file content as String.
  */
 fun print(): String

}

/**
 * Utility interface which processes a given file and may or may not change it's content.
 */
interface KlutterVisitor {

 /**
  * Reads the content of a file and determines if any editting should be done.
  */
 fun visit()

}

/**
 * Class which contains all logic to fully execute a functional task.
 */
interface KlutterTask {

 fun run()

}