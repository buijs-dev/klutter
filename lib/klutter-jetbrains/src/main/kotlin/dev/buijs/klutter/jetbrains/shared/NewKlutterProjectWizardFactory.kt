package dev.buijs.klutter.jetbrains.shared

fun String.startsWithAny(vararg prefixes: String): Boolean {
    return prefixes.any { this.startsWith(it) }
}

fun getLoadedPackageVersions(vararg excludePrefixes: String): String {
    val packages = Package.getPackages()
    val filteredPackages = packages.filterNot {
        it.name.startsWithAny(*excludePrefixes)
    }
    val versionsText = filteredPackages
        .map { "\t${it.name}: ${it.implementationTitle} v(${it.implementationVersion}) by ${it.implementationVendor}" }
        .sorted()
        .joinToString("\n")
    return "Versions of ${filteredPackages.size} loaded packages:\n${versionsText}"
}