package dev.buijs.klutter.core

import dev.buijs.klutter.core.test.TestUtil

internal fun verify(printer: KlutterPrinter, expected: String) {
    TestUtil.verify(printer.print(), expected)
}