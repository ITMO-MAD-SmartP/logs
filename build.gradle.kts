val kotlin_version: String by project
val logback_version: String by project

plugins {
    kotlin("jvm") version "2.0.0"
    id("io.ktor.plugin") version "2.3.11"
    id("io.freefair.lombok") version "5.3.0"
}

group = "com.example"
version = "0.0.1"

application {
    mainClass.set("com.example.ApplicationKt")

    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("io.ktor:ktor-server-core-jvm")
    implementation("com.ucasoft.ktor:ktor-simple-cache:0.+")
    implementation("com.ucasoft.ktor:ktor-simple-redis-cache-jvm:0.+")
    implementation("io.ktor:ktor-server-netty-jvm")
    implementation("ch.qos.logback:logback-classic:$logback_version")
    testImplementation("io.ktor:ktor-server-tests-jvm")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:$kotlin_version")
    implementation("redis.clients:jedis:4.0.1")
    implementation("com.ecwid.clickhouse:clickhouse-client:0.13.0")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.13.1")
}
