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

package dev.buijs.klutter.core.annotations


import dev.buijs.klutter.core.tasks.scanForKlutterAdaptee
import io.kotlintest.shouldBe
import io.kotlintest.specs.WordSpec
import java.nio.file.Files

class KlutterAdapteeProcessorTest: WordSpec({

    val toBeScanned = """
        package some.company.ridiculous_plugin.platform

        import dev.buijs.klutter.annotations.kmp.KlutterAdaptee

        class Greeting {

            @KlutterAdaptee(name = "greeting")
            fun greetingCount(): Int {
               return 10
            }

        }
        
        class IsOut4Summer {
            
            @KlutterAdaptee(name = "summertime")
            fun isSummerTime(): Boolean {
               return true
            }
        
        }
    """.trimIndent()

    val source = Files.createTempFile("t", ".kt").toFile().also {
        it.writeText(toBeScanned)
    }

    "Using the scanner" should {

        "Return Dart types as default" {
            source.scanForKlutterAdaptee().let { methods ->
                methods.size shouldBe 2
                methods.first().let { method ->
                    method.async shouldBe false
                    method.command shouldBe "greeting"
                    method.dataType shouldBe "int" // Dart !!
                    method.import shouldBe "some.company.ridiculous_plugin.platform.Greeting"
                    method.method shouldBe "Greeting().greetingCount()"
                }

                methods[1].let { method ->
                    method.async shouldBe false
                    method.command shouldBe "summertime"
                    method.dataType shouldBe "bool" // Dart !!
                    method.import shouldBe "some.company.ridiculous_plugin.platform.IsOut4Summer"
                    method.method shouldBe "IsOut4Summer().isSummerTime()"
                }

            }

        }

    }

})