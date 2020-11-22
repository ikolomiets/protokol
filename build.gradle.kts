import java.util.*

plugins {
    kotlin("multiplatform") version "1.4.10"
    `maven-publish`
    id("com.jfrog.bintray") version "1.8.5"
}

group = "com.electrit"
version = "1.0.0"

repositories {
    mavenCentral()
}

kotlin {
    jvm {
        compilations.all {
            kotlinOptions.jvmTarget = "1.8"
        }
    }

    js(IR) {
        browser {
            testTask {
                useKarma {
                    useChromeHeadless()
                    webpackConfig.cssSupport.enabled = true
                }
            }
        }
        binaries.executable()
    }

    val hostOs = System.getProperty("os.name")
    val isMingwX64 = hostOs.startsWith("Windows")
    val nativeTarget = when {
        hostOs == "Mac OS X" -> macosX64("native")
        hostOs == "Linux" -> linuxX64("native")
        isMingwX64 -> mingwX64("native")
        else -> throw GradleException("Host OS is not supported in Kotlin/Native.")
    }

    sourceSets {
        val commonMain by getting
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
            }
        }
        val jvmMain by getting
        val jvmTest by getting {
            dependencies {
                implementation(kotlin("test-junit"))
            }
        }
        val jsMain by getting
        val jsTest by getting {
            dependencies {
                implementation(kotlin("test-js"))
            }
        }
        val nativeMain by getting
        val nativeTest by getting
    }

    configure(listOf(targets["metadata"], jvm(), js())) {
        mavenPublication {
            val targetPublication = this@mavenPublication

            println("mavenPublication: ${targetPublication.name}")

            tasks.withType<AbstractPublishToMaven>()
/* FIXME had to modify example from https://kotlinlang.org/docs/reference/mpp-publish-lib.html
                .matching { it.publication == targetPublication }
                .all { onlyIf { findProperty("isMainHost") == "true" } }
*/
        }
    }
}

bintray {
    user = project.findProperty("bintrayUser").toString()
    key = project.findProperty("bintrayKey").toString()

    publish = true

    setPublications("metadata", "jvm", "js")

    pkg.apply {
        repo = "maven"
        name = rootProject.name
        userOrg = "ikolomiets"
        githubRepo = "ikolomiets/protokol"
        vcsUrl = "https://github.com/ikolomiets/protokol.git"
        description = "Kotlin Multiplatform serialization library"
        setLabels("kotlin", "serialization", "MPP", "Protokol")
        setLicenses("MIT")
        websiteUrl = "https://github.com/ikolomiets/protokol"
        issueTrackerUrl = "https://github.com/ikolomiets/protokol/issues"

        version.apply {
            name = project.version.toString()
            desc = "https://github.com/ikolomiets/protokol"
            released = Date().toString()
            vcsTag = project.version.toString()
        }
    }
}