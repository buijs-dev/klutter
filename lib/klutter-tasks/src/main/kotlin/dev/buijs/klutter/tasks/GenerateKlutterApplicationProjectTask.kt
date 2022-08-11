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
package dev.buijs.klutter.tasks

import dev.buijs.klutter.kore.KlutterTask
import dev.buijs.klutter.kore.shared.execute
import dev.buijs.klutter.kore.shared.verifyExists
import java.io.File

internal const val klutterGradleVersion = "2022.r8-alpha"
internal const val klutterPubVersion = "0.1.3"
internal const val platformWidgetsVersion = "2.0.0"
internal const val kotlinVersion = "1.7.10"
internal const val kotlinSerializationVersion = "1.7.0"
internal const val androidGradleVersion = "7.2.1"
internal const val iosDeploymentTarget = "14.1"
internal const val androidCompileSdkVersion = "31"
internal const val androidMinSdkVersion = "21"
internal const val androidTargetSdkVersion = "31"
internal const val kotlinxSerializationVersion = "1.3.3"
internal const val kotlinxCoroutinesVersion = "1.6.3"
internal const val gradleWrapperDistributionUrl = "https\\://services.gradle.org/distributions/gradle-7.3.3-bin.zip"

/**
 * Task to generate a klutter plugin project.
 */
class GenerateKlutterApplicationProjectTask(
    /**
     * Path to the folder where to create the new project.
     */
    private val pathToRoot: String,

    /**
     * Name of the application.
     *
     * This name will be used when generating the flutter application
     * which ensures the name will be propagated to the Android and iOS artifact.
     *
     * Default value: "kompose_app_frontend".
     */
    appName: String? = null,

    /**
     * Name of the application organisation.
     *
     * This name will be used when generating the flutter application
     * which ensures the organisation name will be propagated to the Android and iOS artifact.
     *
     * Default value: "dev.buijs.klutter".
     */
    groupName: String? = null,

) : KlutterTask {

    private val appName: String = appName ?: "kompose_app_frontend"

    private val groupName: String = groupName ?: "dev.buijs.klutter"

    override fun run() {
        val root = File(pathToRoot).also { it.verifyExists() }
        root.createApp()
        root.createLib()
        root.createTestLib()
        root.addGradleWrapper()
        root.writeBuildGradleFile()
        root.writeSettingsGradleFile()
    }

    private fun File.createApp() {

        val folder = resolve("app").also { it.mkdir() }

        "flutter create $appName --org $groupName".execute(folder)
        "flutter create --org dev.buijs.klutter --template=plugin --platforms=android,ios kompose_app_backend".execute(folder)

        resolve("app/$appName/android")
            .copyRecursively(resolve("app/frontend/android"))
        resolve("app/$appName/ios")
            .copyRecursively(resolve("app/frontend/ios"))
        resolve("app/$appName/lib")
            .copyRecursively(resolve("app/frontend/lib"))
        resolve("app/$appName/.gitignore")
            .copyTo(resolve("app/frontend/.gitignore"))
        resolve("app/$appName/.metadata")
            .copyTo(resolve("app/frontend/.metadata"))
        resolve("app/$appName/.packages")
            .copyTo(resolve("app/frontend/.packages"))
        resolve("app/$appName/pubspec.yaml")
            .copyTo(resolve("app/frontend/pubspec.yaml"))
        resolve("app/$appName").deleteRecursively()

        resolve("app/kompose_app_backend/android")
            .copyRecursively(resolve("app/backend/android"))
        resolve("app/kompose_app_backend/ios")
            .copyRecursively(resolve("app/backend/ios"))
        resolve("app/kompose_app_backend/lib").mkdir()
        resolve("app/kompose_app_backend/.gitignore")
            .copyTo(resolve("app/backend/.gitignore"))
        resolve("app/kompose_app_backend/.metadata")
            .copyTo(resolve("app/backend/.metadata"))
        resolve("app/kompose_app_backend/.packages")
            .copyTo(resolve("app/backend/.packages"))
        resolve("app/kompose_app_backend/pubspec.yaml")
            .copyTo(resolve("app/backend/pubspec.yaml"))
        resolve("app/kompose_app_backend").deleteRecursively()

//        this.resolve("app/frontend/pubspec.yaml").writeText("""
//            |name: $appName
//            |description: Generated Flutter UI
//            |
//            |publish_to: 'none'
//            |
//            |environment:
//            |  sdk: ">=2.17.5 <3.0.0"
//            |
//            |dependencies:
//            |  flutter:
//            |    sdk: flutter
//            |
//            |  kompose_app_backend:
//            |    path: ../backend
//            |
//            |  cupertino_icons: ^1.0.2
//            |  flutter_platform_widgets: ^$platformWidgetsVersion
//            |  klutter: ^$klutterPubVersion
//            |
//            |dev_dependencies:
//            |  flutter_test:
//            |    sdk: flutter
//            |  flutter_lints: ^2.0.0
//            |
//            |flutter:
//            |  uses-material-design: true
//        """.trimMargin())
//
//        this.resolve("app/backend/pubspec.yaml").writeText("""
//            |name: kompose_app_backend
//            |description: Generated Flutter plugin to access Platform
//            |version: 0.0.1
//            |
//            |environment:
//            |  sdk: ">=2.17.5 <3.0.0"
//            |  flutter: ">=2.5.0"
//            |
//            |dependencies:
//            |  flutter:
//            |    sdk: flutter
//            |
//            |  klutter: ^$klutterPubVersion
//            |
//            |dev_dependencies:
//            |  flutter_test:
//            |    sdk: flutter
//            |  flutter_lints: ^2.0.0
//            |
//            |flutter:
//            |  plugin:
//            |    platforms:
//            |      android:
//            |        package: dev.buijs.klutter.kompose_app_backend
//            |        pluginClass: KomposeAppBackendPlugin
//            |      ios:
//            |        pluginClass: KomposeAppBackendPlugin
//        """.trimMargin())

        this.resolve("app/frontend/pubspec.yaml").writeText("""
            |name: $appName
            |description: Generated Flutter UI
            |
            |publish_to: 'none'
            |
            |environment:
            |  sdk: ">=2.17.5 <3.0.0"
            |
            |dependencies:
            |  flutter:
            |    sdk: flutter
            |
            |  kompose_app_backend:
            |    path: ../backend
            |
            |  cupertino_icons: ^1.0.2
            |  flutter_platform_widgets: ^$platformWidgetsVersion
            |  klutter:
            |   path: /Users/buijs/repos/klutter-dart
            |
            |dev_dependencies:
            |  flutter_test:
            |    sdk: flutter
            |  flutter_lints: ^2.0.0
            |
            |flutter:
            |  uses-material-design: true
        """.trimMargin())

        this.resolve("app/backend/pubspec.yaml").writeText("""
            |name: kompose_app_backend
            |description: Generated Flutter plugin to access Platform
            |version: 0.0.1
            |
            |environment:
            |  sdk: ">=2.17.5 <3.0.0"
            |  flutter: ">=2.5.0"
            |
            |dependencies:
            |  flutter:
            |    sdk: flutter
            |
            |  klutter: 
            |   path: /Users/buijs/repos/klutter-dart
            |
            |dev_dependencies:
            |  flutter_test:
            |    sdk: flutter
            |  flutter_lints: ^2.0.0
            |
            |flutter:
            |  plugin:
            |    platforms:
            |      android:
            |        package: dev.buijs.klutter.kompose_app_backend
            |        pluginClass: KomposeAppBackendPlugin
            |      ios:
            |        pluginClass: KomposeAppBackendPlugin
        """.trimMargin())

        "flutter pub get".execute(resolve("app/frontend"))
        "flutter pub get".execute(resolve("app/backend"))
        "flutter pub run klutter:consumer init".execute(resolve("app/frontend"))
        "flutter pub run klutter:consumer add=kompose_app_backend".execute(resolve("app/frontend"))
        "flutter pub run klutter:producer init".execute(resolve("app/backend"))

        resolve("app/backend/example").deleteRecursively()
        resolve("app/backend/build.gradle.kts").delete()
        resolve("app/backend/settings.gradle.kts").writeText(
            """// Copyright (c) 2021 - 2022 Buijs Software
              |//
              |// Permission is hereby granted, free of charge, to any person obtaining a copy
              |// of this software and associated documentation files (the "Software"), to deal
              |// in the Software without restriction, including without limitation the rights
              |// to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
              |// copies of the Software, and to permit persons to whom the Software is
              |// furnished to do so, subject to the following conditions:
              |//
              |// The above copyright notice and this permission notice shall be included in all
              |// copies or substantial portions of the Software.
              |//
              |// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
              |// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
              |// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
              |// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
              |// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
              |// OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
              |// SOFTWARE.
              |include(":klutter:kompose_app_backend")
              |include(":android")
              |""".trimMargin()
        )

        resolve("app/backend/android/build.gradle").addDependenciesToAndroidBuildGradle()
    }

}

internal fun File.addDependenciesToAndroidBuildGradle() {
    val regex  = """(android\s+?\{[\w\W]+)dependencies\s+?\{([^}]+?)}""".toRegex()
    val match1 = regex.find(readText())?.groupValues?.get(1) ?: ""
    val match2 = regex.find(readText())?.groupValues?.get(2) ?: ""
    val text = readText().replace(regex, """
        $match1
        dependencies {
            $match2    implementation "dev.buijs.klutter:kompose:$klutterGradleVersion"
                    implementation "org.jetbrains.kotlinx:kotlinx-serialization-json:$kotlinxSerializationVersion"
                    implementation "dev.buijs.klutter:annotations:$klutterGradleVersion"
        }
    """.trimIndent())
    writeText(text)

}

private fun File.createTestLib() {
    resolve("lib-test/src/main/kotlin/dev/buijs/klutter/integrationtest").mkdirs()
    resolve("lib-test/src/test/kotlin").mkdirs()
    resolve("lib-test/src/test/resources").mkdirs()
}

/**
 * Create the lib/build.gradle.kts file which applies all the
 * required Kotlin Multiplatform plugins and configuration.
 */
private fun File.createLib() {
    resolve("lib").mkdir()
    resolve("lib/build.gradle.kts").let { file ->
        file.createNewFile()
        file.writeText("""
            |plugins {
            |    id("com.android.library")
            |    kotlin("multiplatform")
            |    kotlin("native.cocoapods")
            |    kotlin("plugin.serialization") version "$kotlinSerializationVersion"
            |}
            |
            |version = ""
            |
            |kotlin {
            |    android()
            |    iosX64()
            |    iosArm64()
            |    iosArm32()
            |    jvm()
            |
            |    cocoapods {
            |        summary = "Kompose Platform"
            |        homepage = ""
            |        ios.deploymentTarget = "$iosDeploymentTarget"
            |        framework {
            |            baseName = "Platform"
            |        }
            |    }
            |
            |    sourceSets {
            |
            |        val commonMain by getting {
            |            dependencies {
            |                api("org.jetbrains.kotlinx:kotlinx-serialization-json:$kotlinxSerializationVersion")
            |                api("dev.buijs.klutter:annotations:$klutterGradleVersion")
            |                api("dev.buijs.klutter:kompose:$klutterGradleVersion")
            |            }
            |        }
            |
            |        val commonTest by getting {
            |            dependencies {
            |                implementation(kotlin("test-common"))
            |                implementation(kotlin("test-annotations-common"))
            |                implementation(kotlin("test-junit"))
            |                implementation("junit:junit:4.13.2")
            |                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:$kotlinxCoroutinesVersion")
            |            }
            |        }
            |
            |        val jvmMain by getting {
            |            dependencies {
            |                implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
            |                implementation("dev.buijs.klutter:annotations:$klutterGradleVersion")
            |                implementation("dev.buijs.klutter:core:$klutterGradleVersion")
            |                implementation("dev.buijs.klutter:kompose:$klutterGradleVersion")
            |            }
            |        }
            |
            |        val jvmTest by getting
            |
            |        val androidMain by getting {
            |            dependencies {
            |                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:$kotlinxCoroutinesVersion")
            |            }
            |        }
            |
            |        val androidTest by getting {
            |            dependencies {
            |                implementation(kotlin("test-junit"))
            |                implementation("junit:junit:4.13.2")
            |            }
            |        }
            |
            |        val iosX64Main by getting
            |        val iosArm64Main by getting
            |        val iosArm32Main by getting
            |        val iosMain by creating {
            |            dependsOn(commonMain)
            |            iosX64Main.dependsOn(this)
            |            iosArm64Main.dependsOn(this)
            |            iosArm32Main.dependsOn(this)
            |            dependencies {}
            |        }
            |
            |        val iosX64Test by getting
            |        val iosArm64Test by getting
            |        val iosArm32Test by getting
            |        val iosTest by creating {
            |            dependsOn(commonTest)
            |            iosX64Test.dependsOn(this)
            |            iosArm64Test.dependsOn(this)
            |            iosArm32Test.dependsOn(this)
            |        }
            |    }
            |}
            |
            |android {
            |    compileSdk = $androidCompileSdkVersion
            |    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
            |    sourceSets["main"].kotlin { srcDirs("src/androidMain/kotlin") }
            |    defaultConfig {
            |        minSdk = $androidMinSdkVersion
            |        targetSdk = $androidTargetSdkVersion
            |    }
            |}
            |
            |tasks.withType<Jar> {
            |    archiveBaseName.set("kompose")
            |}
            |""".trimMargin())
    }

    resolve("app/backend/platform/src").let { src ->
        src.copyRecursively(resolve("lib/src").also { it.mkdirs() })
    }

    resolve("app/backend/platform").deleteRecursively()

    resolve("lib/src/commonMain/kotlin/dev/buijs/klutter/kompose_app_backend/platform").let {
        it.mkdirs()
        it.resolve("Greeting.kt").also { file ->
            file.writeText(
                """package dev.buijs.klutter.kompose_app_backend.platform
                   |
                   |import dev.buijs.klutter.annotations.*
                   |import dev.buijs.klutter.annotations.KomposeState.*
                   |import dev.buijs.klutter.ui.controller.KomposeController
                   |import dev.buijs.klutter.ui.event.KlutterEvent
                   |import kotlinx.serialization.Serializable
                   |
                   |@Controller
                   |class GreetingController: KomposeController<Greeting>(
                   |    state = Greeting(
                   |        count = 0,
                   |        greeting = "Hello, ${"$"}{Platform().platform}! [0]",
                   |     )
                   |) {
                   |
                   |     override fun onEvent(event: String, data: Any?) {
                   |         when(event) {
                   |            GreetingSetMessage::class.qualifiedName -> {
                   |               state = Greeting(
                   |              count = state.count,
                   |             greeting = data as String,
                   |         )
                   |     }
                   | 
                   |     GreetingIncrementCount::class.qualifiedName -> {
                   |         state = Greeting(
                   |             count = state.count + 1,
                   |             greeting = state.greeting,
                   |         )
                   |     }
                   | 
                   |     else -> { }
                   |        }
                   | }
                   | 
                   | }
                   | 
                   |class GreetingSetMessage : KlutterEvent<GreetingSetMessage, GreetingController>()
                   |
                   |class GreetingIncrementCount : KlutterEvent<GreetingIncrementCount, GreetingController>()
                   |
                   |    @Serializable
                   |    @Stateful(type = EPHEMERAL)
                   |    open class Greeting(
                   |        val greeting: String,
                   |        val count: Int,
                   |    ): KlutterJSON<Greeting>() {
                   |
                   |        override fun data() = this
                   |
                   |        override fun strategy() = serializer()
                   |
                   |    }
                   |""".trimMargin()
            )
        }
    }

    resolve("lib/src/jvmMain/kotlin/dev/buijs/klutter/kompose_app_backend/platform").let {
        it.mkdirs()
        it.resolve("Platform.kt").also { file ->
            file.createNewFile()
            file.writeText(
                """package dev.buijs.klutter.kompose_app_backend.platform
                    |
                    |actual class Platform actual constructor() {
                    |    actual val platform: String = "JVM!"
                    |}
                """.trimMargin())
        }

        it.resolve("Home.kt").also { file ->
            file.createNewFile()
            file.writeText(
                """package dev.buijs.klutter.kompose_app_backend.platform
                  |
                  |import dev.buijs.klutter.annotations.KomposeView
                  |import dev.buijs.klutter.ui.*
                  |import dev.buijs.klutter.ui.builder.JetlagScreen
                  |import dev.buijs.klutter.ui.builder.JetlagUI
                  |
                  |@KomposeView
                  |class Home: JetlagUI(name = "Home") {
                  |
                  |    override fun screen(): JetlagScreen = {
                  |        SafeArea {
                  |            Scaffold {
                  |                Center {
                  |                    Column {
                  |                        children(
                  |                            title,
                  |                            navigateToFeed,
                  |                            NavTextButton(
                  |                                text = "Go to feed",
                  |                                route = "feed",
                  |                                arguments = "Data from Home"
                  |                            ),
                  |
                  |                            TextButton(
                  |                                event = GreetingIncrementCount::class,
                  |                                text = "increment",
                  |                            ),
                  |
                  |                            TextField(
                  |                                event = GreetingSetMessage::class,
                  |                                hint = "enter new greeting"
                  |                            )
                  |
                  |                        )
                  |                    }
                  |                }
                  |            }
                  |        }
                  |    }
                  |
                  |    private val title = Text(
                  |        data = "Hello!",
                  |        textAlign = TextAlign.center,
                  |    )
                  |
                  |    private val navigateToFeed = NavTextButton(
                  |        text = "Go to feed",
                  |        route = "feed",
                  |        arguments = "Data from Home"
                  |    )
                  |
                  |}
                  |""".trimMargin())
        }

        it.resolve("Feed.kt").also { file ->
            file.createNewFile()
            file.writeText(
                """package dev.buijs.klutter.kompose_app_backend.platform
                  |
                  |import dev.buijs.klutter.annotations.KomposeView
                  |import dev.buijs.klutter.ui.NavTextButton
                  |import dev.buijs.klutter.ui.Text
                  |import dev.buijs.klutter.ui.builder.KlutterScreen
                  |import dev.buijs.klutter.ui.builder.KlutterUI
                  |
                  |@KomposeView
                  |class Feed: KlutterUI(name = "Feed") {
                  |
                  |    override fun screen(): KlutterScreen = {
                  |        SafeArea {
                  |            Scaffold {
                  |                Center {
                  |                    Column {
                  |                        children(
                  |                            Text("Feed"),
                  |                            NavTextButton(
                  |                                text = "Go to home",
                  |                                route = "home",
                  |                                arguments = "Data from Feed"
                  |                            ),
                  |                        )
                  |                    }
                  |                }
                  |            }
                  |        }
                  |    }
                  |
                  |}
                  |""".trimMargin())
        }
    }

}

private fun File.addGradleWrapper() {

    // Update the gradle wrapper distributionUrl.
    val properties = resolve("app/backend/gradle/wrapper/gradle-wrapper.properties")
    val output = mutableListOf<String>()
    properties.readLines().forEach { line ->
        if(line.startsWith("distributionUrl=")) {
            output.add("distributionUrl=$gradleWrapperDistributionUrl")
        } else {
            output.add(line)
        }
    }
    properties.writeText(output.joinToString("\n") { it})

    // Copy the gradle wrapper files.
    resolve("app/backend/gradle").copyRecursively(resolve("gradle"))

    resolve("app/backend/gradle.properties")
        .copyTo(resolve("gradle.properties"))

    resolve("app/backend/gradlew.bat").let { file ->
        file.copyTo(resolve("gradlew.bat")).also { gradlew ->
            gradlew.setExecutable(true)
            gradlew.setReadable(true)
            gradlew.setWritable(true)
        }
    }

    resolve("app/backend/gradlew").let { file ->
        file.copyTo(resolve("gradlew")).also { gradlew ->
            gradlew.setExecutable(true)
            gradlew.setReadable(true)
            gradlew.setWritable(true)
        }
    }

    resolve("app/backend/android/local.properties")
        .copyTo(resolve("local.properties"))
}

/**
 * Generate the build.gradle.kts file in the root folder of a klutter application.
 *
 * This build.gradle.kts file applies the klutter gradle plugin and adds a few
 * convenience tasks to build the app:
 * - klutterBuild
 * - klutterCopyFramework
 * - klutterCopyAarFile
 *
 * Task klutterBuild is a wrapper task which executes
 * - clean
 * - build
 * - klutterGenerateAdapters
 * - klutterCopyFramework
 * - klutterCopyAarFile
 * - klutterGenerateUI
 *
 * Task klutterCopyFramework:
 * Copies the generated iOS framework from lib/build/fat-framework/release to app/backend/ios/Klutter.
 *
 * Task klutterCopyAarFile:
 * Copies the generated Android .aar file from lib/build/outputs/aar/lib-release.aar
 * to app/backend/android/klutter and renames the file from lib-release.aar to platform.aar.
 */
private fun File.writeBuildGradleFile() = resolve("build.gradle.kts").writeText("""
    |plugins {
    |    id("dev.buijs.klutter.gradle") version "$klutterGradleVersion"
    |}
    |
    |buildscript {
    |    dependencies {
    |        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion")
    |        classpath("com.android.tools.build:gradle:$androidGradleVersion")
    |    }
    |}
    |
    |klutter {
    |    application { name = "kompose" }
    |}
    |
    |tasks.register("klutterBuild", Exec::class) {
    |    commandLine(
    |        "bash", "./gradlew", "clean", "build",
    |        "klutterGenerateAdapters",
    |        "klutterCopyFramework",
    |        "klutterCopyAarFile",
    |        "klutterGenerateUI",
    |    )
    |}
    |
    |tasks.register("klutterCopyFramework", Copy::class) {
    |    from("lib/build/fat-framework/release")
    |    into("app/backend/ios/Klutter")
    |}
    |
    |tasks.register("klutterCopyAarFile", Copy::class) {
    |    from("lib/build/outputs/aar/lib-release.aar")
    |    into("app/backend/android/klutter")
    |    rename { fileName ->
    |        fileName.replace("lib-release", "platform")
    |    }
    |}
    |""".trimMargin())

/**
 * Generate the settings.gradle.kts file in the root folder of a klutter application.
 */
private fun File.writeSettingsGradleFile() = resolve("settings.gradle.kts").writeText("""
    |// Copyright (c) 2021 - 2022 Buijs Software
    |//
    |// Permission is hereby granted, free of charge, to any person obtaining a copy
    |// of this software and associated documentation files (the "Software"), to deal
    |// in the Software without restriction, including without limitation the rights
    |// to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
    |// copies of the Software, and to permit persons to whom the Software is
    |// furnished to do so, subject to the following conditions:
    |//
    |// The above copyright notice and this permission notice shall be included in all
    |// copies or substantial portions of the Software.
    |//
    |// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
    |// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
    |// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
    |// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
    |// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
    |// OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
    |// SOFTWARE.
    |include(":lib")
    |
    |dependencyResolutionManagement {
    |    repositories {
    |        gradlePluginPortal()
    |        google()
    |        mavenCentral()
    |        maven { url = uri("https://repsy.io/mvn/buijs-dev/klutter") }
    |    }
    |}
    |
    |pluginManagement {
    |    repositories {
    |        gradlePluginPortal()
    |        google()
    |        mavenCentral()
    |        maven { url = uri("https://repsy.io/mvn/buijs-dev/klutter") }
    |    }
    |}
    |""".trimMargin())
