package dev.buijs.klutter.core

import dev.buijs.klutter.core.shared.Method

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