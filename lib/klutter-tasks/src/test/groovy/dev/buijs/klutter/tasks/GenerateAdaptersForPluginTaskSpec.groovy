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
package dev.buijs.klutter.tasks

import dev.buijs.klutter.kore.KlutterException
import dev.buijs.klutter.kore.shared.DartEnum
import dev.buijs.klutter.kore.shared.DartField
import dev.buijs.klutter.kore.shared.DartMessage
import dev.buijs.klutter.kore.test.TestUtil
import spock.lang.Specification

class GenerateAdaptersForPluginTaskSpec extends Specification {

    def "Validate throws exception if customDataType list is not empty after validating"() {
        given:
        def message1 = new DartMessage("Bob",
                [new DartField("FartCannon", "", false, false, true)]
        )

        def message2 = new DartMessage("Dave",
                [new DartField("String", "", false, false, false)]
        )

        when:
        GenerateAdaptersTaskKt.validate([message1, message2], [])

        then:
        KlutterException e = thrown()
        TestUtil.verify(e.message,
                """Processing annotation '@KlutterResponse' failed, caused by:
            
                            Could not resolve the following classes:
                            
                            - 'FartCannon'
                            
                            
                            Verify if all KlutterResponse annotated classes comply with the following rules:
                            
                            1. Must be an open class
                            2. Fields must be immutable
                            3. Constructor only (no body)
                            4. No inheritance
                            5. Any field type should comply with the same rules
                            
                            If this looks like a bug please file an issue at: https://github.com/buijs-dev/klutter/issues
                            """

        )

    }

    def "Validate no exception is thrown if customDataType list not empty after validating"() {
        when:
        def message1 = new DartMessage("Bob",
                [new DartField("FartCannon", "", false, false, true)]
        )

        def message2 = new DartMessage("Dave",
                [new DartField("String", "", false, false, false)]
        )

        def message3 = new DartMessage("FartCannon",
                [new DartField("GruMood", "intensity", false, false, true)]
        )

        def enum1 = new DartEnum("GruMood", ["BAD, GOOD, CARING, HATING, CONQUER_THE_WORLD"], [])

        then:
        GenerateAdaptersTaskKt.validate([message1, message2, message3], [enum1])

    }
}