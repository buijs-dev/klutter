package dev.buijs.klutter.core

import dev.buijs.klutter.core.test.TestUtil

internal fun verify(printer: KlutterPrinter, expected: String): Boolean {
    return TestUtil.verify(printer.print(), expected)
}