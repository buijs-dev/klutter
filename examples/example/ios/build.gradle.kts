tasks.register("podupdate") {
    doLast {
        exec {
            commandLine("bash", "./Klutter/podupdate.sh")
        }
    }
}