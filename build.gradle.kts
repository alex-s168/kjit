import java.net.URI

plugins {
    kotlin("multiplatform") version "1.9.20-RC2"
}

group = "me.user"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven {
        url = URI("https://jitpack.io")
    }
}

kotlin {
    val hostOs = System.getProperty("os.name")
    val isArm64 = System.getProperty("os.arch") == "aarch64"
    val isMingwX64 = hostOs.startsWith("Windows")
    val nativeTarget = when {
        hostOs == "Mac OS X" && isArm64 -> macosArm64("native")
        hostOs == "Mac OS X" && !isArm64 -> macosX64("native")
        hostOs == "Linux" && isArm64 -> linuxArm64("native")
        hostOs == "Linux" && !isArm64 -> linuxX64("native")
        isMingwX64 -> mingwX64("native")
        else -> throw GradleException("Host OS is not supported in Kotlin/Native.")
    }

    nativeTarget.apply {
        compilations.all {
            cinterops {
                val ctricks by creating {
                    defFile(project.file("src/c/ctricks.def"))
                    packageName("me.alex_s168.kjit.tricks")
                    compilerOpts("-Isrc/c/")
                    includeDirs.allHeaders("src/c/")
                }
            }
        }
        binaries {
            executable {
                entryPoint = "me.alex_s168.kjit.main"
            }
        }
    }
    sourceSets {
        val nativeMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.0-RC2")
            }
        }
        val nativeTest by getting
    }
}
