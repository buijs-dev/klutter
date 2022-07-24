plugins {
    kotlin("jvm") version "1.7.10"
    id("klutter")
    id("klutter-java")
    id("klutter-test")
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
    implementation(gradleTestKit())
}