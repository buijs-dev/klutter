package dev.buijs.klutter.plugins

import dev.buijs.klutter.gradle.KlutterAdapterPlugin
import dev.buijs.klutter.gradle.klutteradapter
import io.kotlintest.shouldBe
import io.kotlintest.shouldNotBe
import io.kotlintest.specs.WordSpec
import org.gradle.testfixtures.ProjectBuilder
import java.io.File

/**
 * By Gillian Buijs
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
            project.klutteradapter() shouldNotBe null

            //when
            project.klutteradapter().sources = listOf(File("."))
            project.klutteradapter().android = File("flutter/android/app")
            project.klutteradapter().flutter = File("flutter/lib")
            project.klutteradapter().ios = File("flutter/ios")

            //then
            project.klutteradapter().sources.isNotEmpty()
            project.klutteradapter().sources shouldBe  listOf(File("."))
            project.klutteradapter().android shouldBe  File("flutter/android/app")
            project.klutteradapter().flutter shouldBe File("flutter/lib")
            project.klutteradapter().ios shouldBe File("flutter/ios")

        }
    }


})