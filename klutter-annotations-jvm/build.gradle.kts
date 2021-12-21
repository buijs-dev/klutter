plugins {
    id("maven-publish")
    id("java-library")
    kotlin("jvm") version "1.6.0"
}

buildscript {
    val file = File("${rootDir.absolutePath}/dev.properties").normalize()

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

    val user = properties["private.repo.username"]
        ?:throw GradleException("missing private.repo.username in dev.properties")

    val pass = properties["private.repo.password"]
        ?:throw GradleException("missing private.repo.password in dev.properties")

    val endpoint = properties["private.repo.url"]
        ?:throw GradleException("missing private.repo.url in dev.properties")

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
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.6.0")
    }
}

sourceSets {
    main {
        java {
            srcDirs("${projectDir.absolutePath}/src/main/kotlin")
        }
    }
}

publishing {
    val file = File("${rootDir.absolutePath}/dev.properties").normalize()

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

    val user = properties["private.repo.username"]
        ?:throw GradleException("missing private.repo.username in dev.properties")

    val pass = properties["private.repo.password"]
        ?:throw GradleException("missing private.repo.password in dev.properties")

    val endpoint = properties["private.repo.url"]
        ?:throw GradleException("missing private.repo.url in dev.properties")

    repositories {
        maven {
            url = uri(endpoint)
            credentials {
                username = user
                password = pass
            }
        }
    }

    publications {
        create<MavenPublication>("maven") {

            groupId = "dev.buijs.klutter"
            artifactId = "annotations-jvm"
            version = "0.2.12"

            artifact("$projectDir/build/libs/klutter-annotations-jvm.jar")

        }
    }

}

tasks.register("buildAndPublish") {
    doLast {
        project.exec {
            commandLine("bash", "./publish.sh")
        }
    }
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}