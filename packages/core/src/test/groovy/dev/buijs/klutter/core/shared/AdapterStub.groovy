package dev.buijs.klutter.core.shared

import dev.buijs.klutter.core.Method

class AdapterStub {
    static def get() {
        return new AdapterData(
                new PubspecData(
                        "super_plugin",
                        new PubFlutter(
                                new Plugin(
                                        new Platforms(
                                                new PluginClass("super_plugin", "SuperPlugin"),
                                                new PluginClass("super_plugin", "SuperPlugin"),
                                        )
                                )
                        )
                ),
                [new Method(
                        "greeting",
                        "platform.Greeting",
                        "Greeting().greeting()",
                        false,
                        "String"
                )], [], []
        )
    }

    static def methods = [
                new Method(
                        "doFooBar",
                        "io.foo.bar.FooBar",
                        "FooBar().zeta()",
                        false,
                        "String",
                ),
                new Method(
                        "notDoFooBar",
                        "io.foo.bar.FooBar",
                        "FooBar().beta()",
                        false,
                        "int",
                ),
                new Method(
                        "complexityGetter",
                        "io.foo.bar.ComplexFoor",
                        "ComplexFoo().bar()",
                        false,
                        "List<Complex>",
                )
        ]

}
