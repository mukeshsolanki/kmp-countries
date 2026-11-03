pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "kmp-countries-root"

include(":kmp-countries", ":kmp-countries-geo")
project(":kmp-countries").projectDir = file("countries")
project(":kmp-countries-geo").projectDir = file("countries-geo")
