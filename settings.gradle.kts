plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}

rootProject.name = "cs-ticketing-system-modulith"
include(
    "ticket-lifecycle",
    "prioritization",
    "assignment",
    "agent-center",
    "customer-mgmt",
    "shared-kernel",
    "shared-testing"
)
