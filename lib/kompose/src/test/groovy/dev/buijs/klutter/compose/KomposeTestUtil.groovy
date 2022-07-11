package dev.buijs.klutter.compose

import dev.buijs.klutter.core.KlutterPrinter
import dev.buijs.klutter.core.test.TestUtil

class KomposeTestUtil extends TestUtil {
    def static verify(KlutterPrinter printer, String expected) {
        verify(printer.print(), expected)
    }
}
