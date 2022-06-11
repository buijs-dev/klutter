plugins {
    kotlin("jvm") version "1.6.10"
    id("org.jetbrains.dokka") version "1.6.10"
    id("org.jetbrains.kotlinx.kover") version "0.5.1"
}

subprojects {
    plugins.apply("org.jetbrains.dokka")
}

buildscript {

    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
    }

    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.6.10")
        classpath("com.android.tools.build:gradle:7.0.4")
    }
}

allprojects {

    val properties = HashMap<String, String>().also { map ->
        File("${rootDir.absolutePath}/publish/_publish.properties"
        ).normalize().also { file ->
            if (file.exists()) {
                file.forEachLine {
                    val pair = it.split("=")
                    if (pair.size == 2) {
                        map[pair[0]] = pair[1]
                    }
                }
            }
        }
    }

    val repoUsername = (properties["repo.username"]
        ?: System.getenv("KLUTTER_PRIVATE_USERNAME"))
        ?: throw GradleException("missing repo.username")

    val repoPassword = (properties["repo.password"]
        ?: System.getenv("KLUTTER_PRIVATE_PASSWORD"))
        ?: throw GradleException("missing repo.password")

    val repoEndpoint = (properties["repo.url"]
        ?: System.getenv("KLUTTER_PRIVATE_URL"))
        ?: throw GradleException("missing repo.url")

    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
        maven {
            url = uri(repoEndpoint)
            credentials {
                username = repoUsername
                password = repoPassword
            }
        }
    }
}

tasks.withType<org.jetbrains.dokka.gradle.DokkaTask>().configureEach {
    dokkaSourceSets {

        named("main") {
            moduleName.set("Klutter: Core")
            includes.from("core/module.md")
            sourceLink {
                remoteUrl.set(uri("https://github.com/buijs-dev/klutter/tree/main/packages/core/src/main/kotlin").toURL())
                localDirectory.set(file("core/src/main/kotlin"))
                remoteLineSuffix.set("#L")
            }
        }

        named("main") {
            moduleName.set("Klutter: Annotations for JVM")
            includes.from("annotations-jvm/module.md")
            sourceLink {
                remoteUrl.set(uri("https://github.com/buijs-dev/klutter/tree/main/packages/annotations-jvm/src/main/kotlin").toURL())
                localDirectory.set(file("annotations-jvm/src/main/kotlin"))
                remoteLineSuffix.set("#L")
            }
        }

    }
}

tasks.dokkaHtmlMultiModule.configure {
    outputDirectory.set(buildDir.resolve("dokkaSite"))
}

tasks.koverMergedXmlReport {
    isEnabled = true

    excludes = listOf(
        //a test-only module
        "dev.buijs.klutter.core.test.*",
    )

    xmlReportFile.set(layout.buildDirectory.file("koverage.xml"))
}

kover {

    // KOVER destroys running with coverage from IDE
    isDisabled = hasProperty("nokover")

    coverageEngine.set(kotlinx.kover.api.CoverageEngine.JACOCO)

    jacocoEngineVersion.set("0.8.8")

    disabledProjects = setOf(
        // contains only annotations
        // and breaks Jacoco due to duplicate classes
        ":packages:annotations-kmp",

        // contains only annotations
        ":packages:annotations-jvm",

        // a test-only module
        ":packages:core-test"
    )
}