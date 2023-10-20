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
package dev.buijs.klutter.kore.tasks.project

import dev.buijs.klutter.kore.KlutterException
import dev.buijs.klutter.kore.common.Either
import dev.buijs.klutter.kore.tasks.project.ProjectBuilderOptions
import spock.lang.Specification

import static dev.buijs.klutter.kore.tasks.project.GroovyHelper.findProjectBuilderAction

class ProjectBuilderActionsSpec extends Specification {

    def "Verify a KlutterException is thrown if no ProjectBuilderAction is found"(){
        when:
        findProjectBuilderAction(
                new ProjectBuilderOptions(
                        Either.ok(new File("")),
                        Either.ok(""),
                        Either.ok(""),
                        "3.0.5.macos.arm64",
                        null))

        then:
        KlutterException e = thrown()
        e.message == "Unknown ProjectBuilderAction: interface dev.buijs.klutter.kore.tasks.project.ProjectBuilderAction"
    }

}
