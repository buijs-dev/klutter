package dev.buijs.klutter.annotations.processor

import io.kotlintest.shouldBe
import io.kotlintest.specs.WordSpec

/**
 * @author Gillian Buijs
 */
class KlutterAdapteeScannerTest : WordSpec({

    """Using a KlutterAdapteeScanner""" should {

        "return methodcalldefinitions" {

            //given:
            val body = """
       
                    @KlutterAdaptee(name = "getGreeting")
                    fun greeting(): String {
                        return "Hello"
                    }
        
            """.trimIndent()

            //and:
            val sut = KlutterAdapteeScanner(
                className = "Greeting",
                fqdn = "dev.some.package",
                ktFileBody = body
            )

            //when:
            val actual = sut.scan()

            //then:
            actual.size shouldBe 1
            actual[0].call shouldBe "Greeting().greeting()"
            actual[0].getter shouldBe "getGreeting"
            actual[0].returns shouldBe "String"

        }

    }
})
