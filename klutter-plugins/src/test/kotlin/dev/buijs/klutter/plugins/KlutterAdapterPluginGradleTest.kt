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

            val flutterDir = Path.of("").resolve("flutter/lib").toAbsolutePath().toFile()
            flutterDir.mkdirs()
            val mainDartFile = flutterDir.resolve("main.dart").absoluteFile
            mainDartFile.createNewFile()
            mainDartFile.writeText("""
                import 'package:flutter/material.dart';

                void main() {
                  runApp(const MyApp());
                }

                class MyApp extends StatelessWidget {
                  const MyApp({Key? key}) : super(key: key);

                  @override
                  Widget build(BuildContext context) {
                    return MaterialApp(
                      debugShowCheckedModeBanner: false,
                      title: 'Klutter Example',
                      theme: ThemeData(
                        primarySwatch: Colors.blue,
                      ),
                      home: const MyHomePage(title: 'Klutter'),
                    );
                  }
                }

            """.trimIndent())

            val sourcesDir = androidAppDir.resolve("FakeClass.kt").absoluteFile
            sourcesDir.createNewFile()
            sourcesDir.writeText("""
                package foo.bar.baz

                import dev.buijs.klutter.annotations.Annotations

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
                    id("dev.buijs.klutter.gradle")
                }

                klutter {
                    sources = listOf(File("$sourcesDir"))
                    flutter = File("${flutterDir.absolutePath}")
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

                import foo.bar.baz.FakeClass
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
                            result.success(FakeClass().foo())
                        } else if (call.method == "BabyYoda") {
                            result.success(FakeClass().fooBar())
                        } else  if (call.method == "BabyYoda") {
                            result.success(FakeClass().zeta())
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
                         MethodChannel(flutterEngine.dartExecutor,"KLUTTER")
                            .setMethodCallHandler{ call, result ->
                                GeneratedKlutterAdapter().handleMethodCalls(call, result)
                         }
                         GeneratedPluginRegistrant.registerWith(flutterEngine)
                    }
                }
            """.filter { !it.isWhitespace() }

            //cleanup
            flutterDir.deleteRecursively()
            androidAppDir.deleteRecursively()
            Path.of("").resolve("android").toFile().delete()

        }
    }

})