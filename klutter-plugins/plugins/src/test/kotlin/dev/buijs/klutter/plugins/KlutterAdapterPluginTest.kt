package dev.buijs.klutter.plugins

import dev.buijs.klutter.gradle.KlutterAdapterPlugin
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
            project.plugins.getPlugin(KlutterAdapterPlugin::class.java) shouldNotBe null
        }
    }

    "Applying the KlutterAdapterPlugin" should {
        "Register the 'KlutterAdapterPluginExtension' extension" {
            val project = ProjectBuilder.builder().build()
            project.pluginManager.apply(KlutterAdapterPlugin::class.java)
            project.adapter() shouldNotBe null

            //when
            project.adapter().sources = listOf(File("."))
            project.adapter().android = File("flutter/android/app")
            project.adapter().flutter = File("flutter/lib")
            project.adapter().ios = File("flutter/ios")

            //then
            project.adapter().sources.isNotEmpty()
            project.adapter().sources shouldBe  listOf(File("."))
            project.adapter().android shouldBe  File("flutter/android/app")
            project.adapter().flutter shouldBe File("flutter/lib")
            project.adapter().ios shouldBe File("flutter/ios")

        }
    }


})