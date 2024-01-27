@file:Suppress("UNUSED_EXPRESSION")

plugins {
    id("java") // Alternative id("java")
    id("org.jetbrains.kotlin.jvm") version "1.5.21" // Alternative id("org.jetbrains.kotlin.jvm") version "1.5.21"
}

group "org.proto" // If failing, alternatively use ""
version "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    google()
    maven {
        url = uri("https://repo.powbot.org/releases")
    }
}

// If failing, alternatively use ""
dependencies {
    implementation("org.powbot:client-sdk:1.+") // + means gradle will pull the latest libs on refresh of project
    implementation("org.powbot:client-sdk-loader:1.+")
    implementation("com.google.guava:guava:31.1-jre") // needed for @Subscribe annotations / event bus
    implementation("com.fasterxml.jackson.core:jackson-core:2.13.0")
}