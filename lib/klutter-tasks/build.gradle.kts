plugins {
    kotlin("jvm") version "1.7.10"
    id("java-library")
    id("maven-publish")
    id("groovy")
    id("klutter")
}

group = "dev.buijs.klutter"
version = dev.buijs.klutter.ProjectVersions.tasks

java {
    withJavadocJar()
    withSourcesJar()
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
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

sonarqube {
    isSkipProject = true
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
            artifactId = "klutter-gradle"
            version = dev.buijs.klutter.ProjectVersions.gradle
            artifact("$projectDir/build/libs/klutter-gradle-${dev.buijs.klutter.ProjectVersions.gradle}.jar")

            pom {
                name.set("Klutter: Gradle Plugin")
                description.set("Gradle plugin for the Klutter Framework")
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

dependencies {
    // Project
    implementation(project(":lib:klutter-kore"))
    implementation(project(":lib:klutter-annotations"))

    // Logging
    implementation("org.slf4j:slf4j-api:2.0.0-alpha7")
    implementation("io.github.microutils:kotlin-logging:2.1.23")

    testImplementation(project(":lib-test"))
}

tasks.named<Test>("test") {
    useJUnitPlatform()
}