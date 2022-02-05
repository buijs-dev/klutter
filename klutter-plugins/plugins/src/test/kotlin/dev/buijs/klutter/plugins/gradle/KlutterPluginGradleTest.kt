package dev.buijs.klutter.plugins.gradle


import io.kotlintest.shouldBe
import io.kotlintest.specs.WordSpec
import org.gradle.testkit.runner.GradleRunner
import java.nio.file.Path
import kotlin.io.path.createDirectory
import kotlin.io.path.createFile
import kotlin.io.path.writeText

/**
 * @author Gillian Buijs
 */
class KlutterPluginGradleTest : WordSpec({

    "A configured Kotlin DSL builscript" should {
        "Lead to a successful build" {
            val project = KlutterTestProject()
            val klutterDir = project.projectDir.resolve(".klutter").also { it.createDirectory() }
            val properties = klutterDir.resolve("klutter.properties").also { it.createFile() }
            properties.writeText("""
                       app.version.code=1
                       app.version.name=1.0.0
                       app.id=dev.buijs.klutter.example.basic
                       android.sdk.minimum=21
                       android.sdk.compile=31
                       android.sdk.target=31
                       ios.version=13.0
                       flutter.sdk.version=2.5.3
                       klutter.gradle.plugin.version=0.3.39-pre-alpha
                       klutter.annotations.kmp.version=0.2.49
                       kotlin.version=1.6.10
                       kotlinx.serialization.version=1.3.10
                       kotlinx.coroutines.version=1.8.35
                       gradle.version=7.0.4
                       flutter.sdk.location=/Users/boba/tools/flutter
            """.trimIndent())

            project.flutterMainFile.writeText("""
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

            val sourceFile = project.kmpDir.resolve("FakeClass.kt").absoluteFile
            sourceFile.createNewFile()
            sourceFile.writeText("""
                package foo.bar.baz

                import dev.buijs.klutter.annotations.Annotations

                class FakeClass {
                    @KlutterAdaptee(name = "DartMaul")
                    fun foo(): String {
                        return "Maul"
                    }

                    @KlutterAdaptee(name = "BabyYoda")
                    fun fooBar(): List<String> {
                        return listOf("baz")
                    }

                    @KlutterAdaptee(name = "ObiWan")
                    fun zeta(): List<String> =
                        listOf(foo()).map { str ->
                            "str = str                "
                        }.filter { baz ->
                            baz != ""
                        }

                }
                
                @Serializable
                @KlutterResponse
                enum class {
                    @SerialName("boom") BOOM,
                    @SerialName("boom boom") BOOM_BOOM,
                }
                
            """.trimIndent())

            val androidManifestDir = project.androidAppDir.resolve(Path.of("src", "main").toFile())
            androidManifestDir.mkdirs()

            val androidManifest = androidManifestDir.resolve("AndroidManifest.xml")
            androidManifest.createNewFile()
            androidManifest.writeText("""
                <manifest xmlns:android="http://schemas.android.com/apk/res/android"
                    package="foo.bar.cest.moi">
                   <application
                        android:label="example"
                        android:icon="@mipmap/ic_launcher">
                        <activity
                            android:exported="true"
                            android:name=".MainActivity"
                            android:launchMode="singleTop"
                            android:theme="@style/LaunchTheme"
                            android:configChanges="orientation|keyboardHidden|keyboard|screenSize|smallestScreenSize|locale|layoutDirection|fontScale|screenLayout|density|uiMode"
                            android:hardwareAccelerated="true"
                            android:windowSoftInputMode="adjustResize">
                            <!-- Specifies an Android theme to apply to this Activity as soon as
                                 the Android process has started. This theme is visible to the user
                                 while the Flutter UI initializes. After that, this theme continues
                                 to determine the Window background behind the Flutter UI. -->
                            <meta-data
                              android:name="io.flutter.embedding.android.NormalTheme"
                              android:resource="@style/NormalTheme"
                              />
                            <!-- Displays an Android View that continues showing the launch screen
                                 Drawable until Flutter paints its first frame, then this splash
                                 screen fades out. A splash screen is useful to avoid any visual
                                 gap between the end of Android's launch screen and the painting of
                                 Flutter's first frame. -->
                            <meta-data
                              android:name="io.flutter.embedding.android.SplashScreenDrawable"
                              android:resource="@drawable/launch_background"
                              />
                            <intent-filter>
                                <action android:name="android.intent.action.MAIN"/>
                                <category android:name="android.intent.category.LAUNCHER"/>
                            </intent-filter>
                        </activity>
                        <!-- Don't delete the meta-data below.
                             This is used by the Flutter tool to generate GeneratedPluginRegistrant.java -->
                        <meta-data
                            android:name="flutterEmbedding"
                            android:value="2" />
                    </application>
                </manifest>
            """.trimIndent())

            val mainActivityDir = project.androidAppDir.resolve(
                Path.of("src", "main", "java", "foo", "bar", "baz", "appz").toFile())

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

            val podspec = project.podspecDir.resolve("common.podspec").absoluteFile
            podspec.createNewFile()
            podspec.writeText("""
                Pod::Spec.new do |spec|
                    spec.name                     = 'app_backend'
                    spec.version                  = '1.0.0'
                    spec.homepage                 = 'Link to the Shared Module homepage'
                    spec.source                   = { :git => "Not Published", :tag => "Cocoapods/#{spec.name}/#{spec.version}" }
                    spec.authors                  = ''
                    spec.license                  = ''
                    spec.summary                  = 'Some description for the Shared Module'

                    spec.vendored_frameworks      = "build/fat-framework/debug/Platform.framework"
                    spec.libraries                = "c++"
                    spec.module_name              = "#{spec.name}_umbrella"

                    spec.ios.deployment_target = '13.0' 
                    spec.pod_target_xcconfig = { 'EXCLUDED_ARCHS[sdk=iphonesimulator*]' => 'arm64' } 
                    spec.user_target_xcconfig = { 'EXCLUDED_ARCHS[sdk=iphonesimulator*]' => 'arm64' } 


                                

                    spec.pod_target_xcconfig = {
                        'KOTLIN_PROJECT_PATH' => ':app-backend',
                        'PRODUCT_MODULE_NAME' => 'app_backend',
                    }

                    spec.script_phases = [
                        {
                            :name => 'Build app_backend',
                            :execution_position => :before_compile,
                            :shell_path => '/bin/sh',
                            :script => <<-SCRIPT
                                if [ "YES" = "                blabla                " ]; then
                                  echo "Skipping Gradle build task invocation due to COCOAPODS_SKIP_KOTLIN_BUILD environment variable set to \"YES\""
                                  exit 0
                                fi
                                set -ev
                                REPO_ROOT="                blabla                "
                                "                blabla                /../gradlew" -p "                blabla                "                 pathsz                :syncFramework \
                                    -Pkotlin.native.cocoapods.platform=                blabla                 \
                                    -Pkotlin.native.cocoapods.archs="                blabla                " \
                                    -Pkotlin.native.cocoapods.configuration=                blabla
                            SCRIPT
                        }
                    ]
                end
            """.trimIndent())


            val frameworkinfo = project.appFrameworkInfoPlist
            frameworkinfo.writeText(
                    """
              <?xml version="1.0" encoding="UTF-8"?>
                <!DOCTYPE plist PUBLIC "-//Apple//DTD PLIST 1.0//EN" "http://www.apple.com/DTDs/PropertyList-1.0.dtd">
                <plist version="1.0">
                <dict>
                  <key>CFBundleDevelopmentRegion</key>
                  <string>en</string>
                  <key>CFBundleExecutable</key>
                  <string>App</string>
                  <key>CFBundleIdentifier</key>
                  <string>io.flutter.flutter.app</string>
                  <key>CFBundleInfoDictionaryVersion</key>
                  <string>6.0</string>
                  <key>CFBundleName</key>
                  <string>App</string>
                  <key>CFBundlePackageType</key>
                  <string>FMWK</string>
                  <key>CFBundleShortVersionString</key>
                  <string>1.0</string>
                  <key>CFBundleSignature</key>
                  <string>????</string>
                  <key>CFBundleVersion</key>
                  <string>1.0</string>
                  <key>MinimumOSVersion</key>
                  <string>9.0</string>
                </dict>
                </plist>
            """.trimIndent()
                )

            project.buildGradle.writeText("""
                plugins {
                    id("dev.buijs.klutter.gradle")
                }

                klutter {
                    multiplatform {
                        source = "${project.kmpDir}"
                    }
                }

            """.trimIndent())

            GradleRunner.create()
                .withProjectDir(project.projectDir.toFile())
                .withPluginClasspath()
                .withArguments("generate adapters","--stacktrace")
                .build()

            val generatedFile = project.androidAppDir.resolve(
                Path.of("src", "main", "java", "dev", "buijs", "klutter", "adapter", "GeneratedKlutterAdapter.kt").toFile())

            generatedFile.exists()
            generatedFile.readText().filter { !it.isWhitespace() } shouldBe """
               package dev.buijs.klutter.adapter

                import foo.bar.baz.FakeClass
                import io.flutter.plugin.common.MethodChannel
                import io.flutter.plugin.common.MethodCall
                import kotlinx.coroutines.CoroutineScope
                import kotlinx.coroutines.Dispatchers
                import kotlinx.coroutines.launch
                
                /**
                 * Generated code by the Klutter Framework
                 */
                class GeneratedKlutterAdapter {
                
                    private val mainScope = CoroutineScope(Dispatchers.Main)   
                    
                    fun handleMethodCalls(call: MethodCall, result: MethodChannel.Result) {
                        mainScope.launch {
                           when (call.method) {
                                "DartMaul" -> {
                                    result.success(FakeClass().foo())
                                }
                                "BabyYoda" -> {
                                    result.success(FakeClass().fooBar().toKJson())
                                }
                                "ObiWan" -> {
                                    result.success(FakeClass().zeta().toKJson())
                                } 
                                else -> result.notImplemented()
                           }
                        }
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

        }
    }

})