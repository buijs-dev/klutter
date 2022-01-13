package dev.buijs.klutter.gradle.tasks.adapter.protobuf

import io.kotlintest.specs.WordSpec

/**
 * @author Gillian Buijs
 */
class ProtoPrinterTest : WordSpec({

    //TestData:
    val nullableField = ProtoField(
        name = "nullableFoo",
        dataType = ProtoDataType.STRING,
        optional = true,
        repeated = false
    )

    val requiredField = ProtoField(
        name = "foo",
        dataType = ProtoDataType.INTEGER,
        optional = false,
        repeated = false
    )

    val repeatedField = ProtoField(
        name = "fooBaca",
        dataType = ProtoDataType.LONG,
        optional = false,
        repeated = true
    )

    "When using the MemberPrinter" should {
        "Print all fields as immutable" {

            //Given:
            val printer = MemberPrinter(listOf(nullableField, requiredField, repeatedField))

            //When:
            val actual = printer.print()

            //Then:
            actual.filter { !it.isWhitespace() } shouldBe """
                | int32 foo = 1;
                | repeated int64 fooBaca = 2;
                | optional string nullableFoo = 3;
            """.filter { !it.isWhitespace() }

        }
    }

    "When using the EnumerationPrinter" should {
        "Print all fields as immutable" {

            //Given:
            val enumeration = ProtoEnum(
                name = "NothingElseMatters",
                values = listOf("FOREVER", "TRUSTING", "WHO", "WE", "ARE"))

            val printer = EnumerationPrinter(enumeration)

            //When:
            val actual = printer.print()

            //Then:
            actual.filter { !it.isWhitespace() } shouldBe """
                enum NothingElseMatters {
                   FOREVER = 0;
                   TRUSTING = 1;
                   WHO = 2;
                   WE = 3;
                   ARE = 4;
                }
            """.filter { !it.isWhitespace() }

        }
    }

})

