plugins {
    kotlin("jvm")
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
    main { java { srcDirs("${projectDir.absolutePath}/src/main/kotlin") } }
    test { java { srcDirs("${projectDir.absolutePath}/src/test/kotlin") } }
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
            artifactId = "tasks"
            version = dev.buijs.klutter.ProjectVersions.tasks
            artifact("$projectDir/build/libs/tasks-${dev.buijs.klutter.ProjectVersions.tasks}.jar")

            pom {
                name.set("Klutter: Tasks")
                description.set("Collection of Klutter tasks to be executed through Gradle, Flutter and/or Jetbrains IDE.")
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
    implementation(project(":lib:kore"))
    implementation(project(":lib:annotations"))

    // Jackson for XML
    implementation("com.fasterxml.jackson.core:jackson-databind:2.14.2")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.14.2")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-xml:2.14.2")

    // Logging
    implementation("org.slf4j:slf4j-api:2.0.7")
    implementation("io.github.microutils:kotlin-logging:3.0.5")

    testImplementation(project(":lib-test"))
}

tasks.named<Test>("test") {
    useJUnitPlatform()
}