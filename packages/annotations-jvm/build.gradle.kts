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

    val libversion = extra.has("annotations.version").let {
        if (it) extra.get("annotations.version") as String else "0.10.0"
    }

    val repoUsername = extra.has("repo.username").let {
        if (it) extra.get("repo.username") as String else {
            System.getenv("KLUTTER_PRIVATE_USERNAME") ?:
            throw GradleException("missing repo.username in gradle.properties")
        }
    }

    val repoPassword = extra.has("repo.password").let {
        if (it) extra.get("repo.password") as String else {
            System.getenv("KLUTTER_PRIVATE_PASSWORD") ?:
            throw GradleException("missing repo.password in gradle.properties")
        }
    }

    val repoEndpoint = extra.has("repo.url").let {
        if (it) extra.get("repo.url") as String else {
            System.getenv("KLUTTER_PRIVATE_URL") ?:
            throw GradleException("missing repo.url in gradle.properties")
        }
    }

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