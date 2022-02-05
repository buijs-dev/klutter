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
       
                    @KlutterAdaptee("getGreeting")
                    fun greeting(): String {
                        return "Hello"
                    }
        
                    @KlutterAdaptee(name = "getAnotherGreeting")
                    suspend fun anotherGreeting(): String {
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
            actual.size shouldBe 2
            actual[0].call shouldBe "Greeting().greeting()"
            actual[0].getter shouldBe "getGreeting"
            actual[0].async shouldBe false
            actual[0].returns shouldBe "String"

            actual[1].call shouldBe "Greeting().anotherGreeting()"
            actual[1].getter shouldBe "getAnotherGreeting"
            actual[1].async shouldBe true
            actual[1].returns shouldBe "String"

        }

    }
})
