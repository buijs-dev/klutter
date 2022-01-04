package dev.buijs.klutter.core.flutter

import dev.buijs.klutter.core.MethodCallDefinition
import io.kotlintest.shouldBe
import io.kotlintest.specs.WordSpec


/**
 * @author Gillian Buijs
 * @author https://buijs.dev
 */
class AndroidAdapterPrinterTest: WordSpec({

    "Using the KlutterAdapterPrinter" should {
        "Create the body GeneratedAdapter body with a branch for each KlutterAdaptee annotation" {

            val definitions = listOf(
                MethodCallDefinition(
                call = "FooBar().zeta()",
                getter = "doFooBar",
                import = "io.foo.bar.FooBar",
                returns = Any::class.java
            ),
                MethodCallDefinition(
                    call = "FooBar().beta()",
                    getter = "doNotFooBar",
                    import = "io.foo.bar.FooBar",
                    returns = Any::class.java
                ),
            )

            val actual = AndroidAdapterPrinter(definitions).print()

            actual.filter { !it.isWhitespace() } shouldBe """
                 package dev.buijs.klutter.adapter

                 import io.foo.bar.FooBar
                 import io.flutter.plugin.common.MethodChannel
                 import io.flutter.plugin.common.MethodChannel.Result
                 import io.flutter.plugin.common.MethodCall
                
                 /**
                  * Generated code By Gillian Buijs
                  *
                  * For bugs or improvements contact me: https://buijs.dev
                  *
                  */
                 class GeneratedKlutterAdapter {
                
                   fun handleMethodCalls(call: MethodCall, result: MethodChannel.Result) {
                        if (call.method == "doFooBar") {
                            result.success(FooBar().zeta())
                        } else if (call.method == "doNotFooBar") {
                            result.success(FooBar().beta())
                        } else result.notImplemented()
                   }
                
                 }
                """.filter { !it.isWhitespace() }
        }
    }
})


