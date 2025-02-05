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
    implementation(libs.postgresql)
    implementation(libs.flyway)
    implementation(libs.testcontainers)
    implementation(libs.testcontainers.postgresql)
    implementation(libs.testcontainers.ollama)
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
