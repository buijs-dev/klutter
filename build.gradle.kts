plugins {
    kotlin("jvm") version "1.6.10"
    id("org.jetbrains.dokka") version "1.6.10"
    id("org.jetbrains.kotlinx.kover") version "0.5.0"
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

    val prod = (System.getenv("KLUTTER_ENABLE_PRODUCTION") ?: "FALSE") == "TRUE"

    val properties = HashMap<String, String>().also { map ->
        File("${rootDir.absolutePath}/publish/" +
                "${if(prod) "_release" else "_develop"}.properties"
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
    xmlReportFile.set(layout.buildDirectory.file("koverage.xml"))
}

tasks.register("_publish_develop") {
    dependsOn(tasks["_publish_develop_annotations"])
    dependsOn(tasks["_publish_develop_core"])
    dependsOn(tasks["_publish_develop_gradle_plugin"])
}

tasks.register("_publish_release") {
    dependsOn(tasks["_publish_release_annotations"])
    dependsOn(tasks["_publish_release_core"])
    dependsOn(tasks["_publish_release_gradle_plugin"])
}

tasks.register("_publish_develop_annotations", Exec::class) {
    dependsOn(tasks["switchDevelopment"])
    commandLine("bash", "./publish/publish_annotations.sh")
}

tasks.register("_publish_release_annotations", Exec::class) {
    dependsOn(tasks["switchRelease"])
    commandLine("bash", "./publish/publish_annotations.sh")
}

tasks.register("_publish_develop_core", Exec::class) {
    dependsOn(tasks["switchDevelopment"])
    commandLine("bash", "./publish/publish_core.sh")
}

tasks.register("_publish_release_core", Exec::class) {
    dependsOn(tasks["switchRelease"])
    commandLine("bash", "./publish/publish_release.sh")
}

tasks.register("_publish_develop_gradle_plugin", Exec::class) {
    dependsOn(tasks["switchDevelopment"])
    commandLine("bash", "./publish/publish_gradle_plugin.sh")
}

tasks.register("_publish_release_gradle_plugin", Exec::class) {
    dependsOn(tasks["switchRelease"])
    commandLine("bash", "./publish/publish_gradle_plugin_release.sh")
}

tasks.register("switchDevelopment", Exec::class) {
    environment("KLUTTER_ENABLE_PRODUCTION","FALSE")
    commandLine("echo", "DEVELOPMENT")
}

tasks.register("switchRelease", Exec::class) {
    environment("KLUTTER_ENABLE_PRODUCTION","TRUE")
    commandLine("echo", "PRODUCTION")
}