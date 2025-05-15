enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
        maven { url = uri("https://s01.oss.sonatype.org/content/repositories/snapshots/") }
        mavenCentral()
    }
}

dependencyResolutionManagement {
    @Suppress("UnstableApiUsage")
    repositories {
        google()
        maven { url = uri("https://s01.oss.sonatype.org/content/repositories/snapshots/") }
        mavenCentral()
    }
}

rootProject.name = "KotlinHealth"
include(":khealth")
include(":sampleAndroidApp")
include(":sampleShared")
include(":samples:CMPKoinSample:composeApp")
