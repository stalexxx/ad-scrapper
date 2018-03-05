

import org.jetbrains.kotlin.gradle.dsl.Coroutines

val ktor_version = "0.9.1"
//val fuel_version: String by ext

plugins {
    application
    kotlin("jvm")
    id ("io.spring.dependency-management")
}

application {
    mainClassName = "io.ktor.server.netty.DevelopmentEngine"
}

kotlin {
    // configure<org.jetbrains.kotlin.gradle.dsl.KotlinProjectExtension>
    experimental.coroutines = Coroutines.ENABLE
}

dependencies {
    compile(kotlin("stdlib"))
    compile(kotlin("stdlib-jre8"))
    compile(kotlin("reflect"))
//    compile(project(":core"))

    compile("io.ktor:ktor-server-core:$ktor_version")
    compile("io.ktor:ktor-server-netty:$ktor_version")
    compile("io.ktor:ktor-gson:$ktor_version")

    compile("com.github.esafirm:skrape:1.0.0")

    compile("com.github.kittinunf.fuel:fuel")
    compile("com.github.kittinunf.fuel:fuel-gson")

    testCompile("io.mockk:mockk")
}
