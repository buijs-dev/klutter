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
package dev.buijs.klutter.compiler.scanner

import dev.buijs.klutter.compiler.wrapper.KAWrapper
import dev.buijs.klutter.kore.ast.Method
import dev.buijs.klutter.kore.ast.StringType
import dev.buijs.klutter.kore.common.EitherNok
import dev.buijs.klutter.kore.common.EitherOk
import kotlin.sequences.SequencesKt
import spock.lang.Shared
import spock.lang.Specification

class EventScannerSpec extends Specification {

    @Shared
    Method dummyMethod = new Method("", "", "", false, new StringType(), null, null)

    def "When there is an error message, then an InvalidEvent is returned" () {
        given:
        def wrapper = new KAWrapper(true, "It's broken", null)

        when:
        def result = EventScannerKt.getEvents(SequencesKt.sequenceOf(wrapper))

        then:
        with(result[0] as EitherNok) {
            it.data == "It's broken"
        }
    }

    def "When there is no error message and method is null, then an InvalidEvent is returned" () {
        given:
        def wrapper = new KAWrapper(true, null, null)

        when:
        def result = EventScannerKt.getEvents(SequencesKt.sequenceOf(wrapper))

        then:
        with(result[0] as EitherNok) {
            it.data == "Failed to convert Event to Method AST."
        }
    }

    def "When there is no error message and method is not null, then a ValidEvent is returned" () {
        given:
        def wrapper = new KAWrapper(true, null, dummyMethod)

        when:
        def result = EventScannerKt.getEvents(SequencesKt.sequenceOf(wrapper))

        then:
        with(result[0] as EitherOk) {
            it.data == dummyMethod
        }
    }

    def "When there is no Event annotation, then scanning is skipped" () {
        given:
        def wrapper = new KAWrapper(false, null, null)

        when:
        def result = EventScannerKt.getEvents(SequencesKt.sequenceOf(wrapper))

        then:
        result.isEmpty()
    }
}
