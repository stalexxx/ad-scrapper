
import org.jetbrains.kotlin.gradle.dsl.Coroutines

val kotlintest_version: String by ext

plugins {
    application
    kotlin("jvm")
    id ("io.spring.dependency-management")
}

application {
    mainClassName = "com.stalex.avito.mainKt.kt"
}

kotlin {
    // configure<org.jetbrains.kotlin.gradle.dsl.KotlinProjectExtension>
    experimental.coroutines = Coroutines.ENABLE
}

dependencies {
    val skrape_version = "1.0.0"
    val kmongo_version = "3.6.2"

    compile(project(":pipeline"))

    compile(kotlin("stdlib-jdk8"))
//    compile("com.github.esafirm:skrape:$skrape_version")
    compile("com.github.stalexxx:skrape:-SNAPSHOT")
    compile("org.litote.kmongo:kmongo:$kmongo_version")

    compile("io.github.microutils:kotlin-logging")
    testCompile("io.kotlintest:kotlintest")
    testCompile("io.mockk:mockk")

    compile("com.github.salomonbrys.kodein:kodein")
}
