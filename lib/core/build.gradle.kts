plugins {
    kotlin("jvm") version "1.7.10"
    id("klutter")
    id("klutter-java")
    id("klutter-test")
    id("maven-publish")
}

sourceSets {
    main {
        java {
            srcDirs("${projectDir.absolutePath}/src/main/kotlin")
        }
    }

    test {
        java {
            srcDirs(
                "${projectDir.absolutePath}/src/test/kotlin",
                "${projectDir.absolutePath}/src/test/groovy"
                )
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
    implementation(gradleApi())
    testImplementation(gradleTestKit())
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
            artifactId = "core"
            version = dev.buijs.klutter.ProjectVersions.core
            artifact("$projectDir/build/libs/core.jar")

            pom {
                name.set("Klutter: Core")
                description.set("Klutter Framework core module")
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