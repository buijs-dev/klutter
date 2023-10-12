package dev.buijs.klutter.kommand.flutterw

import dev.buijs.klutter.kore.KlutterException

enum class OperatingSystem(val value: String) {
    WINDOWS("windows"),
    MACOS("macos")
}

enum class Architecture {
    X64,
    ARM64,
}

data class Version(
    val major: Int,
    val minor: Int,
    val patch: Int,
)

data class FlutterVersion(
    val id: Version,
    val os: OperatingSystem,
    val arch: Architecture
)

internal val FlutterVersion.folderName: String
    get() = "${id.prettyPrint}.${os.value.lowercase()}.${arch.name.lowercase()}"

internal val Version.prettyPrint: String
    get() = "${major}.${minor}.${patch}"

internal fun compatibleFlutterVersionSet() = buildSet {
    for(entry in compatibleFlutterVersions) {
        val os = entry.key
        entry.value.forEach { (arch, versions) ->
            for(version in versions.keys) {
                add(FlutterVersion(version, os, arch))
            }
        }
    }
}

fun findFlutterVersionOrThrow(
    os: OperatingSystem,
    arch: Architecture,
    version: Version
): String {
    val versionsByArch = compatibleFlutterVersions[os]
    return when {
        versionsByArch == null -> throw KlutterException("No compatible Flutter version found for operating system $os")
        else -> when (val versions = versionsByArch[arch]) {
            null -> throw KlutterException("No compatible Flutter version found for operating system $os + $arch (possible architectures: ${versionsByArch.map { it.key }})")
            else -> {

                val matches: (Int, Set<Version>, (Version) -> Int) -> Set<Version> = { v, possibleVersions, selector ->
                    if (v == latestVersionPlaceholder) {
                        setOf(possibleVersions.maxByOrNull { selector(it) }!!)
                    } else {
                        possibleVersions.filter { selector(it) == v }.toSet()
                    }
                }

                val findMatch = { matching: Map<Version, String>,
                                  major: Int ->
                    { minor: Int ->
                        { patch: Int ->
                            val byMajor = matches(major, matching.keys) { p -> p.major }
                            val byMinor = matches(minor, byMajor) { p -> p.minor }
                            matches(patch, byMinor) { p -> p.patch }.firstOrNull()
                                ?.let { versionNotNull -> matching[versionNotNull] }
                        }
                    }
                }

                findMatch(versions, version.major)(version.minor)(version.patch)
                    ?: throw KlutterException("No compatible Flutter version found for operating system $os + $arch + $version (possible versions: ${versions.map { it.key }})")
            }
        }
    }
}

private val compatibleFlutterVersions = buildMap {
    put(OperatingSystem.WINDOWS, buildMap {
        put(
            Architecture.X64, mapOf(
                Version(
                    2,
                    10,
                    5
                ) to "https://storage.googleapis.com/flutter_infra_release/releases/stable/windows/flutter_windows_2.10.5-stable.zip",
                Version(
                    3,
                    0,
                    5
                ) to "https://storage.googleapis.com/flutter_infra_release/releases/stable/windows/flutter_windows_3.0.5-stable.zip",
                Version(
                    3,
                    3,
                    10
                ) to "https://storage.googleapis.com/flutter_infra_release/releases/stable/windows/flutter_windows_3.3.10-stable.zip",
                Version(
                    3,
                    7,
                    12
                ) to "https://storage.googleapis.com/flutter_infra_release/releases/stable/windows/flutter_windows_3.7.12-stable.zip",
                Version(
                    3,
                    10,
                    6
                ) to "https://storage.googleapis.com/flutter_infra_release/releases/stable/windows/flutter_windows_3.10.6-stable.zip"
            )
        )
    })

    put(OperatingSystem.MACOS, buildMap {
        put(
            Architecture.X64, mapOf(
                Version(
                    2,
                    10,
                    5
                ) to "https://storage.googleapis.com/flutter_infra_release/releases/stable/macos/flutter_macos_2.10.5-stable.zip",
                Version(
                    3,
                    0,
                    5
                ) to "https://storage.googleapis.com/flutter_infra_release/releases/stable/macos/flutter_macos_3.0.5-stable.zip",
                Version(
                    3,
                    3,
                    10
                ) to "https://storage.googleapis.com/flutter_infra_release/releases/stable/macos/flutter_macos_3.3.10-stable.zip",
                Version(
                    3,
                    7,
                    12
                ) to "https://storage.googleapis.com/flutter_infra_release/releases/stable/macos/flutter_macos_3.7.12-stable.zip",
                Version(
                    3,
                    10,
                    6
                ) to "https://storage.googleapis.com/flutter_infra_release/releases/stable/macos/flutter_macos_3.10.6-stable.zip",
            )
        )

        put(
            Architecture.ARM64, mapOf(
                Version(
                    3,
                    0,
                    5
                ) to "https://storage.googleapis.com/flutter_infra_release/releases/stable/macos/flutter_macos_arm64_3.0.5-stable.zip",
                Version(
                    3,
                    3,
                    10
                ) to "https://storage.googleapis.com/flutter_infra_release/releases/stable/macos/flutter_macos_arm64_3.3.10-stable.zip",
                Version(
                    3,
                    7,
                    12
                ) to "https://storage.googleapis.com/flutter_infra_release/releases/stable/macos/flutter_macos_arm64_3.7.12-stable.zip",
                Version(
                    3,
                    10,
                    6
                ) to "https://storage.googleapis.com/flutter_infra_release/releases/stable/macos/flutter_macos_3.10.6-stable.zip",
            )
        )
    })
}

