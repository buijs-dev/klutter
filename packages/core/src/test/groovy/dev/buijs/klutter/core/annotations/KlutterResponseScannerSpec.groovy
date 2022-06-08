package dev.buijs.klutter.core.annotations

import spock.lang.Specification


class KlutterResponseScannerSpec extends Specification {

    def "Verify messages and enumerations are returned properly"(){

        given:
        def content = """
                open class Jedi(
                    val name: String,
                    val age: Int,
                    val alliance: String? = null,
                    val abilities: List<Ability>,
                    val rank: Rank
                )
                
                enum class Ability {
                    FORCE_JUMP,
                    FORCE_PULL,
                    MIND_TRICK,
                    LEVITATION
                }
                
                enum class Rank {
                    S, A, B, C, D
                }
                
                @Serializable
                enum class SerializableRank {
                    @SerialName("Super") S,
                    @SerialName("Awesome") A,
                    @SerialName("Badass") B,
                }
            """

        when:
        def messages = new MessageScanner(content).scan()
        def enumerations = new EnumScanner(content).scan()

        then:
        messages.size() == 1
        messages[0].name == "Jedi"

        messages[0].fields[0].name == "name"
        messages[0].fields[0].type == "String"
        !messages[0].fields[0].customType
        !messages[0].fields[0].isList
        !messages[0].fields[0].isOptional

        //And field 2
        messages[0].fields[1].name == "age"
        messages[0].fields[1].type == "int"
        !messages[0].fields[1].customType
        !messages[0].fields[1].isList
        !messages[0].fields[1].isOptional

        //And field 3
        messages[0].fields[2].name == "alliance"
        messages[0].fields[2].type == "String"
        !messages[0].fields[2].customType
        !messages[0].fields[2].isList
        messages[0].fields[2].isOptional

        //And field 4
        messages[0].fields[3].name == "abilities"
        messages[0].fields[3].type == "Ability"
        messages[0].fields[3].customType
        messages[0].fields[3].isList
        !messages[0].fields[3].isOptional

        //And field 5
        messages[0].fields[4].name == "rank"
        messages[0].fields[4].type == "Rank"
        messages[0].fields[4].customType
        !messages[0].fields[4].isList
        !messages[0].fields[4].isOptional

        //And asserting enumerations
        enumerations.size == 3
        enumerations[0].name == "Ability"
        enumerations[0].values.size == 4
        enumerations[0].values[0] == "FORCE_JUMP"
        enumerations[0].values[1] == "FORCE_PULL"
        enumerations[0].values[2] == "MIND_TRICK"
        enumerations[0].values[3] == "LEVITATION"

        enumerations[1].name == "Rank"
        enumerations[1].values.size == 5
        enumerations[1].values[0] == "S"
        enumerations[1].values[1] == "A"
        enumerations[1].values[2] == "B"
        enumerations[1].values[3] == "C"
        enumerations[1].values[4] == "D"

        enumerations[2].name == "SerializableRank"
        enumerations[2].values.size == 3
        enumerations[2].values[0] == "S"
        enumerations[2].values[1] == "A"
        enumerations[2].values[2] == "B"
        enumerations[2].valuesJSON[0] == "Super"
        enumerations[2].valuesJSON[1] == "Awesome"
        enumerations[2].valuesJSON[2] == "Badass"
    }



}
