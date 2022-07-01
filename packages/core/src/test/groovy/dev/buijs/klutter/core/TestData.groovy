package dev.buijs.klutter.core

import dev.buijs.klutter.core.shared.DartField
import dev.buijs.klutter.core.shared.DartMessage
import dev.buijs.klutter.core.shared.Method

class TestData {

    def static greetingMethod = new Method(
            "greeting",
            "platform.Greeting",
            "Greeting().greeting()",
            false,
            "String",
            false
    )

    def static complexityMethods = [
            new Method(
                    "doFooBar",
                    "io.foo.bar.FooBar",
                    "FooBar().zeta()",
                    false,
                    "String",
                    false
            ),
            new Method(
                    "notDoFooBar",
                    "io.foo.bar.FooBar",
                    "FooBar().beta()",
                    false,
                    "int",
                    false
            ),
            new Method(
                    "complexityGetter",
                    "io.foo.bar.ComplexFoor",
                    "ComplexFoo().bar(context)",
                    true,
                    "List<Complex>",
                    false
            )
    ]

    def static fooBarMethods = [
            new Method(
                    "doFooBar",
                    "io.foo.bar.FooBar",
                    "FooBar().zeta()",
                    false,
                    "String",
                    false
            ),
            new Method(
                    "notDoFooBar",
                    "io.foo.bar.FooBar",
                    "FooBar().beta()",
                    false,
                    "int",
                    false
            ),
            new Method(
                    "fooBarBinary",
                    "io.foo.bar.FooBar",
                    "FooBar().trueOrFalse()",
                    false,
                    "bool",
                    false
            ),
            new Method(
                    "twoFoo4You",
                    "io.foo.bar.FooBar",
                    "FooBar().makeItADouble()",
                    false,
                    "double",
                    false
            ),
            new Method(
                    "getExoticFoo",
                    "io.foo.bar.FooBar",
                    "FooBar().exotic()",
                    false,
                    "ExoticFoo",
                    false
            ),
            new Method(
                    "manyFooBars",
                    "io.foo.bar.FooBar",
                    "FooBar().moreManyMore()",
                    true,
                    "List<String>",
                    false
            ),
            new Method(
                    "maybeFoos",
                    "io.foo.bar.FooBar",
                    "FooBar().moreMaybeFoos()",
                    true,
                    "List<String>?",
                    true
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