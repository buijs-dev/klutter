package dev.buijs.klutter.core.shared

import dev.buijs.klutter.core.KlutterPrinter
import spock.lang.Shared
import spock.lang.Specification

class AdapterGeneratorSpec extends Specification {

    def "Package name is used as method channel name"(){
        expect:
        sut().methodChannelName() == "super_plugin"
    }

    def "KLUTTER is used as method channel name if pubspec has no android section"(){
        given:
        def sut = sut(dataWithoutPlugins)

        expect:
        sut.methodChannelName() == "KLUTTER"
    }

    def "KLUTTER is used as method channel name if pubspec has android section without plugin name"(){
        given:
        def sut = sut(dataWithEmptyPlugins)

        expect:
        sut.methodChannelName() == "KLUTTER"
    }

    def "Empty String is returned if pubspec has no android section"(){
        given:
        def sut = sut(dataWithoutPlugins)

        expect:
        sut.androidPluginClassName() == ""
    }

    def "Empty String is returned if pubspec has android section without plugin name"(){
        given:
        def sut = sut(dataWithEmptyPlugins)

        expect:
        sut.androidPluginClassName() == ""
    }

    def "Android plugin class name is returned from pubspec"(){
        given:
        def sut = sut()

        expect:
        sut.androidPluginClassName() == "SuperPlugin"
    }

    def "Empty String is returned if pubspec has no ios section"(){
        given:
        def sut = sut(dataWithoutPlugins)

        expect:
        sut.iosPluginClassName() == ""
    }

    def "Empty String is returned if pubspec has ios section without plugin name"(){
        given:
        def sut = sut(dataWithEmptyPlugins)

        expect:
        sut.iosPluginClassName() == ""
    }

    def "IOS plugin class name is returned from pubspec"(){
        given:
        def sut = sut()

        expect:
        sut.iosPluginClassName() == "SuperPlugin"
    }

    @Shared
    def static dataWithEmptyPlugins = new AdapterData(
            new PubspecData("super_plugin",
                    new PubFlutter(
                            new Plugin(
                                    new Platforms(
                                            new PluginClass(null,null), null)
                            )
                    )
            ), [], [], []
    )

    @Shared
    def static dataWithoutPlugins = new AdapterData(
            new PubspecData("super_plugin",
                    new PubFlutter(
                            new Plugin(
                                    new Platforms(null, null)
                            )
                    )
            ), [], [], []
    )

    def static sut() {
        return new AdapterGenerator(AdapterStub.get()) {

            @Override
            KlutterPrinter printer() { return null }

            @Override
            File path() { return null }

        }
    }

    def static sut(AdapterData data) {
        return new AdapterGenerator(data) {

            @Override
            KlutterPrinter printer() { return null }

            @Override
            File path() { return null }

        }
    }

}