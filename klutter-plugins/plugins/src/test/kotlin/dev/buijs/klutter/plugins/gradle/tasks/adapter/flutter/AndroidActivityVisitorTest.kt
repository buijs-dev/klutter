package dev.buijs.klutter.plugins.gradle.tasks.adapter.flutter

import dev.buijs.klutter.core.KtFileContent
import io.kotlintest.shouldBe
import io.kotlintest.shouldNotBe
import io.kotlintest.specs.WordSpec
import org.mockito.kotlin.mock
import java.nio.file.Files

/**
 * @author Gillian Buijs
 * @contact https://buijs.dev
 *
 */
class AndroidActivityVisitorTest : WordSpec({

    "Using the KlutterActivityPrinter" should {

        val projectDir = Files.createTempDirectory("")
        val mainActivity = projectDir.resolve("mainActivity.kts").toFile()

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

            AndroidActivityVisitor(content).visit()

            mainActivity shouldNotBe null
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

            AndroidActivityVisitor(content).visit()

            mainActivity shouldNotBe null
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
                        someothermethod().blabla()
                    }
                
                }""".filter { !it.isWhitespace() }
        }

        "Append entire configureFlutterEngine function if its missing" {

            mainActivity.createNewFile()
            mainActivity.writeText(
                """
            package foo.bar.baz.appz


            import io.flutter.embedding.android.FlutterActivity
            
            @KlutterAdapter
            class MainActivity: FlutterActivity() {
            
                fun someOtherMethod(): String {}

            }
        """.trimIndent())

            val content = KtFileContent(
                file = mainActivity,
                ktFile = mock(),
                content = mainActivity.readText()
            )

            AndroidActivityVisitor(content).visit()

            mainActivity shouldNotBe null
            mainActivity.readText().filter { !it.isWhitespace() } shouldBe """
            package foo.bar.baz.appz


            import io.flutter.plugins.GeneratedPluginRegistrant
            import io.flutter.embedding.engine.FlutterEngine
            import androidx.annotation.NonNull
            import dev.buijs.klutter.adapter.GeneratedKlutterAdapter
            import io.flutter.plugin.common.MethodChannel
            import io.flutter.embedding.android.FlutterActivity
            
            @KlutterAdapter
            class MainActivity: FlutterActivity() {
            
                override fun configureFlutterEngine(@NonNull flutterEngine: FlutterEngine) {
                    MethodChannel(flutterEngine.dartExecutor,"KLUTTER")
                        .setMethodCallHandler{ call, result ->
                            GeneratedKlutterAdapter().handleMethodCalls(call, result)
                        }
                    GeneratedPluginRegistrant.registerWith(flutterEngine)
                }
            
                fun someOtherMethod(): String {}

            }""".filter { !it.isWhitespace() }
        }

    }

})