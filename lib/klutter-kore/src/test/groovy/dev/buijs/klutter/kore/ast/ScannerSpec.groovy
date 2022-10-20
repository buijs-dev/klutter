package dev.buijs.klutter.kore.ast

import spock.lang.Specification

class ScannerSpec extends Specification {

    def "[packageName] returns null if file contains no package name"() {
        expect:
        ScannerKt.packageName("") == ""
    }

    def "[packageName] package name is returned"() {
        expect:
        ScannerKt.packageName("package a.b.c") == "a.b.c"
    }

}