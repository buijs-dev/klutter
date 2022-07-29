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
    implementation("io.appium:java-client:8.1.1")
    implementation("org.seleniumhq.selenium:selenium-support:4.3.0")
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
            artifactId = "testkit"
            version = dev.buijs.klutter.ProjectVersions.kitty
            artifact("$projectDir/build/libs/testkit.jar")

            pom {
                name.set("Klutter: Kitty")
                description.set("Klutter Framework integration-test module")
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