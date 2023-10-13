package dev.buijs.klutter.kore.project

data class Flutter(
    val version: Version,
    val os: OperatingSystem,
    val arch: Architecture
)

/**
 * Generate a unique display name for this Flutter configuration.
 *
 * Example: "3.0.5 (MACOS ARM64)".
 */
val Flutter.prettyPrint: String
    get() = "${version.prettyPrint} ($os $arch)"

/**
 * Convert a pretty printed String to [Flutter] data class.
 */
fun flutterFromString(prettyPrinted: String): Flutter {
        val osAndArchString = prettyPrinted.substringAfter("(").substringBefore(")").split(" ")
        val os = OperatingSystem.valueOf(osAndArchString[0].uppercase())
        val arch = Architecture.valueOf(osAndArchString[1])
        val versionString = prettyPrinted.substringBefore(" ")
        val versionSplitted = versionString.split(".")
        val version = Version(
            major = versionSplitted[0].toInt(),
            minor = versionSplitted[1].toInt(),
            patch = versionSplitted[2].toInt())
        return Flutter(version, os, arch)
    }

/**
 * Generate a unique folder name for this Flutter configuration.
 *
 * Example: "3.0.5.macos.arm64".
 */
val Flutter.folderName: String
    get() = "${version.prettyPrint}.${os.value.lowercase()}.${arch.name.lowercase()}"