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
    val file = File("${rootDir.absolutePath}/../publish/_publish.properties").normalize()

    if(!file.exists()) {
        throw GradleException("missing _publish.properties file in ${file.absolutePath}")
    }

    val properties = HashMap<String, String>()

    file.forEachLine {
        val pair = it.split("=")
        if(pair.size == 2){
            properties[pair[0]] = pair[1]
        }
    }

    val user = properties["repo.username"]
        ?:throw GradleException("missing repo.username in .properties")

    val pass = properties["repo.password"]
        ?:throw GradleException("missing repo.password in .properties")

    val endpoint = properties["repo.url"]
        ?:throw GradleException("missing repo.url in .properties")

    repositories {
        maven {
            credentials {
                username = user
                password = pass
            }

            url = uri(endpoint)

        }
    }

    publications {
        create<MavenPublication>("maven") {

            groupId = "dev.buijs.klutter"
            artifactId = "annotations-jvm"
            version = properties["annotations.version"]

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