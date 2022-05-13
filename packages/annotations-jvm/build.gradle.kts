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

    val libversion = extra.has("annotations.version").let {
        if (it) extra.get("annotations.version") as String else {
            throw GradleException("missing annotations.version in gradle.properties")
        }
    }

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