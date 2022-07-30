import org.jetbrains.dokka.DokkaConfiguration.*
import org.jetbrains.dokka.gradle.DokkaTask

plugins {
    kotlin("jvm") version "1.7.10"
    id("org.jetbrains.dokka") version "1.7.0"
    id("org.jetbrains.kotlinx.kover") version "0.5.1"
    id("org.sonarqube") version "3.4.0.2513"
    id("maven-publish")
    id("java-library")
    id("klutter")
}

java {
    withJavadocJar()
    withSourcesJar()
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

group = "dev.buijs.klutter"
version = dev.buijs.klutter.ProjectVersions.bom

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
        create<MavenPublication>("plugin") {
            groupId = "dev.buijs.klutter"
            artifactId = "klutter-bom-plugin"
            version = dev.buijs.klutter.ProjectVersions.bom
            artifact("$projectDir/build/libs/klutter-plugin.jar")

            pom {
                name.set("Klutter: BOM")
                description.set("Klutter - Bill of Materials (BOM): plugin")
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

            pom.withXml {
                val dependenciesNode = asNode().appendNode("dependencies")
                val dependenciesManagementNode = asNode()
                    .appendNode("dependencyManagement")
                    .appendNode("dependencies")
                configurations.implementation.get().allDependencies.forEach {
                    if (it.group != null) {
                        if (it.name.endsWith("-bom")) {
                            val dependencyNode = dependenciesManagementNode.appendNode("dependency")
                            dependencyNode.appendNode("groupId", it.group)
                            dependencyNode.appendNode("artifactId", it.name)
                            dependencyNode.appendNode("version", it.version)
                            dependencyNode.appendNode("scope", "import")
                            dependencyNode.appendNode("type", "pom")
                        } else {
                            val dependencyNode = dependenciesNode.appendNode("dependency")
                            dependencyNode.appendNode("groupId", it.group)
                            dependencyNode.appendNode("artifactId", it.name)
                            if (it.version != null) {
                                dependencyNode.appendNode("version", it.version)
                            }
                        }
                    }
                }
            }

        }

        create<MavenPublication>("application") {
            groupId = "dev.buijs.klutter"
            artifactId = "klutter-bom-application"
            version = dev.buijs.klutter.ProjectVersions.bom
            artifact("$projectDir/build/libs/klutter-application.jar")

            pom {
                name.set("Klutter: BOM")
                description.set("Klutter - Bill of Materials (BOM): application")
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

            pom.withXml {
                val dependenciesNode = asNode().appendNode("dependencies")
                val dependenciesManagementNode = asNode()
                    .appendNode("dependencyManagement")
                    .appendNode("dependencies")
                configurations.implementation.get().allDependencies.forEach {
                    if (it.group != null) {
                        if (it.name.endsWith("-bom")) {
                            val dependencyNode = dependenciesManagementNode.appendNode("dependency")
                            dependencyNode.appendNode("groupId", it.group)
                            dependencyNode.appendNode("artifactId", it.name)
                            dependencyNode.appendNode("version", it.version)
                            dependencyNode.appendNode("scope", "import")
                            dependencyNode.appendNode("type", "pom")
                        } else {
                            val dependencyNode = dependenciesNode.appendNode("dependency")
                            dependencyNode.appendNode("groupId", it.group)
                            dependencyNode.appendNode("artifactId", it.name)
                            if (it.version != null) {
                                dependencyNode.appendNode("version", it.version)
                            }
                        }
                    }
                }
            }
        }
    }
}

subprojects {
    plugins.apply("org.jetbrains.dokka")
}

kover {

    // KOVER destroys running with coverage from IDE
    isDisabled = hasProperty("nokover")

    coverageEngine.set(kotlinx.kover.api.CoverageEngine.JACOCO)

    jacocoEngineVersion.set("0.8.8")

    disabledProjects = setOf(
        // contains only annotations
        // and breaks Jacoco due to duplicate classes
        ":lib:klutter-annotations",

        // breaks Jacoco due to duplicate classes, haven't found a fix yet...
        ":lib:klutter-kompose",

        // a test-only module
        ":lib-test",
    )
}

sonarqube {
    properties {
        property("sonar.projectKey", "buijs-dev_klutter")
        property("sonar.organization", "buijs-dev")
        property("sonar.host.url", "https://sonarcloud.io")
        property(
            "sonar.coverage.jacoco.xmlReportPaths",
            rootProject.buildDir.resolve("koverage.xml").absolutePath
        )
    }
}

tasks.withType<DokkaTask>().configureEach {
    dokkaSourceSets {
        configureEach {
            documentedVisibilities.set(
                setOf(
                    Visibility.PUBLIC,
                    Visibility.PRIVATE,
                    Visibility.PROTECTED,
                    Visibility.INTERNAL,
                )
            )
        }
    }
}

tasks.dokkaHtmlMultiModule.configure {
    outputDirectory.set(layout.buildDirectory.dir("dokkaSite").map { it.asFile })
}

tasks.koverMergedXmlReport {
    isEnabled = true

    excludes = listOf(
        // A test-only module
        "dev.buijs.klutter.core.test.*",
    )

    xmlReportFile.set(layout.buildDirectory.file("koverage.xml"))
}