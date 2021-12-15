package dev.buijs.klutter.plugins


import io.kotlintest.shouldBe
import io.kotlintest.specs.WordSpec
import org.gradle.testkit.runner.GradleRunner
import java.nio.file.Files
import java.nio.file.Path

/**
 * By Gillian Buijs
 *
 * Contact me: https://buijs.dev
 *
 */
class KlutterAdapterPluginGradleTest : WordSpec({

    "A configured Kotlin DSL builscript" should {
        "Lead to a successful build" {
            val projectDir = Files.createTempDirectory("")
            val buildScript = projectDir.resolve("build.gradle.kts").toFile()

            val androidAppDir = Path.of("").resolve("android/app").toAbsolutePath().toFile()
            androidAppDir.mkdirs()

            val sourcesDir = androidAppDir.resolve("FakeClass.kt").absoluteFile
            sourcesDir.createNewFile()
            sourcesDir.writeText("""
                package foo.bar.baz

                import dev.buijs.klutter.plugins.adapter.Annotations

                class FakeClass {
                    @KlutterAdaptee(name = "DartMaul")
                    fun foo(): String {
                        return "bar"
                    }

                    @KlutterAdaptee(name = "BabyYoda")
                    fun fooBar(): List<String> {
                        return listOf("baz")
                    }

                    @KlutterAdaptee(name = "BabyYoda")
                    fun zeta(): List<String> =
                        listOf(foo()).map { str ->
                            "str = str                "
                        }.filter { baz ->
                            baz != ""
                        }

                }
            """.trimIndent())

            val mainActivityDir = androidAppDir.resolve(
                Path.of("src", "main", "java", "foo",
                    "bar", "baz", "appz").toFile())

            mainActivityDir.mkdirs()
            val mainActivity = mainActivityDir.resolve("MainActivity.kt")
            mainActivity.createNewFile()
            mainActivity.writeText("""
                package foo.bar.baz.appz

                import io.flutter.embedding.android.FlutterActivity
                import androidx.annotation.NonNull
                import io.flutter.embedding.engine.FlutterEngine

                @KlutterAdapter
                class MainActivity: FlutterActivity() {

                    override fun configureFlutterEngine(@NonNull flutterEngine: FlutterEngine) {
                        GeneratedPluginRegistrant.registerWith(flutterEngine)
                    }

                }
            """.trimIndent())


            buildScript.writeText("""
                plugins {
                    id("dev.buijs.klutter.plugins.adapter.gradle")
                }

                klutteradapter {
                    sources = listOf(File("$sourcesDir"))
                    flutter = File("root/flutterproj/lib")
                    android = File("${androidAppDir.absolutePath}")
                    ios = File("root/flutterproj/ios")
                }

            """.trimIndent())

            GradleRunner.create()
                .withProjectDir(projectDir.toFile())
                .withPluginClasspath()
                .withArguments("generate")
                .build()

            val generatedFile = androidAppDir.resolve(
                Path.of("src", "main", "java", "dev", "buijs", "klutter", "adapter", "GeneratedKlutterAdapter.kt").toFile())

            generatedFile.exists()
            generatedFile.readText().filter { !it.isWhitespace() } shouldBe """
                package dev.buijs.klutter.adapter

                 import io.flutter.plugin.common.MethodChannel
                 import io.flutter.plugin.common.MethodChannel.Result
                 import io.flutter.plugin.common.MethodCall

                 /**
                  * Generated code By Gillian Buijs
                  *
                  * For bugs or improvements contact me: https://buijs.dev
                  *
                  */
                 class GeneratedKlutterAdapter {

                   fun handleMethodCalls(call: MethodCall, result: MethodChannel.Result) {
                        if (call.method == "DartMaul") {
                            result.success(foo())
                        } else if (call.method == "BabyYoda") {
                            result.success(fooBar())
                        } else  if (call.method == "BabyYoda") {
                            result.success(zeta())
                        } else result.notImplemented()
                   }
                 }
            """.filter { !it.isWhitespace() }

            mainActivity.readText().filter { !it.isWhitespace() } shouldBe """
                package foo.bar.baz.appz

                import dev.buijs.klutter.adapter.GeneratedKlutterAdapter
                import io.flutter.plugin.common.MethodChannel
                import io.flutter.embedding.android.FlutterActivity
                import androidx.annotation.NonNull
                import io.flutter.embedding.engine.FlutterEngine

                @KlutterAdapter
                class MainActivity: FlutterActivity() {

                    override fun configureFlutterEngine(@NonNull flutterEngine: FlutterEngine) {
                        GeneratedPluginRegistrant.registerWith(flutterEngine)
                         MethodChannel(flutterEngine.dartExecutor,"KLUTTER")
                            .setMethodCallHandler{ call, result ->
                                GeneratedKlutterAdapter().handleMethodCalls(call, result)
                         }
                    }
                }
            """.filter { !it.isWhitespace() }

            //cleanup
            androidAppDir.deleteRecursively()
            Path.of("").resolve("android").toFile().delete()

        }
    }

})