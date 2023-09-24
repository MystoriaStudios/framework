import java.net.URI

plugins {
    id("maven-publish")
    id("io.sentry.jvm.gradle") version "3.12.0"
    id("com.github.johnrengelman.shadow") version "7.1.2"
    kotlin("kapt") version "1.9.10"
    kotlin("jvm") version "1.9.10"
}

val artifactory_contextUrl: String by project
val artifactory_release: String by project
val artifactory_user: String by project
val artifactory_password: String by project

val sentry_auth: String by project

sentry {
    // Generates a JVM (Java, Kotlin, etc.) source bundle and uploads your source code to Sentry.
    // This enables source context, allowing you to see your source
    // code as part of your stack traces in Sentry.
    includeSourceContext.set(true)
    org.set("mystoria-studios-196d3d453")
    projectName.set("java")
    authToken.set(sentry_auth)
}

allprojects {
    apply(plugin = "maven-publish")
    apply(plugin = "kotlin")
    apply(plugin = "com.github.johnrengelman.shadow")
    apply(plugin = "org.jetbrains.kotlin.kapt")
    apply(plugin = "com.github.johnrengelman.shadow")

    group = "net.mystoria.framework"
    version = "1.0.07-SNAPSHOT"

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
        implementation(kotlin("stdlib"))
        implementation(kotlin("reflect")) // Add this line

        implementation("org.mongodb:mongo-java-driver:3.12.11")
        implementation("io.lettuce:lettuce-core:6.2.4.RELEASE")
        implementation("com.google.code.gson:gson:2.9.0")
        implementation("io.sentry:sentry:6.29.0")
        implementation("com.konghq:unirest-java:3.13.6:standalone")

        implementation("com.google.guava:guava:31.0.1-jre")
        implementation("commons-io:commons-io:2.11.0")
        implementation("com.squareup.retrofit:retrofit:2.0.0-beta2")
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

    tasks.shadowJar {
        archiveFileName.set("framework-${project.name}.jar")
    }

    tasks.named("build") {
        if (project.name.contains("core")) dependsOn(tasks.shadowJar)
        dependsOn("publishShadowPublicationToJungleRepository")
    }
}

kotlin {
    jvmToolchain(18)
}