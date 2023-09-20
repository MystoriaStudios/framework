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

    this.group = "com.junglerealms.framework"
    this.version =  "1.0.01-SNAPSHOT"

    repositories {
        mavenCentral()

        maven {
            this.name = "Jungle"
            this.url = URI("${artifactory_contextUrl}/${artifactory_release}")

            credentials {
                this.username = artifactory_user
                this.password = artifactory_password
            }
        }
    }
}

kotlin {
    jvmToolchain(18)
}