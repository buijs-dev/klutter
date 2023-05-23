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
package dev.buijs.klutter.gradle.dsl

import dev.buijs.klutter.kore.KlutterException
import spock.lang.Specification

class KlutterVersionSpec extends Specification {

    def "Verify versions can be retrieved from included properties File"() {
        expect:
        KlutterVersion.annotations != null
        KlutterVersion.compiler != null
        KlutterVersion.kore != null
        KlutterVersion.gradle != null
        KlutterVersion.tasks != null
        KlutterVersion.kompose != null
        KlutterVersion.flutterEngine != null
    }

    def "Verify versions can be retrieved by using the simpleModuleName"() {
        expect:
        KlutterVersion.INSTANCE.byName$gradle("annotations") == KlutterVersion.annotations
        KlutterVersion.INSTANCE.byName$gradle("compiler") == KlutterVersion.compiler
        KlutterVersion.INSTANCE.byName$gradle("kore") == KlutterVersion.kore
        KlutterVersion.INSTANCE.byName$gradle("gradle") == KlutterVersion.gradle
        KlutterVersion.INSTANCE.byName$gradle("tasks") == KlutterVersion.tasks
        KlutterVersion.INSTANCE.byName$gradle("kompose") == KlutterVersion.kompose
        KlutterVersion.INSTANCE.byName$gradle("flutter-engine-android") == KlutterVersion.flutterEngine
    }

    def "If the simpleModuleName is unknown then a KlutterException is thrown"() {
        when:
        KlutterVersion.INSTANCE.byName$gradle("doesNotExist")

        then:
        KlutterException e = thrown()
        e.message == "Unknown module name 'doesNotExist'."
    }

    def "If the property does NOT exist, then a KlutterException is thrown"() {
        when:
        KlutterVersion.INSTANCE.getOrThrow$gradle("doesNotExist")

        then:
        KlutterException e = thrown()
        e.message == "Missing 'doesNotExist' in Klutter Gradle Jar."
    }

}