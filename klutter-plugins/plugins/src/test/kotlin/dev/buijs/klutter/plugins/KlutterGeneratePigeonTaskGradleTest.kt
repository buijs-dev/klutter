package dev.buijs.klutter.plugins


import io.kotlintest.shouldBe
import io.kotlintest.specs.WordSpec
import org.gradle.testkit.runner.GradleRunner
import java.nio.file.Files
import java.nio.file.Path

/**
 * @author Gillian Buijs
 * @contact https://buijs.dev
 */
class KlutterGeneratePigeonTaskGradleTest : WordSpec({

    "A configured Kotlin DSL builscript" should {
        "Lead to a successful build" {
            val project = KlutterTestProject()
            val buildScript = project.buildGradle
            val flutterDir = project.flutterDir
            val klutterDir = project.klutterDir

            buildScript.writeText("""
                plugins {
                    id("dev.buijs.klutter.gradle")
                }

                klutter {
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
                .withProjectDir(project.projectDir.toFile())
                .withPluginClasspath()
                .withArguments("generateApi")
                .build()

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

        }
    }

})