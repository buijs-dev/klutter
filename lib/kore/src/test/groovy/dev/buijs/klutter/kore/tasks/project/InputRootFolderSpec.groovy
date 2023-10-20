/* Copyright (c) 2021 - 2023 Buijs Software
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
package dev.buijs.klutter.kore.tasks.project

import dev.buijs.klutter.kore.common.EitherNok
import dev.buijs.klutter.kore.common.EitherOk
import dev.buijs.klutter.kore.tasks.project.InputRootFolderKt
import spock.lang.Specification

import java.nio.file.Files

class InputRootFolderSpec extends Specification {

     def "If folder does not exist then toRootFolder returns EitherNok"() {
         expect:
         with(InputRootFolderKt.toRootFolder("doesNotExist")) {
             it instanceof EitherNok
             (it as EitherNok).data == "Root folder does not exist: 'doesNotExist'"
         }
     }

    def "If folder exists then toRootFolder returns EitherOk"() {
        given:
        def folder = Files.createTempDirectory("").toFile()

        expect:
        with(InputRootFolderKt.toRootFolder(folder.absolutePath)) {
            it instanceof EitherOk
            (it as EitherOk).data == folder
        }
    }
}
