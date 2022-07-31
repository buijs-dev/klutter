package dev.buijs.klutter.plugins.gradle


import spock.lang.Specification

class KlutterPluginDSLSpec extends Specification {

    def "Verify a KlutterPluginDTO is created by using the KlutterPluginDSL" () {

        when:
        def dto = new KlutterPluginDSL().configure({
            it.name = "awesome_plugin"
        })

        then:
        dto.name == "awesome_plugin"
    }

}
