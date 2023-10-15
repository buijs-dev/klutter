package dev.buijs.klutter.kore.project

import dev.buijs.klutter.kore.KlutterException

const val latestVersionPlaceholder = 9999

val compatibleFlutterVersions = mapOf(
    windows("https://storage.googleapis.com/flutter_infra_release/releases/stable/windows/flutter_windows_3.0.5-stable.zip", 3, 0, 5),
    windows("https://storage.googleapis.com/flutter_infra_release/releases/stable/windows/flutter_windows_3.3.10-stable.zip", 3, 3, 10),
    windows("https://storage.googleapis.com/flutter_infra_release/releases/stable/windows/flutter_windows_3.7.12-stable.zip", 3, 7, 12),
    windows("https://storage.googleapis.com/flutter_infra_release/releases/stable/windows/flutter_windows_3.10.6-stable.zip", 3, 10, 6),
    linux("https://storage.googleapis.com/flutter_infra_release/releases/stable/linux/flutter_linux_3.0.5-stable.tar.xz", 3, 0, 5),
    linux("https://storage.googleapis.com/flutter_infra_release/releases/stable/linux/flutter_linux_3.3.10-stable.tar.xz", 3, 3, 10),
    linux("https://storage.googleapis.com/flutter_infra_release/releases/stable/linux/flutter_linux_3.7.12-stable.tar.xz", 3, 7, 12),
    linux("https://storage.googleapis.com/flutter_infra_release/releases/stable/linux/flutter_linux_3.10.6-stable.tar.xz", 3, 10, 6),
    macosX64("https://storage.googleapis.com/flutter_infra_release/releases/stable/macos/flutter_macos_3.0.5-stable.zip", 3, 0, 5),
    macosX64("https://storage.googleapis.com/flutter_infra_release/releases/stable/macos/flutter_macos_3.3.10-stable.zip", 3, 3, 10),
    macosX64("https://storage.googleapis.com/flutter_infra_release/releases/stable/macos/flutter_macos_3.7.12-stable.zip", 3, 7, 12),
    macosX64("https://storage.googleapis.com/flutter_infra_release/releases/stable/macos/flutter_macos_3.10.6-stable.zip", 3, 10, 6),
    macosArm64("https://storage.googleapis.com/flutter_infra_release/releases/stable/macos/flutter_macos_arm64_3.0.5-stable.zip", 3, 0, 5),
    macosArm64("https://storage.googleapis.com/flutter_infra_release/releases/stable/macos/flutter_macos_arm64_3.3.10-stable.zip", 3, 3, 10),
    macosArm64("https://storage.googleapis.com/flutter_infra_release/releases/stable/macos/flutter_macos_arm64_3.7.12-stable.zip", 3, 7, 12),
    macosArm64("https://storage.googleapis.com/flutter_infra_release/releases/stable/macos/flutter_macos_arm64_3.10.6-stable.zip", 3, 10, 6),
)

data class FlutterDistribution(
    val version: Version,
    val os: OperatingSystem,
    val arch: Architecture
)

/**
 * The full Flutter distribution version in format major.minor.patch (platform architecture).
 *
 * Example: 3.0.5 MACOS (ARM64).
 */
@JvmInline
value class PrettyPrintedFlutterDistribution(internal val s: String)

/**
 * The full Flutter distribution version in format major.minor.patch.platform.architecture.
 *
 * Example: 3.0.5.windows.x64.
 */
@JvmInline
value class FlutterDistributionFolderName(internal val s: String)

internal val String.asFlutterDistributionFolderName
    get() = FlutterDistributionFolderName(this)

/**
 * Generate a unique display name for this Flutter configuration.
 *
 * Example: "3.0.5 (MACOS ARM64)".
 */
val FlutterDistribution.prettyPrintedString: PrettyPrintedFlutterDistribution
    get() = PrettyPrintedFlutterDistribution("${version.prettyPrint} ($os $arch)")

val PrettyPrintedFlutterDistribution.flutterDistribution: FlutterDistribution
    get() {
        val prettyPrinted = this.s
        if("$this" == "$latestVersionPlaceholder")
            return latestFlutterVersion(currentOperatingSystem)

        val osAndArchString = prettyPrinted.substringAfter("(").substringBefore(")").split(" ")
        val os = OperatingSystem.valueOf(osAndArchString[0].uppercase())
        val arch = Architecture.valueOf(osAndArchString[1])
        val versionString = prettyPrinted.substringBefore(" ")
        val versionSplitted = versionString.split(".")
        val version = Version(
            major = versionSplitted[0].toInt(),
            minor = versionSplitted[1].toInt(),
            patch = versionSplitted[2].toInt())
        return FlutterDistribution(version, os, arch)
    }

/**
 * Generate a unique folder name for this Flutter configuration.
 *
 * Example: "3.0.5.macos.arm64".
 */
val FlutterDistribution.folderNameString: FlutterDistributionFolderName
    get() = FlutterDistributionFolderName("${version.prettyPrint}.${os.value.lowercase()}.${arch.name.lowercase()}")

val FlutterDistributionFolderName.flutterDistribution: FlutterDistribution
    get() {
        val parts = this.s.uppercase().split(".")
        val os = OperatingSystem.valueOf(parts[3])
        val arch = Architecture.valueOf(parts[4])
        val version = Version(
            major = parts[0].toInt(),
            minor = parts[1].toInt(),
            patch = parts[2].toInt())
        return FlutterDistribution(version, os, arch)
    }

val PrettyPrintedFlutterDistribution.folderName: FlutterDistributionFolderName
    get() = FlutterDistributionFolderName("$version.${os.value}.${os.name}.${arch.name}".lowercase())

val PrettyPrintedFlutterDistribution.version: String
    get() = "$this".substringBefore(" ")

val PrettyPrintedFlutterDistribution.arch: Architecture
    get() = Architecture.valueOf("$this".substringAfter("(").substringBefore(")").split(" ")[1])

val PrettyPrintedFlutterDistribution.os: OperatingSystem
    get() = OperatingSystem.valueOf("$this".substringAfter("(").substringBefore(")").split(" ")[0])

fun latestFlutterVersion(os: OperatingSystem) =
    flutterVersionsDescending(os).first()

fun flutterVersionsDescending(os: OperatingSystem) =
    compatibleFlutterVersions.keys
        .filter { version -> version.os == os }
        .sortedWith(compareBy({it.version.major}, {it.version.minor}, {it.version.patch}))
        .toSet()

fun flutterDownloadPathOrThrow(os: OperatingSystem, arch: Architecture, version: Version): String =
    compatibleFlutterVersions[FlutterDistribution(version, os, arch)]
        ?: throw KlutterException("No compatible Flutter version found for operating system $os + $arch + $version.")

private fun windows(path: String, vararg v: Int) =
    FlutterDistribution(
        os = OperatingSystem.WINDOWS,
        arch = Architecture.X64,
        version = Version(v[0], v[1], v[2])
    ) to path

private fun linux(path: String, vararg v: Int) =
    FlutterDistribution(
        os = OperatingSystem.LINUX,
        arch = Architecture.X64,
        version = Version(v[0], v[1], v[2])
    ) to path

private fun macosX64(path: String, vararg v: Int) =
    FlutterDistribution(
        os = OperatingSystem.MACOS,
        arch = Architecture.X64,
        version = Version(v[0], v[1], v[2])
    ) to path

private fun macosArm64(path: String, vararg v: Int) =
    FlutterDistribution(
        os = OperatingSystem.MACOS,
        arch = Architecture.ARM64,
        version = Version(v[0], v[1], v[2])
    ) to path