package dev.buijs.klutter.gradle.tasks.adapter.dart

import dev.buijs.klutter.core.DartKotlinMap
import dev.buijs.klutter.core.DartEnum
import dev.buijs.klutter.core.DartField
import dev.buijs.klutter.plugins.gradle.tasks.adapter.dart.EnumerationPrinter
import dev.buijs.klutter.plugins.gradle.tasks.adapter.dart.MemberPrinter
import io.kotlintest.shouldBe
import io.kotlintest.specs.WordSpec

/**
 * @author Gillian Buijs
 */
class DartPrinterTest : WordSpec({

    //TestData:
    val nullableField = DartField(
        name = "nullableFoo",
        dataType = DartKotlinMap.STRING.dartType,
        optional = true,
        isList = false,
    )

    val requiredField = DartField(
        name = "foo",
        dataType = DartKotlinMap.INTEGER.dartType,
        optional = false,
        isList = false,
    )

    val repeatedField = DartField(
        name = "fooBaca",
        dataType = DartKotlinMap.DOUBLE.dartType,
        optional = false,
        isList = true,
    )

    "When using the MemberPrinter" should {
        "Print all fields as immutable" {

            //Given:
            val printer = MemberPrinter(listOf(nullableField, requiredField, repeatedField))

            //When:
            val actual = printer.print()

            //Then:
            actual.filter { !it.isWhitespace() } shouldBe """
                final int foo;
                final List<double> fooBaca;
                String? nullableFoo;
            """.filter { !it.isWhitespace() }

        }
    }

    "When using the EnumerationPrinter" should {
        "Print all fields as immutable" {

            //Given:
            val enumeration = DartEnum(
                name = "NothingElseMatters",
                values = listOf("forever", "trusting", "who", "we", "are"))

            val printer = EnumerationPrinter(enumeration)

            //When:
            val actual = printer.print()

            //Then:
            actual.filter { !it.isWhitespace() } shouldBe """
                enum NothingElseMatters {
                    forever,
                    trusting,
                    who,
                    we,
                    are,
                    none
                }
            """.filter { !it.isWhitespace() }

        }
    }

})

