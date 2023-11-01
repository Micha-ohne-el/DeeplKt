plugins {
    kotlin("multiplatform") version "1.9.10"
    kotlin("plugin.serialization") version "1.9.10"
    id("io.kotest.multiplatform") version "5.6.1"
}

group = "moe.micha"
version = "0.0.0"

object Versions {
    const val coroutines = "1.7.3"
    const val ktor = "2.3.5"
    const val kotest = "5.7.2"
}

repositories {
    mavenCentral()
}

kotlin {
    jvm {
        jvmToolchain(11)

        testRuns.all {
            executionTask.configure {
                useJUnitPlatform()
            }
        }
    }

    js(IR) {
        browser()
        nodejs()
    }

    linuxX64()
    macosX64()
    macosArm64()
    mingwX64("windowsX64")

    sourceSets {
        val commonMain = getByName("commonMain") {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:${Versions.coroutines}")
                implementation("io.ktor:ktor-client-core:${Versions.ktor}")
                implementation("io.ktor:ktor-client-content-negotiation:${Versions.ktor}")
                implementation("io.ktor:ktor-serialization-kotlinx-json:${Versions.ktor}")
            }
        }

        getByName("jvmMain") {
            dependencies {
                implementation("io.ktor:ktor-client-java:${Versions.ktor}")
            }
        }

        val nativeMain = create("nativeMain") { dependsOn(commonMain) }

        getByName("linuxX64Main") {
            dependsOn(nativeMain)

            dependencies {
                implementation("io.ktor:ktor-client-curl:${Versions.ktor}")
            }
        }

        getByName("macosX64Main") {
            dependsOn(nativeMain)

            dependencies {
                implementation("io.ktor:ktor-client-darwin:${Versions.ktor}")
            }
        }

        getByName("macosArm64Main") {
            dependsOn(nativeMain)

            dependencies {
                implementation("io.ktor:ktor-client-darwin:${Versions.ktor}")
            }
        }

        getByName("windowsX64Main") {
            dependsOn(nativeMain)

            dependencies {
                implementation("io.ktor:ktor-client-winhttp:${Versions.ktor}")
            }
        }

        val commonTest = getByName("commonTest") {
            dependencies {
                implementation("io.kotest:kotest-framework-engine:${Versions.kotest}")
                implementation("io.kotest:kotest-assertions-core:${Versions.kotest}")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:${Versions.coroutines}")
                implementation("io.ktor:ktor-client-mock:${Versions.ktor}")
            }
        }

        getByName("jvmTest") {
            dependencies {
                implementation("io.kotest:kotest-runner-junit5:${Versions.kotest}")
            }
        }

        val nativeTest = create("nativeTest") { dependsOn(commonTest) }
        getByName("linuxX64Test") { dependsOn(nativeTest) }
        getByName("macosX64Test") { dependsOn(nativeTest) }
        getByName("macosArm64Test") { dependsOn(nativeTest) }
        getByName("windowsX64Test") { dependsOn(nativeTest) }
    }
}
