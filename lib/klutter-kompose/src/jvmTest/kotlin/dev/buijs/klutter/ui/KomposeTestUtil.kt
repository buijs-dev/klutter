package dev.buijs.klutter.ui

import dev.buijs.klutter.kore.KlutterPrinter
import dev.buijs.klutter.kore.test.TestUtil

fun verify(printer: KlutterPrinter, expected: String) {
    TestUtil.verify(printer.print(), expected)
}