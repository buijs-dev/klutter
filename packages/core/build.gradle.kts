plugins {
    id("groovy")
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

dependencies {

    implementation("org.jetbrains.kotlin:kotlin-reflect:1.6.21")
    implementation("org.jetbrains.kotlin:kotlin-compiler:1.6.21")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.13.2")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-xml:2.13.2")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.13.2")

    //JUnit
    testImplementation(platform("org.junit:junit-bom:5.8.2"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("io.kotlintest:kotlintest-runner-junit5:3.1.10")

    //Spock
    testImplementation("org.codehaus.groovy:groovy-all:3.0.9")
    testImplementation("org.spockframework:spock-core:2.2-M1-groovy-3.0")

    //Gradle
    implementation(gradleApi())
    testImplementation(gradleTestKit())

    //Mock
    testImplementation("org.mockito:mockito-core:4.2.0")
    testImplementation("org.mockito.kotlin:mockito-kotlin:4.0.0")

    testImplementation(project(":packages:core-test"))
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}


publishing {

    val properties = HashMap<String, String>().also { map ->
        File("${rootDir.absolutePath}/publish/_publish.properties"
        ).normalize().also { file ->
            if (file.exists()) {
                file.forEachLine {
                    val pair = it.split("=")
                    if (pair.size == 2) {
                        map[pair[0]] = pair[1]
                    }
                }
            }
        }
    }

    val libversion = (properties["core.version"] ?: "0.12.4")
        .also { println("VERSION CORE ==> $it") }

    val repoUsername = (properties["repo.username"]
        ?: System.getenv("KLUTTER_PRIVATE_USERNAME"))
        ?: throw GradleException("missing repo.username")

    val repoPassword = (properties["repo.password"]
        ?: System.getenv("KLUTTER_PRIVATE_PASSWORD"))
        ?: throw GradleException("missing repo.password")

    val repoEndpoint = (properties["repo.url"]
        ?: System.getenv("KLUTTER_PRIVATE_URL"))
        ?: throw GradleException("missing repo.url")

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
            artifactId = "core"
            version = libversion
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

tasks.named<Test>("test") {
    useJUnitPlatform()
}