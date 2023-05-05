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
package dev.buijs.klutter.compiler.scanner

import dev.buijs.klutter.kore.ast.*
import dev.buijs.klutter.kore.common.Either
import dev.buijs.klutter.kore.common.EitherNok
import dev.buijs.klutter.kore.common.EitherOk
import dev.buijs.klutter.kore.common.maybeCreateFolder
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File

/**
 * Write all AbstractTypes or error message to [outputFolder].
 */
internal fun Either<String,SquintMessageSource>.writeOutput(
    outputFolder: File, count: Int
): Either<String,SquintMessageSource> {

    val folder = outputFolder
        .resolve("response")
        .maybeCreateFolder()

    when (this) {
        is EitherNok -> {
            val file = folder.resolve("sqdb_${count}_invalid.json")
            file.createNewFile()
            file.writeText(data)
            return this
        }

        is EitherOk -> {
            val file = folder.resolve("sqdb_${data.type.className.lowercase()}.json")
            file.createNewFile()
            when (data.squintType) {
                is SquintCustomType ->
                    file.writeText(Json.encodeToString(data.squintType))
                is SquintEnumType ->
                    file.writeText(Json.encodeToString(data.squintType))
            }
            return ValidSquintType(data = data.copy(source = file))
        }
    }

}