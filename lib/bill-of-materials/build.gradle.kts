plugins {
    id("java-platform")
    id("maven-publish")
    id("klutter")
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
        create<MavenPublication>("maven") {
            groupId = "dev.buijs.klutter"
            artifactId = "bom"
            version = dev.buijs.klutter.ProjectVersions.bom
            afterEvaluate { from(components["javaPlatform"]) }
            pom {
                name.set("Klutter: Bill of Materials")
                description.set("Collection of compatible Klutter dependencies.")
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

// List of modules to include in the bom artifact.
val includeModules = listOf(
    "annotations",
    "compiler",
    "flutter-engine",
    "gradle",
    "kompose",
    "kore",
    "tasks"
)
dependencies {
    constraints {
        project.rootProject.subprojects.forEach { subproject ->
            if (subproject.name in includeModules) {
                api(subproject)
            }
        }
    }
}