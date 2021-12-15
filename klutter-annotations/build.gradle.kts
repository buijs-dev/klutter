plugins {
    kotlin("multiplatform") version "1.6.0"
    id("maven-publish")
}

group = "dev.buijs.klutter"
version = "0.0.21"

kotlin {
   jvm()

    sourceSets {
        val commonMain by getting
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
            }
        }

        val jvmMain by getting
        val jvmTest by getting
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

    repositories {
        maven {
            url = uri(repsyUrl)
            credentials {
                username = repsyUsername
                password = repsyPassword
            }
        }
    }

    publishing {
        publications.withType<MavenPublication> {
            artifactId = "annotations"
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