plugins {
    kotlin("jvm")
    id("klutter")
    id("groovy")
    id("maven-publish")
    id("java-library")
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

    compileOnly(project(":lib:kore"))

    // KSP for annotation scanning
    implementation(kotlin("stdlib"))
    implementation("com.google.devtools.ksp:symbol-processing-api:1.8.20-1.0.11")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.5.0")

    // Jackson for XML and YAML
    implementation("com.fasterxml.jackson.core:jackson-databind:2.14.2")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.14.2")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-xml:2.14.2")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.14.2")

    // Logging
    implementation("org.slf4j:slf4j-api:2.0.7")
    implementation("io.github.microutils:kotlin-logging:3.0.5")

    // T-t-t-t-testing !
    testImplementation(project(":lib:kore"))
    testImplementation(project(":lib-test"))
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
            artifactId = "compiler"
            version = dev.buijs.klutter.ProjectVersions.compiler
            artifact("$projectDir/build/libs/compiler.jar")

            pom {
                name.set("Klutter: Kore")
                description.set("Klutter Framework Compiler Plugin")
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

                withXml {
                    val dependencies = asNode().appendNode("dependencies")
                    configurations.implementation.get().allDependencies.forEach { dep ->
                        dependencies.addDependency(dep)
                    }
                }
            }
        }
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

fun groovy.util.Node.addDependency(dep: Dependency) {
    appendNode("dependency").let {
        it.appendNode("groupId", dep.getLocalOrProjectGroup())
        it.appendNode("artifactId", dep.name)
        it.appendNode("version", dep.getLocalOrProjectVersion())
    }
}

fun Dependency.getLocalOrProjectGroup() = when {
    group == null ->
        throw GradleException("Unable to publish compiler plugin because a dependency is missing the groupId")

    group!!.contains("klutter") ->
        "dev.buijs.klutter"

    else -> group
}

fun Dependency.getLocalOrProjectVersion() = when(name) {
    "kore" ->
        dev.buijs.klutter.ProjectVersions.kore
    "kotlin-stdlib" ->
        "1.7.10"
    else -> version
}