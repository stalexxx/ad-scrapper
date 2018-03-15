
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

//val fuel_version by extra { "1.12.1" }

plugins {
    base
    kotlin("jvm") version "1.2.30" apply false
    id("org.jlleitschuh.gradle.ktlint") version "3.1.0"
    id ("io.spring.dependency-management") version "1.0.4.RELEASE"
    id ("com.github.ben-manes.versions") version "0.17.0"

    `project-report`
    `jacoco`
}

//jacoco {
//
//}

ktlint {
    version = "0.19.0"
    verbose = true
    outputToConsole = true
}

allprojects {

    group = "com.stalex.ad-scrapper"
    version = "1.0"

    repositories {
        jcenter()
        maven("https://jitpack.io")
        maven("http://dl.bintray.com/kotlin/ktor")
        maven("https://dl.bintray.com/kotlin/kotlinx")
        maven("https://plugins.gradle.org/m2/")
    }
}

buildscript {
    repositories {
        maven("https://repo.spring.io/plugins-snapshot")
    }

    dependencies {
        classpath("io.spring.gradle:dependency-management-plugin:1.0.5.BUILD-SNAPSHOT")
    }
}

subprojects {

    tasks.withType<KotlinCompile> {
        println("Configuring $name in project ${project.name}...")

        kotlinOptions {
            suppressWarnings = true
            jvmTarget = "1.8"
        }
    }

    plugins {
        id ("io.spring.dependency-management") apply true
    }

    val extensions = this.extensions
    print("ext: $extensions")

    afterEvaluate {

        this.dependencyManagement {

            val fuel_version = "1.12.1"
            val mockk_version = "1.7.10"
            val slf4j_version = "1.7.25"
            val kotlintest_version = "2.0.7"
            val coroutines_version = "0.22.5"
            val kotlin_logging = "1.5.3"

//
            dependencies {
                dependency("io.kotlintest:kotlintest:$kotlintest_version")
                dependency("io.mockk:mockk:$mockk_version")
                dependency("org.slf4j:slf4j-jdk14:$slf4j_version")
                dependency("io.github.microutils:kotlin-logging:$kotlin_logging")
                dependency("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutines_version")

                dependency("com.github.salomonbrys.kodein:kodein:4.1.0")

                dependency("com.github.kittinunf.fuel:fuel:$fuel_version")
                dependency("com.github.kittinunf.fuel:fuel-gson:$fuel_version")
            }
        }
    }
}

dependencies {
    //     Make the root project archives configuration depend on every subproject
    subprojects.forEach {
        archives(it)
    }
}
