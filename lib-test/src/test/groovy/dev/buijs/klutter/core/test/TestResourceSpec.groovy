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

package dev.buijs.klutter.core.test

import spock.lang.Specification

import java.nio.file.Files

/**
 * @author Gillian Buijs
 */
class TestResourceSpec extends Specification {

    def "Verify a resource is loaded as String" (){

        given:
        def resources = new TestResource()

        when:
        def content = resources.load("android_adapter")

        then:
        content != null
        content != ""

    }

    def "Verify a resource is copied" (){

        given:
        def resources = new TestResource()
        def target = Files.createTempDirectory("")
        def actual = new File("${target.toFile().absolutePath}/foo.kt")

        when:
        resources.copy("android_adapter", actual)

        then:
        actual != null
        actual.exists()
        actual.text != null
        actual.text != ""

    }

}
