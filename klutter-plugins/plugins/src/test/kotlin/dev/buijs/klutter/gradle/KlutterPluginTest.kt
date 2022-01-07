package dev.buijs.klutter.gradle

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
class KlutterPluginTest : WordSpec({

    "Using the KlutterPlugin ID" should {
        "Apply the Plugin" {
            val project = ProjectBuilder.builder().build()
            project.pluginManager.apply("dev.buijs.klutter.gradle")
            project.plugins.getPlugin(KlutterPlugin::class.java) shouldNotBe null
        }
    }

    "Applying the KlutterPlugin" should {
        "Register the 'KlutterPluginExtension' extension" {
            val project = ProjectBuilder.builder().build()
            project.pluginManager.apply(KlutterPlugin::class.java)
            project.adapter() shouldNotBe null

            //when
            project.adapter().multiplatform { source = "la source" }
            project.adapter().root = File("flutter")

            //then
            project.adapter().getMultiplatformDto()?.source shouldBe "la source"
            project.adapter().root shouldBe File("flutter")

        }
    }


})