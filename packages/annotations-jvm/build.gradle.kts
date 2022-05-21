plugins {
    id("maven-publish")
    id("java-library")
    kotlin("jvm")
}

java {
    withJavadocJar()
    withSourcesJar()
}

sourceSets {
    main {
        java {
            srcDirs("${projectDir.absolutePath}/src/main/kotlin")
        }
    }
}

publishing {

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

    val libversion = (properties["annotations.version"] ?: "0.10.0")
        .also { println("VERSION ANNOTATIONS ==> $it") }

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
        maven {
            url = uri(repoEndpoint)
            credentials {
                username = repoUsername
                password = repoPassword
            }
        }
    }

    publications {
        create<MavenPublication>("maven") {

            groupId = "dev.buijs.klutter"
            artifactId = "annotations-jvm"
            version = libversion

            artifact("$projectDir/build/libs/annotations-jvm.jar")

            pom {
                name.set("Klutter: Annotations - JVM ")
                description.set("Klutter Framework annotations used in Java components")
                url.set("https://buijs.dev/klutter/")

                licenses {
                    license {
                        name.set("MIT License")
                        url.set("https://github.com/buijs-dev/klutter/blob/main/LICENSE")
                    }
                }

                developers {
                    developer {
                        id.set("buijs-dev")
                        name.set("Gillian Buijs")
                        email.set("info@buijs.dev")
                    }
                }

                scm {
                    connection.set("git@github.com:buijs-dev/klutter.git")
                    developerConnection.set("git@github.com:buijs-dev/klutter.git")
                    url.set("https://github.com/buijs-dev/klutter")
                }
            }
        }
    }
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}