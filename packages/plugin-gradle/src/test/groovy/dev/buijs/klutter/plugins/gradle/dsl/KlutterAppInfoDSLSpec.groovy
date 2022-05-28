package dev.buijs.klutter.plugins.gradle.dsl

import spock.lang.Specification

class KlutterAppInfoDSLSpec extends Specification {

    def "Verify a KlutterAppInfoDTO is created by using the KlutterAppInfoDSL" () {

        when:
        def dto = new KlutterAppInfoDSL().configure({
            it.name = "awesome_plugin"
            it.version = "0.0.1"
            it.description = "an awesome flutter plugin made with klutter"
            it.projectFolder = "foo/bar"
            it.outputFolder = "foo/bar/build"
        })

        then:
        dto.name == "awesome_plugin"
        dto.appId == "dev.buijs.klutter.activity"
        dto.version == "0.0.1"
        dto.description == "an awesome flutter plugin made with klutter"
        dto.projectFolder == "foo/bar"
        dto.outputFolder == "foo/bar/build"
    }

}
