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

package dev.buijs.klutter.core.tasks

import spock.lang.Ignore
import spock.lang.Specification

import java.nio.file.Files

class ApplicationGeneratorTaskSpec extends Specification {

    @Ignore // TODO template contains local paths
    def "Verify kompose app is generated"() {

        given:
        def pathToRoot = Files.createTempDirectory("").toFile().absolutePath

        when:
        new ApplicationGeneratorTask(pathToRoot).run()

        then:
        def appFolder = new File("${pathToRoot}/app")

        and:
        appFolder.exists()

    }

    def "Verify dependencies are added correctly to android/build.gradle"() {

        given:
        def file = Files.createTempFile("", "").toFile()
        file.write(buildGradle)

        when:
        ApplicationGeneratorTaskKt.addDependenciesToAndroidBuildGradle(file)

        then:
        with(file.text) {
            def actual = it.replace(" ", "").replace("\n", "")
            def expected2 = expected.replace(" ", "").replace("\n", "")
            actual == expected2
        }

        where:
        buildGradle = """
            group 'dev.buijs.klutter.kompose_app_backend'
            version '0.0.1'
            
            buildscript {
            
                repositories {
                    google()
                    mavenCentral()
                    maven { url = uri("https://repsy.io/mvn/buijs-dev/klutter") }
                }
            
                dependencies {
                    classpath 'com.android.tools.build:gradle:7.0.4'
                    classpath "org.jetbrains.kotlin:kotlin-gradle-plugin:1.6.10"
                    classpath "dev.buijs.klutter:core:0.16.32"
                }
            }
            
            rootProject.allprojects {
                repositories {
                    google()
                    mavenCentral()
                    maven { url = uri("https://repsy.io/mvn/buijs-dev/klutter") }
                }
            }
            
            apply plugin: 'com.android.library'
            apply plugin: 'kotlin-android'
            
            android {
                compileSdkVersion 31
            
                compileOptions {
                    sourceCompatibility JavaVersion.VERSION_1_8
                    targetCompatibility JavaVersion.VERSION_1_8
                }
            
                kotlinOptions {
                    jvmTarget = '1.8'
                }
            
                sourceSets {
                    main.java.srcDirs += 'src/main/kotlin'
                }
            
                defaultConfig {
                    minSdkVersion 21
                }
            }
            
            dependencies {
                runtimeOnly "org.jetbrains.kotlinx:kotlinx-coroutines-android:1.3.2"
                implementation "org.jetbrains.kotlin:kotlin-stdlib-jdk7:1.6.10"
                implementation "dev.buijs.klutter:core:0.16.32"
                implementation project(":klutter:kompose_app_backend")
            }
            
            java {
                sourceCompatibility = JavaVersion.VERSION_1_8
                targetCompatibility = JavaVersion.VERSION_1_8
            }
        """

        expected = """
            group 'dev.buijs.klutter.kompose_app_backend'
            version '0.0.1'
            
            buildscript {
            
                repositories {
                    google()
                    mavenCentral()
                    maven { url = uri("https://repsy.io/mvn/buijs-dev/klutter") }
                }
            
                dependencies {
                    classpath 'com.android.tools.build:gradle:7.0.4'
                    classpath "org.jetbrains.kotlin:kotlin-gradle-plugin:1.6.10"
                    classpath "dev.buijs.klutter:core:0.16.32"
                }
            }
            
            rootProject.allprojects {
                repositories {
                    google()
                    mavenCentral()
                    maven { url = uri("https://repsy.io/mvn/buijs-dev/klutter") }
                }
            }
            
            apply plugin: 'com.android.library'
            apply plugin: 'kotlin-android'
            
            android {
                compileSdkVersion 31
            
                compileOptions {
                    sourceCompatibility JavaVersion.VERSION_1_8
                    targetCompatibility JavaVersion.VERSION_1_8
                }
            
                kotlinOptions {
                    jvmTarget = '1.8'
                }
            
                sourceSets {
                    main.java.srcDirs += 'src/main/kotlin'
                }
            
                defaultConfig {
                    minSdkVersion 21
                }
            }
            
            dependencies {
                runtimeOnly "org.jetbrains.kotlinx:kotlinx-coroutines-android:1.3.2"
                implementation "org.jetbrains.kotlin:kotlin-stdlib-jdk7:1.6.10"
                implementation "dev.buijs.klutter:core:0.16.32"
                implementation project(":klutter:kompose_app_backend")
                implementation "dev.buijs.klutter:kompose:${ApplicationGeneratorTaskKt.klutterGradleVersion}"
                implementation "org.jetbrains.kotlinx:kotlinx-serialization-json:${ApplicationGeneratorTaskKt.kotlinxSerializationVersion}"
                implementation "dev.buijs.klutter:annotations:${ApplicationGeneratorTaskKt.klutterGradleVersion}"
            }
            
            java {
                sourceCompatibility = JavaVersion.VERSION_1_8
                targetCompatibility = JavaVersion.VERSION_1_8
            }
        """
    }

}