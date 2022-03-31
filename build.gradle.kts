tasks.register("_switch_develop") {
    file("publish/_publish.properties")
        .writeText(file("publish/_develop.properties").readText())
}

tasks.register("_switch_release") {
    file("publish/_publish.properties")
        .writeText(file("publish/_release.properties").readText())
}

tasks.register("_publish_release_all") {

    dependsOn(tasks["_switch_release"])

    doLast {
        project.exec {
            commandLine("bash", "./publish/publish_all_release.sh")
        }
    }
}

tasks.register("_publish_develop") {

    dependsOn(tasks["_switch_develop"])

    doLast {
        project.exec {
            commandLine("bash", "./publish/publish_develop.sh")
        }
    }
}

tasks.register("_publish_release") {

    dependsOn(tasks["_switch_release"])

    doLast {
        project.exec {
            commandLine("bash", "./publish/publish_release.sh")
        }
    }
}

tasks.register("_publish_annotations_develop") {

    dependsOn(tasks["_switch_develop"])

    doLast {
        project.exec {
            commandLine("bash", "./publish/publish_annotations.sh")
        }
    }
}

tasks.register("_publish_annotations_release") {

    dependsOn(tasks["_switch_release"])

    doLast {
        project.exec {
            commandLine("bash", "./publish/publish_annotations.sh")
        }
    }
}

tasks.register("_publish_core_develop") {

    dependsOn(tasks["_switch_develop"])

    doLast {
        project.exec {
            commandLine("bash", "./publish/publish_core.sh")
        }
    }
}

tasks.register("_publish_core_release") {

    dependsOn(tasks["_switch_release"])

    doLast {
        project.exec {
            commandLine("bash", "./publish/publish_core.sh")
        }
    }
}