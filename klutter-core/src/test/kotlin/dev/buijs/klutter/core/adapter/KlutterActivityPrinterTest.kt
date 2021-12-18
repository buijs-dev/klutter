package dev.buijs.klutter.core.adapter

import io.kotlintest.shouldBe
import io.kotlintest.shouldNotBe
import io.kotlintest.specs.WordSpec
import org.mockito.kotlin.mock
import java.nio.file.Files

/**
 * By Gillian Buijs
 *
 * Contact me: https://buijs.dev
 *
 */

class KlutterActivityPrinterTest : WordSpec({

    "Using the KlutterActivityPrinter" should {

        val projectDir = Files.createTempDirectory("")
        val mainActivity = projectDir.resolve("mainActivity.kts").toFile()

        val sut = KlutterActivityPrinter()

        "Append MethodChannel calls" {

            mainActivity.createNewFile()
            mainActivity.writeText(
                """
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
            """.trimIndent()
            )

            val content = KtFileContent(
                file = mainActivity,
                ktFile = mock(),
                content = mainActivity.readText()
            )


            val actual = sut.print(content)


            actual.content shouldNotBe null
            actual.content.filter { !it.isWhitespace() } shouldBe """
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
                
                }""".filter { !it.isWhitespace() }
        }



        "Not duplicate MethodChannelHandler if present" {
            mainActivity.delete()
            mainActivity.createNewFile()
            mainActivity.writeText(
                """
                package foo.bar.baz.appz

                import io.flutter.embedding.android.FlutterActivity
                import androidx.annotation.NonNull
                import io.flutter.embedding.engine.FlutterEngine

                @KlutterAdapter
                class MainActivity: FlutterActivity() {

                    override fun configureFlutterEngine(@NonNull flutterEngine: FlutterEngine) {
                        GeneratedPluginRegistrant.registerWith(flutterEngine)
                        someothermethod().blabla()
                        MethodChannel(flutterEngine.dartExecutor,"KLUTTER")
                            .setMethodCallHandler{ call, result ->
                                GeneratedKlutterAdapter().handleMethodCalls(call, result)
                            }
                    }

                }
            """.trimIndent()
            )

            val content = KtFileContent(
                file = mainActivity,
                ktFile = mock(),
                content = mainActivity.readText()
            )


            val actual = sut.print(content)


            actual.content shouldNotBe null
            actual.content.filter { !it.isWhitespace() } shouldBe """
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
                        someothermethod().blabla()
                    }
                
                }""".filter { !it.isWhitespace() }
        }
    }


})