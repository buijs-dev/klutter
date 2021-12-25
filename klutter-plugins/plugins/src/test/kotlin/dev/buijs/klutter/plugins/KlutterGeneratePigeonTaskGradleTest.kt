package dev.buijs.klutter.plugins


import io.kotlintest.shouldBe
import io.kotlintest.specs.WordSpec
import org.gradle.testkit.runner.GradleRunner
import java.nio.file.Files
import java.nio.file.Path

/**
 * @author Gillian Buijs
 *
 * Contact me: https://buijs.dev
 *
 */
class KlutterGeneratePigeonTaskGradleTest : WordSpec({

    "A configured Kotlin DSL builscript" should {
        "Lead to a successful build" {
            val projectDir = Path.of("blablabla")//Files.createTempDirectory("")
            val klutterDir = projectDir.resolve("klutter").toAbsolutePath().toFile()
            klutterDir.mkdirs()
            val androidDir = projectDir.resolve("android/app").toAbsolutePath().toFile()
            androidDir.mkdirs()
            val buildScript = klutterDir.resolve("build.gradle.kts")
            klutterDir.resolve("settings.gradle.kts").createNewFile()
            val flutterDir = projectDir.toAbsolutePath().toFile()
            flutterDir.mkdirs()

            buildScript.writeText("""
                plugins {
                    id("dev.buijs.klutter.gradle")
                }

                klutter {
                    android = File("$androidDir")
                    flutter = File("${flutterDir.absolutePath}")
                    services {

                        api("FooApi") {
                            func("footsie"){
                            
                                takes {
                                    parameter { name = "key"; type = "String" }
                                    parameter { name = "value"; type = "String" }
                                }
                               
                                gives { nothing() }
                            }
                        }

                        api("BarApi") {
                            func("search", async = true){
                            
                                takes {
                                    parameter { name = "keyword"; type = "String" }
                                }
                                
                                gives { DefinedList("Book") }
                            }
                        }

                        dataclass("Book") {
                            field { name = "title"; type = "String" }
                            field { name = "author"; type = "String" }
                        }

                        dataclass("Car") {
                            field { name = "make"; type = "String" }
                            field { name = "bhp"; type = "String" }
                        }

                        enumclass("State") {
                            values("pending", "success", "error")
                        }

                    }
                }

            """.trimIndent())

            GradleRunner.create()
                .withProjectDir(klutterDir)
                .withPluginClasspath()
                .withArguments("generateApi")
                .build()

            val adapterFile = flutterDir.resolve("lib/generated/adapter.dart").absoluteFile
            adapterFile.exists()

            val pigeonFile = klutterDir.resolve(".klutter/pigeon.dart").absoluteFile
            pigeonFile.exists()
            pigeonFile.readText().filter { !it.isWhitespace() } shouldBe """
                import 'package:pigeon/pigeon.dart';

                @HostApi()
                abstract class FooApi {
                  void footsie(String key, String value);
                }
                
                @HostApi()
                abstract class BarApi {
                  @async
                  List<Book> search(String keyword);
                }
                
                class Book {
                  String? title;
                  String? author;
                }
                
                class Car {
                  String? make;
                  String? bhp;
                }
                
                enum State {
                  pending,
                  success,
                  error,
                }
            """.filter { !it.isWhitespace() }

            //cleanup
            flutterDir.deleteRecursively()

        }
    }

})