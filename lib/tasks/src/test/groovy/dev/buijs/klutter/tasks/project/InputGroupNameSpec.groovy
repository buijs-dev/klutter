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
package dev.buijs.klutter.tasks.project

import dev.buijs.klutter.kore.common.EitherNok
import dev.buijs.klutter.kore.common.EitherOk
import spock.lang.Specification

class InputGroupNameSpec extends Specification {

     def "Verify valid group names"() {
         expect:
         with(InputGroupNameKt.toGroupName(name)) {
             it instanceof EitherOk
             (it as EitherOk).data == name
         }
         where:
         name << [
                 "com.example",
                 "com.example.app",
                 "awesome.group_1.name",
                 "awesome.group1_1.name"
         ]
     }

    def "Verify invalid group names"() {
        expect:
        with(InputGroupNameKt.toGroupName(name)) {
            it instanceof EitherNok
            (it as EitherNok).data == message
        }

        where:
        name            | message
        "1.dev"         | "GroupName error: Should be lowercase alphabetic separated by dots ('com.example')."
        "_.my.group"    | "GroupName error: Characters . and _ can not precede each other."
        "com_.group"    | "GroupName error: Characters . and _ can not precede each other."
        "groupie"       | "GroupName error: Should contain at least 2 parts ('com.example')."
        "group._"       | "GroupName error: Characters . and _ can not precede each other."
    }

}
