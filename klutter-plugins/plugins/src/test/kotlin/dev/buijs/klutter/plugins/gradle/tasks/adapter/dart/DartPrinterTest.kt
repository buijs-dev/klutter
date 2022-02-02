package dev.buijs.klutter.plugins.gradle.tasks.adapter.dart

import dev.buijs.klutter.core.DartKotlinMap
import dev.buijs.klutter.core.DartEnum
import dev.buijs.klutter.core.DartField
import dev.buijs.klutter.core.DartMessage
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

    "When using the MessagePrinter" should {
        "A valid Dart class is printed" {

            //Given:
            val printer = MessagePrinter(
                DartMessage(
                    name = "Monster",
                    fields = listOf(
                        nullableField,
                        requiredField,
                        repeatedField
                    )
                )
            )

            //When:
            val actual = printer.print()

            //Then:
            actual.filter { !it.isWhitespace() } shouldBe """
               class Monster {

                  Monster({
                    required this.foo,
                    required this.fooBaca,
                    this.nullableFoo,
                  });
                
                  factory Monster.fromJson(dynamic message) {
                    final json = jsonDecode(message);
                    return Monster (
                      nullableFoo: json['nullableFoo']?.toString(),
                      foo: json['foo'].toInt(),
                      fooBaca: List<double>.from(json.decode(json['fooBaca']).map((o) => o.toDouble())),
                    );
                  }
                
                  final int foo;
                  final List<double> fooBaca;
                  String? nullableFoo;
                
                  Map<String, dynamic> toJson() {
                    return {
                      'nullableFoo': nullableFoo,
                      'foo': foo,
                      'fooBaca': fooBaca.toList()
                    };
                  }
                }
            """.filter { !it.isWhitespace() }

        }
    }

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
                values = listOf("forever", "trusting", "who", "we", "are"),
                jsonValues = emptyList())

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

    "When using the EnumExtensionPrinter" should {
        "Print serializable fields" {

            //Given:
            val enumeration = DartEnum(
                name = "NothingElseMatters",
                values = listOf("forever", "trusting", "who", "we", "are"),
                jsonValues = listOf("FOREVER", "TRUSTING", "WHO", "WE", "ARE"))

            val printer = EnumExtensionPrinter(enumeration)

            //When:
            val actual = printer.print()

            //Then:
            actual.filter { !it.isWhitespace() }  shouldBe """
                    extension _NothingElseMatters on NothingElseMatters {
                    
                      static NothingElseMatters fromJson(String value) {
                        switch(value) {
                          case "FOREVER": return NothingElseMatters.forever;
                          case "TRUSTING": return NothingElseMatters.trusting;
                          case "WHO": return NothingElseMatters.who;
                          case "WE": return NothingElseMatters.we;
                          case "ARE": return NothingElseMatters.are;
                          default: return NothingElseMatters.none;
                        }
                     }
                    
                      String? toJson() {
                        switch(this) { 
                          case NothingElseMatters.forever: return "FOREVER";
                          case NothingElseMatters.trusting: return "TRUSTING";
                          case NothingElseMatters.who: return "WHO";
                          case NothingElseMatters.we: return "WE";
                          case NothingElseMatters.are: return "ARE";
                          default: return null;
                        }
                      }
                    
                    }
            """.filter { !it.isWhitespace() }

        }
    }

})

