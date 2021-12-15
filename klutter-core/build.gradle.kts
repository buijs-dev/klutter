plugins {
    id("maven-publish")
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

            artifact("$projectDir/lib/build/libs/lib.jar")

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

tasks.register("buildAndPublish") {
    doLast {
        project.exec {
            commandLine("bash", "./publish.sh")
        }
    }
}