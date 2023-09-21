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

val gitCommitHash = tasks.register("gitCommitHash") {
    dependsOn("shadowJar")
    val commandLine = "git rev-parse HEAD".split(" ")
    val process = Runtime.getRuntime().exec(commandLine.toTypedArray()) // Converts the split commandLine to varargs
    val standardOutput = process.inputReader().readLine()

    doLast {
        project.version = standardOutput.toString().trim().substring(0, 8)
    }
}

allprojects {
    apply(plugin = "maven-publish")
    apply(plugin = "com.github.johnrengelman.shadow")
    apply(plugin = "kotlin")
    apply(plugin = "org.jetbrains.kotlin.kapt")

    group = "net.mystoria.framework"

    repositories {
        mavenCentral()
        maven("https://repo.aikar.co/content/groups/aikar/")

        maven {
            name = "Jungle"
            url = URI("${artifactory_contextUrl}/${artifactory_release}")

            credentials {
                username = artifactory_user
                password = artifactory_password
            }
        }
    }

    dependencies {
        implementation("org.mongodb:mongo-java-driver:3.12.11")
        implementation("io.lettuce:lettuce-core:6.2.4.RELEASE")
        implementation("com.google.code.gson:gson:2.9.0")
    }

    tasks.shadowJar {
        archiveClassifier.set("")
        archiveFileName.set("framework-${project.name}.jar")
    }

    publishing {
        publications {
            create<MavenPublication>("shadow") {
                from(components["kotlin"])
            }
        }

        repositories {
            maven {
                name = "Jungle"
                url = uri("$artifactory_contextUrl/$artifactory_release")

                credentials {
                    username = artifactory_user
                    password = artifactory_password
                }
            }
        }
    }

    tasks.named("build") {
        dependsOn(tasks.shadowJar, "publishShadowPublicationToJungleRepository")
    }
}

kotlin {
    jvmToolchain(18)
}