package dev.buijs.klutter.core.shared

import spock.lang.Specification

import java.nio.file.Files

class DartMessageBuilderSpec extends Specification {

    def "If the input list is empty, then the output list empty"() {
        expect:
        DartMessageBuilderKt.toDartMessageList([]).isEmpty()
    }

    def "If the input files are empty, then the output list empty"() {
        given:
        def file = Files.createTempFile("", ".kt").toFile()
        file.write("")

        expect:
        DartMessageBuilderKt.toDartMessageList([file]).isEmpty()
    }

    def "If the input do not contain classes, then the output list empty"() {
        given:
        def file = Files.createTempFile("", ".kt").toFile()
        file.write("""
               Once upon a time in a galaxy not that far away there was this crazy developer...
        """)

        expect:
        DartMessageBuilderKt.toDartMessageList([file]).isEmpty()
    }

    def "If the input contain classes that are not open, then the output list empty"() {
        given:
        def file = Files.createTempFile("", ".kt").toFile()
        file.write("""
               class Jedi(
                    val name: String,
                    val age: Int,
                    val alliance: String? = null,
                    val abilities: List<Ability>,
                    val rank: Rank
                )
        """)

        expect:
        DartMessageBuilderKt.toDartMessageList([file]).isEmpty()
    }

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
        def messages = DartMessageBuilderKt.toDartMessageList([file])

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

    }

}