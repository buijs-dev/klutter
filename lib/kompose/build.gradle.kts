plugins {
    id("klutter")
    id("klutter-java")
    id("maven-publish")
    id("klutter-test")
}

sourceSets {
    main {
        java {
            srcDirs("${projectDir.absolutePath}/src/main/kotlin")
        }
    }

    test {
        java {
            srcDirs("${projectDir.absolutePath}/src/test/kotlin")
        }
    }
}

dependenciesTest {
    implementation.forEach {
        dependencies.add("testImplementation", it)
    }
}

dependenciesJava {
    implementation.forEach {
        dependencies.add("implementation", it)
    }
}

dependencies {
    implementation(project(":lib:core"))
    implementation(project(":lib:annotations"))
    testImplementation(project(":lib:test"))
}

publishing {

    repositories {
        maven {
            url = dev.buijs.klutter.Repository.endpoint
            credentials {
                username =  dev.buijs.klutter.Repository.username
                password =  dev.buijs.klutter.Repository.password
            }
        }
    }

    publications {
        create<MavenPublication>("maven") {
            groupId = "dev.buijs.klutter"
            artifactId = "kompose"
            version = dev.buijs.klutter.ProjectVersions.core
            artifact("$projectDir/build/libs/kompose.jar")

            pom {
                name.set("Klutter: Kompose")
                description.set("Klutter Framework kompose module to build Flutter UI in Kotlin language")
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