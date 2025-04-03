plugins {
    alias(libs.plugins.kotlin)
    alias(libs.plugins.kotlin.spring)
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(libs.spring.boot.starter)
    implementation(libs.spring.boot.starter.data.jdbc)
    implementation(libs.hikariCP)

    testImplementation(project(":ticket-lifecycle"))
    testImplementation(project(":customer-mgmt"))
    testImplementation(project(":prioritization"))
    testImplementation(project(":assignment"))
    testImplementation(project(":agent-center"))

    testImplementation(libs.spring.boot.starter.web)
    testImplementation(libs.spring.boot.starter.data.jdbc)
    testImplementation(libs.spring.ai.core)
    testImplementation(libs.spring.ai.openai)
    testImplementation(libs.postgresql)

    testImplementation(libs.spring.boot.starter.test)
    testImplementation(libs.testcontainers)
    testImplementation(libs.testcontainers.postgresql)
    testImplementation(libs.mockk)
    testImplementation(libs.spring.mockk)
    testImplementation(libs.kotest.assertions)
    testImplementation(libs.rest.assured.all)
    testImplementation(libs.rest.assured)
}

testing {
    suites {
        val test by getting(JvmTestSuite::class) {
            useJUnitJupiter("5.10.3")
        }
    }
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}
