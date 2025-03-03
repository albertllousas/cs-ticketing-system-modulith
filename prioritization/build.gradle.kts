plugins {
    alias(libs.plugins.kotlin)
    alias(libs.plugins.kotlin.spring)
}

repositories {
    mavenCentral()
    maven {
        url = uri("https://repo.spring.io/snapshot")
    }
}

dependencies {
    implementation(libs.spring.boot.starter)
    implementation(libs.spring.boot.starter.web)
    implementation(libs.spring.boot.starter.data.jdbc)
    implementation(libs.spring.ai.core)
    implementation(libs.spring.ai.openai)
    implementation(libs.postgresql)
    implementation(libs.arrow)
    implementation(libs.flyway.postgresql)
    implementation(libs.flyway)
    implementation(libs.jackson.module.kotlin)
    implementation(project(":shared-kernel"))

    testImplementation(project(":shared-testing"))
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
