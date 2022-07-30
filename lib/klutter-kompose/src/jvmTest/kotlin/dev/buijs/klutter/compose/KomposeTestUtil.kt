package dev.buijs.klutter.compose

import dev.buijs.klutter.core.KlutterPrinter
import dev.buijs.klutter.core.test.TestUtil

fun verify(printer: KlutterPrinter, expected: String) {
    TestUtil.verify(printer.print(), expected)
}