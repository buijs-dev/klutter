package dev.buijs.klutter.core.protobuf


import dev.buijs.klutter.core.Klutter
import dev.buijs.klutter.core.Root
import io.kotlintest.shouldBe
import io.kotlintest.specs.WordSpec
import kotlin.io.path.createDirectories
import kotlin.io.path.createTempDirectory

/**
 * @author Gillian Buijs
 */
class ProtoGeneratorTest: WordSpec({

    val projectDir = createTempDirectory("").also {
        it.createDirectories()
    }

    val klutterDir = projectDir.resolve("klutter").toFile().also {
        it.mkdir()
    }

    "When using the ProtoGenerator" should {
        "It should write a valid proto file" {

            //Given: A ProtoObject
            val objects = ProtoObjects(
                messages = listOf(
                    ProtoMessage(
                        name = "Shazaam",
                        fields = listOf(
                            ProtoField(
                                name = "age",
                                dataType = ProtoDataType.INTEGER,
                                optional = false,
                                repeated = false,
                            ),
                            ProtoField(
                                name = "friends",
                                dataType = ProtoDataType.INTEGER,
                                optional = true,
                                repeated = false,
                            ),
                            ProtoField(
                                name = "bff",
                                dataType = ProtoDataType.STRING,
                                optional = true,
                                repeated = false,
                            ),
                            ProtoField(
                                name = "powers",
                                dataType = ProtoDataType.NONE,
                                customDataType = "Power",
                                optional = false,
                                repeated = true,
                            )
                        )
                    )
                ),
                enumerations = listOf(
                    ProtoEnum(
                        name = "Power",
                        values = listOf(
                            "SUPER_STRENGTH",
                            "SUPER_FUNNY"
                        )
                    )
                )
            )

            //And: A ProtoGenerator
            val generator = ProtoGenerator(
                klutter = Klutter(
                    file = projectDir.resolve("klutter").toFile(),
                    root = Root(file = projectDir.toFile())
                ),
                objects = objects
            )

            //When:
            generator.generate()

            //Then:
            val actual = klutterDir.resolve(".klutter/klutter.proto").also {
                it.exists()
            }

            actual.readText().filter { !it.isWhitespace() } shouldBe """
                syntax = "proto3";
  
                message Shazaam {
                    int32 age = 1;
                    optional string bff = 2;
                    optional int32 friends = 3;
                    repeated Power powers = 4;
                }
                
                enum Power {
                    SUPER_STRENGTH = 0;
                    SUPER_FUNNY = 1;
                }
            """.filter { !it.isWhitespace() }

        }
    }


})