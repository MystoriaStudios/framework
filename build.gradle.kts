import java.net.URI

plugins {
    id("maven-publish")
    id("com.github.johnrengelman.shadow") version "7.1.2"
    kotlin("kapt") version "1.9.10"
    kotlin("jvm") version "1.9.10"
}

val artifactory_contextUrl: String by project
val artifactory_release: String by project
val artifactory_user: String by project
val artifactory_password: String by project

allprojects {
    apply(plugin = "maven-publish")
    apply(plugin = "com.github.johnrengelman.shadow")
    apply(plugin = "kotlin")

    this.group = "net.mystoria.framework"
    this.version =  "1.0.01-SNAPSHOT"

    repositories {
        mavenCentral()
        maven("https://repo.aikar.co/content/groups/aikar/")

        maven {
            this.name = "Jungle"
            this.url = URI("${artifactory_contextUrl}/${artifactory_release}")

            credentials {
                this.username = artifactory_user
                this.password = artifactory_password
            }
        }
    }

    dependencies {
        implementation("org.mongodb:mongo-java-driver:3.12.11")
        implementation("io.lettuce:lettuce-core:6.2.4.RELEASE")
        implementation("com.google.code.gson:gson:2.9.0")
    }
}

kotlin {
    jvmToolchain(18)
}