package dev.buijs.klutter.kore.project

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

val Version.prettyPrint: String
    get() = "${major}.${minor}.${patch}"

val isWindows: Boolean
    get() = System.getProperty("os.name").uppercase().contains("WIN")