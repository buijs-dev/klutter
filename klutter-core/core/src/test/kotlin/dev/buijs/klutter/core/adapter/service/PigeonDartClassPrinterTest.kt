package dev.buijs.klutter.core.adapter.service

import io.kotlintest.shouldBe
import io.kotlintest.specs.WordSpec

/**
 * @author Gillian Buijs
 *
 * Contact me: https://buijs.dev
 *
 */
class PigeonDartClassPrinterTest: WordSpec({

    "Printing a Pigeon.dart file" should {
        "Should return a valid pigeon.dart class body" {
            val dto = KlutterServiceDTO(
                apis = listOf(
                    KlutterApi(
                        name = "Api2Host",
                        functions = listOf(
                            KlutterApiFunction(
                                async = true,
                                name = "calculate",
                                returnValue = "Value",
                                parameters = listOf(KlutterMetaNamedField(name = "value", type = "Value"))
                            )
                        )
                    ),
                    KlutterApi(
                        name = "Api",
                        functions = listOf(
                            KlutterApiFunction(
                                name = "queryState",
                                returnValue = "StateResult"
                            )
                        )
                    )
                ),
                dataClasses = listOf(
                    KlutterDataClass(
                        name = "Value",
                        fields = listOf(
                            KlutterMetaNamedField(
                                name = "number",
                                type = "int"
                            )
                        )
                    ),
                    KlutterDataClass(
                        name = "StateResult",
                        fields = listOf(
                            KlutterMetaNamedField(
                                name = "state",
                                type = "State"
                            ),
                            KlutterMetaNamedField(
                                name = "errorMessage",
                                type = "String"
                            )
                        )
                    )
                ),
                enumClasses = listOf(KlutterEnumClass(
                    name = "State",
                    values = listOf("pending", "success", "error")
                ))
            )

            PigeonDartClassPrinter().print(dto).filter { !it.isWhitespace() } shouldBe """
                import 'package:pigeon/pigeon.dart';
                
                @HostApi()
                abstract class Api2Host {
                  @async
                  Value calculate(Value value);
                }
                
                @HostApi()
                abstract class Api {
                  StateResult queryState();
                }
                
                class Value {
                  int? number;
                }

                class StateResult {
                  State? state;
                  String? errorMessage;
                }

                enum State {
                  pending,
                  success,
                  error,
                }
               
            """.trimIndent().filter { !it.isWhitespace() }

        }

    }
})