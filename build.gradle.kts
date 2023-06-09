plugins {
    kotlin("multiplatform") version "1.8.20"
    kotlin("plugin.serialization") version "1.8.20"
    id("io.kotest.multiplatform") version "5.6.1"
}

group = "moe.micha"
version = "0.0.0"

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
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
                implementation("io.ktor:ktor-client-core:2.3.0")
                implementation("io.ktor:ktor-client-content-negotiation:2.3.0")
                implementation("io.ktor:ktor-serialization-kotlinx-json:2.3.0")
            }
        }

        getByName("jvmMain") {
            dependencies {
                implementation("io.ktor:ktor-client-java:2.3.0")
            }
        }

        val nativeMain = create("nativeMain") { dependsOn(commonMain) }
        val cioBasedMain = create("cioBasedMain") {
            dependsOn(nativeMain)

            dependencies {
                implementation("io.ktor:ktor-client-cio:2.3.0")
            }
        }
        getByName("linuxX64Main") { dependsOn(cioBasedMain) }
        getByName("macosX64Main") { dependsOn(cioBasedMain) }
        getByName("macosArm64Main") { dependsOn(cioBasedMain) }
        getByName("windowsX64Main") {
            dependsOn(nativeMain)

            dependencies {
                implementation("io.ktor:ktor-client-winhttp:2.3.0")
            }
        }

        val commonTest = getByName("commonTest") {
            dependencies {
                implementation("io.kotest:kotest-framework-engine:5.6.1")
                implementation("io.kotest:kotest-assertions-core:5.6.1")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.6.4")
                implementation("io.ktor:ktor-client-mock:2.3.0")
            }
        }

        getByName("jvmTest") {
            dependencies {
                implementation("io.kotest:kotest-runner-junit5:5.6.1")
            }
        }

        val nativeTest = create("nativeTest") { dependsOn(commonTest) }
        val cioBasedTest = create("cioBasedTest") { dependsOn(nativeTest) }
        getByName("linuxX64Test") { dependsOn(cioBasedTest) }
        getByName("macosX64Test") { dependsOn(cioBasedTest) }
        getByName("macosArm64Test") { dependsOn(cioBasedTest) }
        getByName("windowsX64Test") { dependsOn(nativeTest) }
    }
}
