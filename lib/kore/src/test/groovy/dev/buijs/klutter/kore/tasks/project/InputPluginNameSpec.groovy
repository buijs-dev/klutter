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
import dev.buijs.klutter.kore.tasks.project.InputPluginNameKt
import spock.lang.Specification

class InputPluginNameSpec extends Specification {

     def "Verify valid plugin names"() {
         expect:
         with(InputPluginNameKt.toPluginName(name)) {
             it instanceof EitherOk
             (it as EitherOk).data == name
         }
         where:
         name << [
                 "plugin",
                 "my_plugin",
                 "my_awesome_plugin_2",
                 "myawesome2plugins"
         ]
     }

    def "Verify invalid plugin names"() {
        expect:
        with(InputPluginNameKt.toPluginName(name)) {
            it instanceof EitherNok
            (it as EitherNok).data == "PluginName error: Should only contain" +
                    " lowercase alphabetic, numeric and or _ characters" +
                    " and start with an alphabetic character ('my_plugin')."
        }
        where:
        name << [
                "1plugin", // can not start with a number
                "_my_plugin", // can not start with an underscore
                "my_awesome_plugin!!!", // can not contain: !!
                "MYAWESOMEPLUGIN" // can not be uppercase
        ]
    }

}
