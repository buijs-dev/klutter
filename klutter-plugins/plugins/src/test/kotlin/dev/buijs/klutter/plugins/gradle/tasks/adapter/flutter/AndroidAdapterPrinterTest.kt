package dev.buijs.klutter.plugins.gradle.tasks.adapter.flutter

import dev.buijs.klutter.core.MethodCallDefinition
import dev.buijs.klutter.plugins.gradle.tasks.adapter.flutter.AndroidAdapterPrinter
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
                returns = "String"
            ),
                MethodCallDefinition(
                    call = "FooBar().beta()",
                    getter = "doNotFooBar",
                    import = "io.foo.bar.FooBar",
                    returns = "BetaObject"
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
                 * Generated code by the Klutter Framework
                 */
                 class GeneratedKlutterAdapter {
                
                   fun handleMethodCalls(call: MethodCall, result: MethodChannel.Result) {
                        if (call.method == "doFooBar") {
                            result.success(FooBar().zeta())
                        } else if (call.method == "doNotFooBar") {
                            result.success(FooBar().beta().toKJson())
                        } else result.notImplemented()
                   }
                
                 }
                """.filter { !it.isWhitespace() }
        }
    }
})

