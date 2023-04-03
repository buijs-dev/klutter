// Project modules
include(":lib:annotations")
include(":lib:compiler")
include(":lib:gradle")
include(":lib:jetbrains")
include(":lib:kompose")
include(":lib:kore")
include(":lib:tasks")
// Internal Testing library
include(":lib-test")
// Internal workload for testing annotations processor
include(":test-ksp")
include(":test-ksp:platform")
// Internal build properties
includeBuild("lib-build")
