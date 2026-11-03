import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.plugin.mpp.apple.XCFramework

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.kmp.library)
    alias(libs.plugins.detekt)
    `maven-publish`
}

kotlin {
    explicitApi()

    android {
        namespace = "com.mukeshsolanki.countries.geo"
        compileSdk = 37
        minSdk = 24
        compilerOptions.jvmTarget.set(JvmTarget.JVM_11)
        withHostTest {}
    }

    val xcFramework = XCFramework("KmpCountriesGeo")
    listOf(iosArm64(), iosSimulatorArm64()).forEach { target ->
        target.binaries.framework {
            baseName = "KmpCountriesGeo"
            isStatic = true
            export(dependencies.project(":kmp-countries"))
            xcFramework.add(this)
        }
    }

    sourceSets {
        commonMain.dependencies {
            api(project(":kmp-countries"))
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}
