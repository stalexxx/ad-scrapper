

import org.jetbrains.kotlin.gradle.dsl.Coroutines

val fuel_version: String by ext


plugins {
    kotlin("jvm")
    id ("io.spring.dependency-management")
}

kotlin {
    // configure<org.jetbrains.kotlin.gradle.dsl.KotlinProjectExtension>
    experimental.coroutines = Coroutines.ENABLE
}

dependencies {
    compile(kotlin("stdlib"))
    compile(kotlin("stdlib-jre8"))
    compile(kotlin("reflect"))
    compile("org.jetbrains.kotlinx:kotlinx-coroutines-core")

    testCompile("io.kotlintest:kotlintest")
    testCompile("io.mockk:mockk")

    compile("org.slf4j:slf4j-jdk14")
}
