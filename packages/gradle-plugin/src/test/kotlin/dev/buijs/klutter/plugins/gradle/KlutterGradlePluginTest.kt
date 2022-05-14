//package dev.buijs.klutter.plugins.gradle
//
//import io.kotlintest.shouldBe
//import io.kotlintest.shouldNotBe
//import io.kotlintest.specs.WordSpec
//import org.gradle.testfixtures.ProjectBuilder
//import java.io.File
//
///**
// * @author Gillian Buijs
// */
//class KlutterGradlePluginTest : WordSpec({
//
//    "Using the KlutterGradlePlugin ID" should {
//        "Apply the Plugin" {
//            val project = ProjectBuilder.builder().build()
//            project.pluginManager.apply("dev.buijs.klutter.gradle")
//            project.plugins.getPlugin(KlutterGradlePlugin::class.java) shouldNotBe null
//        }
//    }
//
//    "Applying the KlutterGradlePlugin" should {
//        "Register the 'KlutterGradlePluginExtension' extension" {
//            val project = ProjectBuilder.builder().build()
//            project.pluginManager.apply(KlutterGradlePlugin::class.java)
//            project.adapter() shouldNotBe null
//x
//            //when
//            project.adapter().root = File("flutter")
//
//            //then
//            project.adapter().root shouldBe File("flutter")
//
//        }
//    }
//
//
//})