plugins {
    id("maven-publish")
}

buildscript {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }

    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.6.0")
        classpath("com.android.tools.build:gradle:7.0.4")
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
    }
}

publishing {
    val file = File("${projectDir.absolutePath}/dev.properties").normalize()

    if(!file.exists()) {
        throw GradleException("missing dev.properties file in ${file.absolutePath}")
    }

    val properties = HashMap<String, String>()

    file.forEachLine {
        val pair = it.split("=")
        if(pair.size == 2){
            properties[pair[0]] = pair[1]
        }
    }

    val repsyUsername = properties["repsy.username"]
        ?:throw GradleException("missing repsy.username in dev.properties")

    val repsyPassword = properties["repsy.password"]
        ?:throw GradleException("missing repsy.password in dev.properties")

    val repsyUrl = properties["repsy.url"]
        ?:throw GradleException("missing repsy.url in dev.properties")

    val appGroupId = properties["app.group.id"]
        ?:throw GradleException("missing groupId in dev.properties")

    val appArtifactId = properties["app.artifact.id"]
        ?:throw GradleException("missing artifactId in dev.properties")

    val appVersion = properties["app.version"]
        ?:throw GradleException("missing version in dev.properties")

    publications {
        create<MavenPublication>("maven") {

            groupId = appGroupId
            artifactId = appArtifactId
            version = appVersion

            artifact("$projectDir/adapter/build/outputs/aar/${appArtifactId}-release.aar")
            artifact("$projectDir/adapter/build/libs/${appArtifactId}-$appVersion.jar")
        }
    }

    repositories {
        maven {
            url = uri(repsyUrl)
            credentials {
                username = repsyUsername
                password = repsyPassword
            }
        }
    }
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}

tasks.register("buildAndPublish") {
    doLast {
        project.exec {
            commandLine("bash", "./publish.sh")
        }
    }
}