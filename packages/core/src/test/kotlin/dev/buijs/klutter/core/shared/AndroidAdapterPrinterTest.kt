package dev.buijs.klutter.core.shared

import dev.buijs.klutter.core.MethodData
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
                MethodData(
                call = "FooBar().zeta()",
                getter = "doFooBar",
                import = "io.foo.bar.FooBar",
                returns = "String"
            ),
                MethodData(
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
                    import io.flutter.plugin.common.MethodCall
                    import kotlinx.coroutines.CoroutineScope
                    import kotlinx.coroutines.Dispatchers
                    import kotlinx.coroutines.launch
                    
                    /**
                     * Generated code by the Klutter Framework
                     */
                    class GeneratedKlutterAdapter {
                    
                        private val mainScope = CoroutineScope(Dispatchers.Main)   
                        
                        fun handleMethodCalls(call: MethodCall, result: MethodChannel.Result) {
                            mainScope.launch {
                               when (call.method) {
                                    "doFooBar" -> {
                                        result.success(FooBar().zeta())
                                    }
                                    "doNotFooBar" -> {
                                        result.success(FooBar().beta().toKJson())
                                    } 
                                    else -> result.notImplemented()
                               }
                            }
                        }
                    }
                """.filter { !it.isWhitespace() }
        }
    }
})


