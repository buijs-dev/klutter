package dev.buijs.klutter.kommand.flutterw

import dev.buijs.klutter.kore.KlutterException
import dev.buijs.klutter.kore.project.*
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.net.URL
import java.nio.channels.Channels
import java.io.*
import java.nio.file.Files
import java.util.zip.ZipFile

/**
 * For testing purposes to avoid downloading flutter SDK during tests.
 */
internal var dryRun = false

internal const val latestVersionPlaceholder = 9999

/**
 * Size of the buffer to read/write data
 */
private const val bufferSize = 4096

/**
 * Files which should be executable.
 */
private val executableFiles = setOf("flutter", "dart", "impellerc")

internal fun downloadFlutter(flutter: Flutter) =
    downloadFlutter(flutter.version, flutter.os, flutter.arch)

internal fun downloadFlutter(
    version: Version,
    os: OperatingSystem,
    arch: Architecture,
) {
    if(dryRun) return
    val path = findFlutterVersionOrThrow(os, arch, version)
    val zip = tempZipFile

    try {
        Channels.newChannel(URL(path).openStream()).use {  readableByteChannel ->
            val fileOutputStream = FileOutputStream(zip)
            val fileChannel = fileOutputStream.getChannel()
            fileChannel.transferFrom(readableByteChannel, 0, Long.MAX_VALUE)
        }
    } catch (e: IOException) {
       throw KlutterException("Failed to download Flutter distribution", e)
    }

    // Run init to make sure KlutterProjects/.cache exists in the user home.
    initKlutterProjectsFolder()
    zip.unzipTo(flutterSDK(Flutter(version, os, arch).folderName).absolutePath)
    zip.deleteRecursively()
}

private val tempZipFile: File
    get() = Files.createTempDirectory("klutter_download").toFile().resolve("flutter.zip")

private fun File.unzipTo(destDirectory: String) {
    File(destDirectory).run { if (!exists()) mkdirs() }
    ZipFile(this).use { zip ->
        zip.entries().asSequence().forEach { entry ->
            zip.getInputStream(entry).use { input ->
                "$destDirectory${File.separator}${entry.name}".let { filePath ->
                    if (entry.isDirectory) {
                        File(filePath).mkdir()
                    } else {
                        input.extractTo(filePath)
                    }
                }
            }
        }
    }
}

/**
 * Extracts a zip entry (file entry)
*/
private fun InputStream.extractTo(destFilePath: String) {
    BufferedOutputStream(FileOutputStream(destFilePath)).use { bos ->
        val bytesIn = ByteArray(bufferSize)
        var bytesLength = read(bytesIn)
        while(bytesLength != -1) {
            bos.write(bytesIn, 0, bytesLength)
            bytesLength = read(bytesIn)
        }
    }

    File(destFilePath).setExecutableIfApplicable()
}

private fun File.setExecutableIfApplicable() {
    if(executableFiles.contains(nameWithoutExtension)) {
        setExecutable(true)
    }
}