package dev.buijs.klutter.plugins

import java.io.File
import java.nio.file.Files
import java.nio.file.Path

data class KlutterTestProject(
    val projectDir: Path = Files.createTempDirectory(""),
    val klutterDir: File = projectDir.resolve("klutter")
        .toAbsolutePath()
        .toFile()
        .also { it.mkdir() },
    val androidAppDir: File = projectDir.resolve("android/app")
        .toAbsolutePath()
        .toFile()
        .also { it.mkdirs()},
    val flutterDir: File = projectDir.toAbsolutePath().toFile()
        .also { it.mkdirs() },
    val flutterLibDir: File = flutterDir.resolve("lib")
        .toPath()
        .toAbsolutePath()
        .toFile()
        .also { it.mkdir() },
    val flutterMainFile: File = flutterLibDir.resolve("main.dart")
        .toPath()
        .toAbsolutePath()
        .toFile()
        .also { it.createNewFile() },
    val buildGradle: File = projectDir.resolve("build.gradle.kts")
        .toAbsolutePath()
        .toFile()
        .also { it.createNewFile() },
    val settingsGradle: File = projectDir.resolve("settings.gradle.kts")
        .toAbsolutePath()
        .toFile()
        .also { it.createNewFile() },
)