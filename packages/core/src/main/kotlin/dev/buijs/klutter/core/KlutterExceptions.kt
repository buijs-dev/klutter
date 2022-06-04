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

class KlutterException(msg: String, cause: String = ""): Exception(msg, Throwable(cause))

/**
 * Signals that an exception has occurred while processing a .kt file.
 *
 * @author Gillian Buijs
 */
class KotlinFileScanningException(msg: String, cause: String = ""): Exception(msg, Throwable(cause))

/**
 * Signals that a failure occurred when creating and/or writing file content.
 *
 * @author Gillian Buijs
 */
class KlutterCodeGenerationException(msg: String, cause: String = ""): Exception(msg, Throwable(cause))

/**
 * Signals that a failure has occurred which is caused by missing or faulty configuration of a Klutter project.
 *
 * @author Gillian Buijs
 */
class KlutterConfigException(msg: String, cause: String = ""): Exception(msg, Throwable(cause))