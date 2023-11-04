import java.net.URL
import org.jetbrains.dokka.gradle.DokkaTask

plugins {
    kotlin("multiplatform") version "1.9.10"
    kotlin("plugin.serialization") version "1.9.10"
    id("io.kotest.multiplatform") version "5.6.1"
    id("org.jetbrains.dokka") version "1.9.10"

    id("maven-publish")
    id("signing")
}

group = "moe.micha"
description = "Kotlin/Multiplatform client library for the popular DeepL Translator"
version = "0.1.0-SNAPSHOT"

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

val sonatypeUsername: String? = System.getenv("SONATYPE_USERNAME")
val sonatypePassword: String? = System.getenv("SONATYPE_PASSWORD")
val gpgPrivateKey: String? = System.getenv("GPG_PRIVATE_KEY")
val gpgKeyPassword: String? = System.getenv("GPG_KEY_PASSWORD")

val repositoryBranch: String? = System.getenv("GITHUB_REF_NAME")

val repositoryUrl: String? = run {
    val githubServerUrl: String? = System.getenv("GITHUB_SERVER_URL")
    val githubRepository: String? = System.getenv("GITHUB_REPOSITORY")

    if (githubServerUrl == null || githubRepository == null) {
        null
    } else {
        "$githubServerUrl/$githubRepository"
    }
}

val dokkaOutputDir = layout.buildDirectory.get().dir("dokka").dir("html")

tasks.withType<DokkaTask>().configureEach {
    outputDirectory = dokkaOutputDir

    dokkaSourceSets.configureEach {
        sourceLink {
            localDirectory = projectDir.resolve("src")
            remoteUrl = URL("$repositoryUrl/tree/$repositoryBranch/src")
            remoteLineSuffix = "#L"
        }
    }
}

val javadocJar = tasks.register<Jar>("javadocJar") {
    dependsOn(tasks.dokkaHtml)

    archiveClassifier = "javadoc"

    from(dokkaOutputDir)
}

tasks.withType<AbstractPublishToMaven> {
    dependsOn(tasks.withType<Sign>())
}

publishing {
    repositories {
        maven {
            name = "oss"
            val releasesRepoUrl = uri("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/")
            val snapshotsRepoUrl = uri("https://s01.oss.sonatype.org/content/repositories/snapshots/")

            url = if (version.toString().endsWith("SNAPSHOT")) snapshotsRepoUrl else releasesRepoUrl

            credentials {
                username = sonatypeUsername
                password = sonatypePassword
            }
        }
    }

    publications.withType<MavenPublication> {
        artifact(javadocJar)

        pom {
            name = project.name
            description = project.description
            url = repositoryUrl

            licenses {
                license {
                    name = "MIT"
                    url = "$repositoryUrl/blob/$repositoryBranch/license.md"
                }
            }

            issueManagement {
                system = "GitHub"
                url = "$repositoryUrl/issues"
            }

            scm {
                connection = "$repositoryUrl.git"
                url = repositoryUrl
            }

            developers {
                developer {
                    name = "Micha Lehmann"
                    email = "michalehmann0112@gmail.com"
                }
            }
        }
    }
}

signing {
    useInMemoryPgpKeys(
        gpgPrivateKey,
        gpgKeyPassword,
    )

    sign(publishing.publications)
}
