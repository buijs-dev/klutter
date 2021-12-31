package dev.buijs.klutter.plugins

import dev.buijs.klutter.gradle.KlutterPlugin
import dev.buijs.klutter.gradle.adapter
import io.kotlintest.shouldBe
import io.kotlintest.shouldNotBe
import io.kotlintest.specs.WordSpec
import org.gradle.testfixtures.ProjectBuilder
import java.io.File

/**
 * @author Gillian Buijs
 *
 * Contact me: https://buijs.dev
 *
 */
class KlutterAdapterPluginTest : WordSpec({

    "Using the KlutterAdapterPlugin ID" should {
        "Apply the Plugin" {
            val project = ProjectBuilder.builder().build()
            project.pluginManager.apply("dev.buijs.klutter.gradle")
            project.plugins.getPlugin(KlutterPlugin::class.java) shouldNotBe null
        }
    }

    "Applying the KlutterAdapterPlugin" should {
        "Register the 'KlutterAdapterPluginExtension' extension" {
            val project = ProjectBuilder.builder().build()
            project.pluginManager.apply(KlutterPlugin::class.java)
            project.adapter() shouldNotBe null

            //when
            project.adapter().multiplatform { source = "la source" }
            project.adapter().flutter = File("flutter")

            //then
            project.adapter().getMultiplatformDto()?.source shouldBe "la source"
            project.adapter().flutter shouldBe File("flutter")

        }
    }


})