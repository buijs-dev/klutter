package dev.buijs.klutter.core

import dev.buijs.klutter.core.test.TestUtil

class CoreTestUtil extends TestUtil {
    def static verify(KlutterPrinter printer, String expected) {
        verify(printer.print(), expected)
    }
}
