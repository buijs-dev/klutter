/* Copyright (c) 2021 - 2022 Buijs Software
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 */

package dev.buijs.klutter.core.tasks.plugin

import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiManager
import dev.buijs.klutter.core.KlutterTestProject
import org.jetbrains.kotlin.psi.KtFile
import spock.lang.Specification
import java.nio.file.Files
import com.intellij.openapi.project.Project

/**
 * @author Gillian Buijs
 */
class CreateFlutterPluginTaskSpec extends Specification {

    def "Verify the task creates a valid Flutter plugin"() {

        given: "a source project and output path"
        def project = new KlutterTestProject()
        def output = Files.createTempDirectory("bar").toFile()

        and: "kotlin multiplatform source files to be scanned"
        def podspec = new File("${project.platformDir.absolutePath}/platform.podspec")
        podspec.createNewFile()

        def buildGradle = new File("${project.platformDir.absolutePath}/build.gradle.kts")
        buildGradle.createNewFile()
        buildGradle.write(buildGradleText)

        and: "flutter documentation files to be copied"
        def flutter = new File("${project.projectDir.toFile().absolutePath}/flutter")
        flutter.mkdirs()

        def readme = new File("${flutter.absolutePath}/README.md")
        readme.createNewFile()

        def changelog = new File("${flutter.absolutePath}/CHANGELOG.md")
        changelog.createNewFile()

        def license = new File("${flutter.absolutePath}/LICENSE")
        license.createNewFile()

        and: "mocked scanning behaviour"
        def ktFileMock = GroovyMock(KtFile)
        def psiManagerMock = GroovyMock(PsiManager) {
            findFile(_ as VirtualFile) >> ktFileMock
        }
        def contextMock = GroovyMock(Project) {
            getService(PsiManager.class) >> psiManagerMock
        }

        and: "a task instance to test"
        def sut = new CreateFlutterPluginTask(
                contextMock,
                "example",
                "blablalbalbalalablalbalbalbala",
                "1.0.0",
                "https://github/some/developer/packages/plugin",
                "com.example",
                project.projectDir.toFile().absolutePath,
                 output.absolutePath,
                new FlutterLibraryDocumentation(readme, changelog, license)
        )

        when:
        sut.run()

        then:
        with(output) {
            new File("${absolutePath}/example/android").exists()
            new File("${absolutePath}/example/ios").exists()
            new File("${absolutePath}/example/lib").exists()
            new File("${absolutePath}/example/platform").exists()
            new File("${absolutePath}/example/build.gradle.kts").exists()
        }

        where:
        buildGradleText = '''
                plugins {
                            id("com.android.library")
                            id("dev.buijs.klutter.gradle")
                            kotlin("multiplatform")
                            kotlin("native.cocoapods")
                            kotlin("plugin.serialization") version "1.6.10"
                        }

                        version = "1.0"

                        klutter {

                            app {
                                // Flutter library name
                                name = "example_project"

                                // Flutter library version
                                version = "1.0.0"

                            }

                        }

                        kotlin {
                            android()
                            iosX64()
                            iosArm64()

                            cocoapods {
                                summary = "Some description for the Shared Module"
                                homepage = "Link to the Shared Module homepage"
                                ios.deploymentTarget = "14.1"
                                framework {
                                    baseName = "platform"
                                }
                            }

                            sourceSets {

                                val commonMain by getting {
                                    dependencies {
                                        api("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.2")
                                        api("dev.buijs.klutter:annotations-kmp:2022-pre-alpha-5")
                                        implementation("io.ktor:ktor-client-core:1.6.7")
                                        implementation("io.ktor:ktor-client-auth:1.6.7")
                                    }
                                }

                                val commonTest by getting

                            }
                        }

                        android {
                            compileSdk = 31
                            sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
                            defaultConfig {
                                minSdk = 21
                                targetSdk = 31
                            }
                        }'''

        ktFile =  '''package dev.buijs.klutter.activity

                    import io.flutter.plugins.GeneratedPluginRegistrant
                    import io.flutter.embedding.engine.FlutterEngine
                    import androidx.annotation.NonNull
                    import dev.buijs.klutter.adapter.GeneratedKlutterAdapter
                    import io.flutter.plugin.common.MethodChannel
                    import io.flutter.embedding.android.FlutterActivity
                    import dev.buijs.klutter.annotations.kmp.KlutterAdapter

                    @KlutterAdapter
                    class MainActivity: FlutterActivity() {

                        override fun configureFlutterEngine(@NonNull flutterEngine: FlutterEngine) {
                            MethodChannel(flutterEngine.dartExecutor,"KLUTTER")
                                .setMethodCallHandler{ call, result ->
                                    GeneratedKlutterAdapter().handleMethodCalls(call, result)
                                }
                        }

                    }'''




    }

}