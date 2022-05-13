package dev.buijs.klutter.core.tasks.plugin

import dev.buijs.klutter.core.Klutter
import spock.lang.Specification

import java.nio.file.Files

class CreatePluginProjectTaskSpec extends Specification {

    def "Verify creating an instance of Secrets by supplying a location"() {

        given:
        def temp = Files.createTempDirectory("buildsrc")
        def properties = temp.resolve("klutter.secrets").toFile()
        properties.createNewFile()
        properties.write(
                "store.file.uri=x\nstore.password=y\nkey.alias=me\nkey.password=pass\n"
        )

        when:
        def sut = Klutter.secrets(temp.toAbsolutePath().toFile().absolutePath)

        then:
        sut["store.file.uri"] == "x"
        sut["store.password"] == "y"
        sut["key.alias"] == "me"
        sut["key.password"] == "pass"

    }

    //    "Using the CreatePluginProjectTask command" should {
    //
    //        "create a klutter plugin project folder in the given folder" {
    //
    //            //given
    //            val temp = Files.createTempDirectory("foo").toFile()
    //
    //            //and
    //            val sut = CreatePluginProjectTask(
    //                organisation = "dev.buijs.examples",
    //                libraryName = "example",
    //                projectLocation = temp.absolutePath,
    //            )
    //
    //            //when
    //            sut.run()
    //
    //            //then
    //            val flutter = temp.resolve("example/flutter").also { it.shouldExist() }
    //            flutter.resolve("README.md").shouldExist()
    //            flutter.resolve("CHANGELOG.md").shouldExist()
    //            flutter.resolve("LICENSE").shouldExist()
    //
    //            val gradle = temp.resolve("example/gradle/wrapper").also { it.shouldExist() }
    //            gradle.resolve("gradle-wrapper.jar").shouldExist()
    //            gradle.resolve("gradle-wrapper.properties")
    //                .also { it.shouldExist() }
    //                .also { it.readText().shouldContain("""distributionUrl=https\://services.gradle.org/distributions/gradle-7.0.4-bin.zip""") }
    //
    //        }
    //
    //    }
}