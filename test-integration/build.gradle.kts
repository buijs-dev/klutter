plugins {
    kotlin("jvm")
    id("klutter")
    id("groovy")
}

buildscript {
    repositories {
        mavenCentral()
    }

    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.7.10")
    }
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation(project(":lib:kore"))
    implementation(project(":lib:tasks"))

    // T-t-t-t-testing !
    testImplementation(project(":lib-test"))
    testImplementation("org.spockframework:spock-core")
}

tasks.withType<Test> {
    useJUnitPlatform()
}