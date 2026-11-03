import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinVersion

plugins {
    alias(libs.plugins.kotlin.multiplatform) apply false
    alias(libs.plugins.android.kmp.library) apply false
    alias(libs.plugins.detekt) apply false
}

subprojects {
    group = "com.mukeshsolanki"
    version = providers.gradleProperty("libVersion").getOrElse("0.1.0")

    pluginManager.withPlugin("org.jetbrains.kotlin.multiplatform") {
        configure<KotlinMultiplatformExtension> {
            coreLibrariesVersion = "2.0.21"
            compilerOptions {
                languageVersion.set(KotlinVersion.KOTLIN_2_0)
                apiVersion.set(KotlinVersion.KOTLIN_2_0)
            }
        }
    }

    pluginManager.withPlugin("maven-publish") {
        configure<PublishingExtension> {
            publications.withType<MavenPublication>().configureEach {
                pom.licenses {
                    license {
                        name.set("MIT")
                        url.set("https://opensource.org/licenses/MIT")
                        comments.set("Code")
                    }
                    license {
                        name.set("ODbL-1.0")
                        url.set("https://opendatacommons.org/licenses/odbl/1-0/")
                        comments.set("Bundled data")
                    }
                }
            }
            providers.gradleProperty("releaseRepoDir").orNull?.let { dir ->
                repositories.maven {
                    name = "Release"
                    url = uri(dir)
                }
            }
        }
    }

    pluginManager.withPlugin("dev.detekt") {
        configure<dev.detekt.gradle.extensions.DetektExtension> {
            buildUponDefaultConfig.set(true)
            source.setFrom("src/commonMain/kotlin", "src/commonTest/kotlin")
        }
        tasks.withType<dev.detekt.gradle.Detekt>().configureEach {
            exclude("**/generated/**")
        }
    }
}
