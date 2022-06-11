package dev.buijs.klutter.core

class TestData {

    def static greetingMethod = new Method(
            "greeting",
            "platform.Greeting",
            "Greeting().greeting()",
            false,
            "String"
    )

    def static fooBarMethods = [
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
                    true,
                    "List<Complex>",
            )
    ]

}
