// Project modules
include(":lib:annotations")
include(":lib:bill-of-materials")
include(":lib:compiler")
include(":lib:gradle")
include(":lib:jetbrains")
include(":lib:kompose")
include(":lib:kommand")
include(":lib:kore")
include(":lib:tasks")
// Internal Testing library
include(":lib-test")
// Internal workload for testing annotations processor
include(":test-ksp")
include(":test-ksp:platform")
// Test generating a new project using the local modules
include(":test-integration")
// Internal build properties
includeBuild("lib-build")
