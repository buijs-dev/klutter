package dev.buijs.klutter.core

class TestData {

    def static greetingMethod = new Method(
            "greeting",
            "platform.Greeting",
            "Greeting().greeting()",
            false,
            "String"
    )

    def static complexityMethods = [
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
    ]

    def static emptyMessageFoo = new DartMessage(
            "Foo", [new DartField("String","field1",  false, false, false)]
    )

    def static fieldRequiredString = new DartField("String","field1",  false, false, false)
    def static fieldRequiredFoo = new DartField("Foo", "field2", false, false, true)
    def static fieldRequiredStringList = new DartField("String", "field3", true, false, false)
    def static fieldRequiredFooList = new DartField("Foo", "field4", true, false, true)
    def static fieldOptionalString = new DartField("String","field5",  false, true, false)
    def static fieldOptionalFoo = new DartField("Foo", "field6", false, true, true)
    def static fieldOptionalStringList = new DartField("String", "field7", true, true, false)
    def static fieldOptionalFooList = new DartField("Foo", "field8", true, true, true)

}