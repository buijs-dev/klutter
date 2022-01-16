package dev.buijs.klutter.annotations.processor

import dev.buijs.klutter.core.DartObjects
import io.kotlintest.shouldBe
import io.kotlintest.specs.WordSpec


/**
 * @author Gillian Buijs
 */
class KlutterResponseScannerTest : WordSpec({

    """Using a KlutterResponseScanner""" should {

        "return enumerations and messages" {

            fun f(o: DartObjects, index: Int) = o.messages[0].fields[index]

            //Given:
            val content = """
                |open class Jedi(
                |    val name: String,
                |    val age: Int,
                |    val alliance: String?,
                |    val abilities: List<Ability>,
                |    val rank: Rank
                |)
                |
                |enum class Ability {
                |    FORCE_JUMP, 
                |    FORCE_PULL, 
                |    MIND_TRICK, 
                |    LEVITATION
                |}
                |
                |enum class Rank {
                |    S, A, B, C, D
                |}
            """.trimMargin()


            //And:
            val scanner = KlutterResponseScanner(ktFileBody = content)

            //When:
            val actual = scanner.scan()

            //Then:
            actual.messages.size shouldBe 1
            actual.messages[0].name shouldBe "Jedi"

            //And field 1
            f(actual, 0).name shouldBe "name"
            f(actual, 0).dataType shouldBe "String"
            f(actual, 0).customDataType shouldBe null
            f(actual, 0).isList shouldBe false
            f(actual, 0).optional shouldBe false

            //And field 2
            f(actual, 1).name shouldBe "age"
            f(actual, 1).dataType shouldBe "int"
            f(actual, 1).customDataType shouldBe null
            f(actual, 1).isList shouldBe false
            f(actual, 1).optional shouldBe false

            //And field 3
            f(actual, 2).name shouldBe "alliance"
            f(actual, 2).dataType shouldBe "String"
            f(actual, 2).customDataType shouldBe null
            f(actual, 2).isList shouldBe false
            f(actual, 2).optional shouldBe true

            //And field 4
            f(actual, 3).name shouldBe "abilities"
            f(actual, 3).dataType shouldBe ""
            f(actual, 3).customDataType shouldBe "Ability"
            f(actual, 3).isList shouldBe true
            f(actual, 3).optional shouldBe false

            //And field 5
            f(actual, 4).name shouldBe "rank"
            f(actual, 4).dataType shouldBe ""
            f(actual, 4).customDataType shouldBe "Rank"
            f(actual, 4).isList shouldBe false
            f(actual, 4).optional shouldBe false


            //And asserting enumerations
            actual.enumerations.size shouldBe 2
            actual.enumerations[0].name shouldBe "Ability"
            actual.enumerations[0].values.size shouldBe 4
            actual.enumerations[0].values[0] shouldBe "FORCE_JUMP"
            actual.enumerations[0].values[1] shouldBe "FORCE_PULL"
            actual.enumerations[0].values[2] shouldBe "MIND_TRICK"
            actual.enumerations[0].values[3] shouldBe "LEVITATION"

            actual.enumerations[1].name shouldBe "Rank"
            actual.enumerations[1].values.size shouldBe 5
            actual.enumerations[1].values[0] shouldBe "S"
            actual.enumerations[1].values[1] shouldBe "A"
            actual.enumerations[1].values[2] shouldBe "B"
            actual.enumerations[1].values[3] shouldBe "C"
            actual.enumerations[1].values[4] shouldBe "D"

        }

    }

})