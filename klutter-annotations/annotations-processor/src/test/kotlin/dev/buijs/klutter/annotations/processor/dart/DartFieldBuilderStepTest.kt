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


package dev.buijs.klutter.annotations.processor.dart

import dev.buijs.klutter.core.DartField
import io.kotlintest.shouldBe
import io.kotlintest.shouldNotBe
import io.kotlintest.specs.WordSpec


/**
 * @author Gillian Buijs
 */
class DartFieldBuilderStepTest: WordSpec({

    fun test(line: String) = DartFieldBuilder(line).build()

    var line: String
    var actual: DartField?

    """Scanning a line""" should {

        "Return the correct variable name" {

            for (name in listOf("foo", " fooBar", " fooBar123 ", " fooBar%xyz  ", " FOOBAR  ")) {
                //Given:
                line = "val $name: String"

                //When:
                actual = test(line)

                //Then:
                actual shouldNotBe null

                //And:
                actual!!.name shouldBe name.filter { !it.isWhitespace() }
            }

        }

        "Return optional = true if datatype contains ?" {

            //Given:
            line = "val maybeFoo: String?"

            //When:
            actual = test(line)

            //Then:
            actual shouldNotBe null

            //And:
            actual!!.optional shouldBe true
            actual!!.dataType shouldBe "String"

        }

        "Return optional = false if datatype does not contains ?" {

            //Given:
            line = "val foo: String"

            //When:
            actual = test(line)

            //Then:
            actual shouldNotBe null

            //And:
            actual!!.optional shouldBe false

        }

        "Return repeated = true with unwrapped datatype if it contains List" {

            //Given:
            val declarations = listOf(
                "val list: List<String>",
                "val list: List< String >",
                "val list: List   < String >   ",
            )

            for(list in declarations) {

                //When:
                actual = test(list)

                //Then:
                actual shouldNotBe null

                //And:
                actual!!.isList shouldBe true
                actual!!.dataType shouldBe "String"

            }

        }

        "Return repeated = false if datatype does not contain List" {

            //Given:
            line = "val notAList: String"

            //When:
            actual = test(line)

            //Then:
            actual shouldNotBe null

            //And:
            actual!!.isList shouldBe false
            actual!!.dataType shouldBe "String"

        }

        "Return custom datatype if it is not found in DartKotlinMap enum" {

            //Given:
            line = "val notAList: MyType"

            //When:
            actual = test(line)

            //Then:
            actual shouldNotBe null

            //And:
            actual!!.dataType shouldBe ""
            actual!!.customDataType shouldBe "MyType"

        }

        "Return custom datatype if it nested datatype is not found in DartKotlinMap enum" {

            //Given:
            line = "val notAList: List<MyType>"

            //When:
            actual = test(line)

            //Then:
            actual shouldNotBe null

            //And:
            actual!!.dataType shouldBe ""
            actual!!.customDataType shouldBe "MyType"
            actual!!.isList shouldBe true

        }

        "Fail when a variable is mutable" {

            //Given:
            line = "var mutable: String?"

            //When:
            actual = test(line)

            //Then:
            actual shouldBe null

        }

        "Fail when a declaration is unreadable" {

            //Given:
            val declarations = listOf(
                "vallist: List<String>",
                "val list:! List< String >",
                "val : List   < String >   ",
                "val : List   < !String >   ",
            )

            for(faulty in declarations) {

                //faulty
                actual = test(faulty)

                //Then:
                actual shouldBe  null

            }

        }

    }

})