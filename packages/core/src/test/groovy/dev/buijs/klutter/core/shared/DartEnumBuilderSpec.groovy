package dev.buijs.klutter.core.shared

import spock.lang.Specification

import java.nio.file.Files

class DartEnumBuilderSpec extends Specification {

    def "Verify messages and enumerations are returned properly"(){

        given:
        def file = Files.createTempFile("", ".kt").toFile()
        file.write("""
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
            """)

        when:
        def enumerations = DartEnumBuilderKt.toDartEnums([file])

        then:
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