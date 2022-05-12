package dev.buijs.klutter.core.tasks.plugin

import dev.buijs.klutter.core.*
import dev.buijs.klutter.core.KlutterPrinter
import dev.buijs.klutter.core.KlutterWriter
import dev.buijs.klutter.core.tasks.shared.DefaultWriter
import java.io.File

/**
 * @author Gillian Buijs
 */
internal class AndroidBuildGradleGenerator(
    private val path: File,
    private val config: FlutterLibraryConfig,
    private val versions: DependencyVersions,
): KlutterFileGenerator() {

    override fun printer() = AndroidBuildGradlePrinter(
        groupId = "${config.developerOrganisation}.${config.libraryName}",
        version = config.libraryVersion,
        androidGradleVersion = versions.androidGradleVersion,
        kotlinVersion = versions.kotlinVersion,
        kotlinxVersion = versions.kotlinxVersion,
        klutterVersion = versions.klutterVersion,
        compileSdkVersion = versions.compileSdkVersion,
        minSdkVersion = versions.minSdkVersion,
    )

    override fun writer() = DefaultWriter(path, printer().print())

}

internal class AndroidBuildGradlePrinter(
    private val groupId: String,
    private val version: String,
    private val androidGradleVersion: String,
    private val kotlinVersion: String,
    private val kotlinxVersion: String,
    private val klutterVersion: String,
    private val compileSdkVersion: Int,
    private val minSdkVersion: Int,
): KlutterPrinter {

    override fun print() = """
            |group '$groupId'
            |version '$version'
            |
            |buildscript {
            |
            |    repositories {
            |        google()
            |        mavenCentral()
            |        maven { url = uri("https://repsy.io/mvn/buijs-dev/klutter") }
            |    }
            |
            |    dependencies {
            |        classpath 'com.android.tools.build:gradle:$androidGradleVersion'
            |        classpath "org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion"
            |        classpath "dev.buijs.klutter:core:$klutterVersion"
            |    }
            |}
            |
            |rootProject.allprojects {
            |    repositories {
            |        google()
            |        mavenCentral()
            |        maven { url = uri("https://repsy.io/mvn/buijs-dev/klutter") }
            |    }
            |}
            |
            |apply plugin: 'com.android.library'
            |apply plugin: 'kotlin-android'
            |
            |android {
            |    compileSdkVersion $compileSdkVersion
            |
            |    compileOptions {
            |        sourceCompatibility JavaVersion.VERSION_1_8
            |        targetCompatibility JavaVersion.VERSION_1_8
            |    }
            |
            |    kotlinOptions {
            |        jvmTarget = '1.8'
            |    }
            |
            |    sourceSets {
            |        main.java.srcDirs += 'src/main/kotlin'
            |    }
            |
            |    defaultConfig {
            |        minSdkVersion $minSdkVersion
            |    }
            |}
            |
            |dependencies {
            |    runtimeOnly "org.jetbrains.kotlinx:kotlinx-coroutines-android:$kotlinxVersion"
            |    implementation "org.jetbrains.kotlin:kotlin-stdlib-jdk7:$kotlinVersion"
            |    implementation "dev.buijs.klutter:core:$klutterVersion"
            |    implementation project(":platform")
            |}
            |
            |""".trimMargin()

}