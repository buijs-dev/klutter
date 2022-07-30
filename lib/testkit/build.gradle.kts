plugins {
    kotlin("jvm") version "1.7.10"
    id("klutter")
    id("groovy")
    id("java-library")
    id("maven-publish")
}

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
            srcDirs(
                "${projectDir.absolutePath}/src/test/kotlin",
                "${projectDir.absolutePath}/src/test/groovy"
                )
        }

    }
}

dependencies {
    implementation("io.appium:java-client:8.1.1")
    implementation("org.seleniumhq.selenium:selenium-support:4.3.0")

    //Kotlin
    implementation("org.jetbrains.kotlin:kotlin-reflect:1.7.10")
    implementation("org.jetbrains.kotlin:kotlin-compiler:1.7.10")

    //Jackson for XML
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.13.2")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-xml:2.13.2")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.13.2")

    //Logging
    implementation("org.slf4j:slf4j-api:2.0.0-alpha7")
    implementation("io.github.microutils:kotlin-logging:2.1.23")

    // Spock
    testImplementation("org.codehaus.groovy:groovy-all:3.0.9")
    testImplementation("org.spockframework:spock-core:2.2-M1-groovy-3.0")

    // Kotlin Test
    @Suppress("GradleDependency") // 30-07-2022 newest 3.4.2 throws exceptions
    testImplementation("io.kotlintest:kotlintest-runner-junit5:3.3.0")
    
    testImplementation(project(":lib:test"))
    testImplementation(gradleTestKit())

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

tasks.named<Test>("test") {
    useJUnitPlatform()
}