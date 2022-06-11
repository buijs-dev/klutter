package dev.buijs.klutter.core

internal fun fooBarMethods(): List<Method> = listOf(
    Method(
        "doFooBar",
        "io.foo.bar.FooBar",
        "FooBar().zeta()",
        false,
        "String",
    ), Method(
        "notDoFooBar",
        "io.foo.bar.FooBar",
        "FooBar().beta()",
        false,
        "int",
    ), Method(
        "complexityGetter",
        "io.foo.bar.ComplexFoor",
        "ComplexFoo().bar()",
        true,
        "List<Complex>",
    )
)