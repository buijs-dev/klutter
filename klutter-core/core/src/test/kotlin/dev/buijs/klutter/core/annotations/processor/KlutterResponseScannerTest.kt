package dev.buijs.klutter.core.annotations.processor

import dev.buijs.klutter.core.DartObjects
import io.kotlintest.shouldBe
import io.kotlintest.specs.WordSpec

/**
 * @author Gillian Buijs
 */
class KlutterResponseScannerTest : WordSpec({

    """Using a KlutterResponseScanner""" should {

        "return enumerations and messages" {

            //Given:
            val content = """
                |open class Jedi(
                |    val name: String,
                |    val age: Int,
                |    val alliance: String? = null,
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
                |
                |@Serializable
                |enum class SerializableRank {
                |    @SerialName("Super") S,
                |    @SerialName("Awesome") A,
                |    @SerialName("Badass") B,
                |}
            """.trimMargin()


            //When:
            val messages = MessageScanner(content).scan()
            val enumerations = EnumScanner(content).scan()

            //Then:
            messages.size shouldBe 1
            messages[0].name shouldBe "Jedi"

            //And field 1
            messages[0].fields[0].name shouldBe "name"
            messages[0].fields[0].dataType shouldBe "String"
            messages[0].fields[0].customDataType shouldBe null
            messages[0].fields[0].isList shouldBe false
            messages[0].fields[0].optional shouldBe false

            //And field 2
            messages[0].fields[1].name shouldBe "age"
            messages[0].fields[1].dataType shouldBe "int"
            messages[0].fields[1].customDataType shouldBe null
            messages[0].fields[1].isList shouldBe false
            messages[0].fields[1].optional shouldBe false

            //And field 3
            messages[0].fields[2].name shouldBe "alliance"
            messages[0].fields[2].dataType shouldBe "String"
            messages[0].fields[2].customDataType shouldBe null
            messages[0].fields[2].isList shouldBe false
            messages[0].fields[2].optional shouldBe true

            //And field 4
            messages[0].fields[3].name shouldBe "abilities"
            messages[0].fields[3].dataType shouldBe ""
            messages[0].fields[3].customDataType shouldBe "Ability"
            messages[0].fields[3].isList shouldBe true
            messages[0].fields[3].optional shouldBe false

            //And field 5
            messages[0].fields[4].name shouldBe "rank"
            messages[0].fields[4].dataType shouldBe ""
            messages[0].fields[4].customDataType shouldBe "Rank"
            messages[0].fields[4].isList shouldBe false
            messages[0].fields[4].optional shouldBe false

            //And asserting enumerations
            enumerations.size shouldBe 3
            enumerations[0].name shouldBe "Ability"
            enumerations[0].values.size shouldBe 4
            enumerations[0].values[0] shouldBe "FORCE_JUMP"
            enumerations[0].values[1] shouldBe "FORCE_PULL"
            enumerations[0].values[2] shouldBe "MIND_TRICK"
            enumerations[0].values[3] shouldBe "LEVITATION"

            enumerations[1].name shouldBe "Rank"
            enumerations[1].values.size shouldBe 5
            enumerations[1].values[0] shouldBe "S"
            enumerations[1].values[1] shouldBe "A"
            enumerations[1].values[2] shouldBe "B"
            enumerations[1].values[3] shouldBe "C"
            enumerations[1].values[4] shouldBe "D"

            enumerations[2].name shouldBe "SerializableRank"
            enumerations[2].values.size shouldBe 3
            enumerations[2].values[0] shouldBe "S"
            enumerations[2].values[1] shouldBe "A"
            enumerations[2].values[2] shouldBe "B"
            enumerations[2].jsonValues[0] shouldBe "Super"
            enumerations[2].jsonValues[1] shouldBe "Awesome"
            enumerations[2].jsonValues[2] shouldBe "Badass"
        }

    }

})