import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.plugin.mpp.apple.XCFramework

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidKotlinMultiplatformLibrary)
    alias(libs.plugins.dokkatoo)
    alias(libs.plugins.mavenPublish)
    alias(libs.plugins.mokkery)
}

kotlin {
    android {
        namespace = "com.khealth"
        compileSdk = libs.versions.compileSdk.get().toInt()
        minSdk = libs.versions.minSdk.get().toInt()

        withHostTest {}

        compilerOptions {
            jvmTarget.set(JvmTarget.fromTarget(libs.versions.java.get()))
        }
    }

    val xcf = XCFramework()
    listOf(
        iosArm64(),
        iosSimulatorArm64(),
        iosX64(),
        watchosArm64(),
        watchosDeviceArm64(),
        watchosSimulatorArm64(),
        watchosX64()
    ).forEach {
        it.binaries.framework {
            baseName = "shared"
            xcf.add(this)
            isStatic = true
        }
    }

    sourceSets {
        androidMain.dependencies {
            implementation(libs.healthConnectClient)
        }
        commonMain.dependencies {
            implementation(libs.coroutines.core)
            implementation(libs.kermit)
            implementation(libs.kotlin.dateTime)
        }
        commonTest.dependencies {
            implementation(libs.coroutines.test)
            implementation(libs.kotlin.test)
        }
    }

    compilerOptions {
        freeCompilerArgs.add("-Xexpect-actual-classes")
    }
}

dokkatoo {
    moduleName.set("KHealth")
    pluginsConfiguration.html {
        customAssets.from("dokka_assets/logo-icon.svg")
        footerMessage.set("(c) 2024 Shubham Singh")
    }
}
