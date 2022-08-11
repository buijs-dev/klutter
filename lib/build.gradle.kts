import org.jetbrains.dokka.gradle.DokkaTask

plugins {
    kotlin("jvm") version "1.7.10"
    id("org.jetbrains.dokka") version "1.6.10"
    id("org.jetbrains.kotlinx.kover") version "0.5.1"
    id("org.sonarqube") version "3.4.0.2513"
    id("klutter")
}

subprojects {
    plugins.apply("org.jetbrains.dokka")
}

kover {

    // KOVER destroys running with coverage from IDE
    isDisabled = hasProperty("nokover")

    coverageEngine.set(kotlinx.kover.api.CoverageEngine.JACOCO)

    jacocoEngineVersion.set("0.8.8")

    disabledProjects = setOf(
        // contains only annotations
        // and breaks Jacoco due to duplicate classes
        ":lib:klutter-annotations",

        // breaks Jacoco due to duplicate classes, haven't found a fix yet...
        ":lib:klutter-kompose",

        // a test-only module
        ":lib-test",
    )
}

sonarqube {
    properties {
        property("sonar.projectKey", "buijs-dev_klutter")
        property("sonar.organization", "buijs-dev")
        property("sonar.host.url", "https://sonarcloud.io")
        property(
            "sonar.coverage.jacoco.xmlReportPaths",
            rootProject.buildDir.resolve("koverage.xml").absolutePath
        )
    }
}

tasks.withType<DokkaTask>().configureEach {
    dokkaSourceSets {
        configureEach {
            includeNonPublic
        }
    }
}

tasks.dokkaHtmlMultiModule.configure {
    outputDirectory.set(layout.buildDirectory.dir("dokkaSite").map { it.asFile })
}

tasks.koverMergedXmlReport {
    isEnabled = true

    excludes = listOf(
        // A test-only module
        "dev.buijs.klutter.kore.test.*",
    )

    xmlReportFile.set(layout.buildDirectory.file("koverage.xml"))
}