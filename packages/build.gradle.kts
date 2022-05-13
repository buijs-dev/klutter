plugins {
    kotlin("jvm") version "1.6.10"
    id("org.jetbrains.dokka") version "1.6.10"
    id("org.jetbrains.kotlinx.kover") version "0.5.0"
}

subprojects {
    plugins.apply("org.jetbrains.dokka")
}

buildscript {

    val user = extra.has("repo.username").let {
        if (it) extra.get("repo.username") as String else {
            throw GradleException("missing repo.username in gradle.properties")
        }
    }

    val pass = extra.has("repo.password").let {
        if (it) extra.get("repo.password") as String else {
            throw GradleException("missing repo.password in gradle.properties")
        }
    }

    val endpoint = extra.has("repo.url").let {
        if (it) extra.get("repo.url") as String else {
            throw GradleException("missing repo.url in gradle.properties")
        }
    }

    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
        maven {
            url = uri(endpoint)
            credentials {
                username = user
                password = pass
            }
        }
    }

    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.6.10")
        classpath("com.android.tools.build:gradle:7.0.4")
    }
}

allprojects {

    val user = extra.has("repo.username").let {
        if (it) extra.get("repo.username") as String else {
            throw GradleException("missing repo.username in gradle.properties")
        }
    }

    val pass = extra.has("repo.password").let {
        if (it) extra.get("repo.password") as String else {
            throw GradleException("missing repo.password in gradle.properties")
        }
    }

    val endpoint = extra.has("repo.url").let {
        if (it) extra.get("repo.url") as String else {
            throw GradleException("missing repo.url in gradle.properties")
        }
    }

    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
        maven {
            url = uri(endpoint)
            credentials {
                username = user
                password = pass
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
                //TODO fix uri
                remoteUrl.set(uri("https://github.com/buijs-dev/klutter/tree/main/klutter-core/core/src/main/kotlin").toURL())
                localDirectory.set(file("core/src/main/kotlin"))
                remoteLineSuffix.set("#L")
            }
        }

        named("main") {
            moduleName.set("Klutter: Annotations for JVM")
            includes.from("annotations-jvm/module.md")
            sourceLink {
                //TODO fix uri
                remoteUrl.set(uri("https://github.com/buijs-dev/klutter/tree/main/klutter-core/core/src/main/kotlin").toURL())
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
}

tasks.register("_publish_release") {
    dependsOn(tasks["_publish_release_annotations"])
    dependsOn(tasks["_publish_release_core"])
}

tasks.register("_publish_develop_annotations") {

    dependsOn(tasks["_switch_develop"])

    doLast {
        project.exec {
            commandLine("bash", "./../publish/publish_annotations.sh")
        }
    }
}

tasks.register("_publish_release_annotations") {

    dependsOn(tasks["_switch_release"])

    doLast {
        project.exec {
            commandLine("bash", "./../publish/publish_annotations.sh")
        }
    }
}

tasks.register("_publish_develop_core") {

    dependsOn(tasks["_switch_develop"])

    doLast {
        project.exec {
            commandLine("bash", "./../publish/publish_core.sh")
        }
    }
}

tasks.register("_publish_release_core") {

    dependsOn(tasks["_switch_release"])

    doLast {
        project.exec {
            commandLine("bash", "./../publish/publish_release.sh")
        }
    }
}

tasks.register("_switch_develop") {
    file("gradle.properties").writeText(file("../publish/_develop.properties").readText())
}

tasks.register("_switch_release") {
    file("gradle.properties").writeText(file("../publish/_release.properties").readText())
}