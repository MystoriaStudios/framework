 import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
 import org.jetbrains.gradle.ext.settings
 import java.net.URI
 import org.jetbrains.gradle.ext.runConfigurations
 import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
 import org.jetbrains.gradle.ext.Gradle

plugins {
    id("maven-publish")
    id("io.sentry.jvm.gradle") version "3.12.0"
    id("com.github.johnrengelman.shadow") version "7.1.2"
    kotlin("jvm") version "1.9.10"
    id("org.jetbrains.gradle.plugin.idea-ext") version "1.1.7"
    id("org.jetbrains.dokka") version "1.9.0"
    kotlin("kapt") version "1.9.10"
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
    apply(plugin = "com.github.johnrengelman.shadow")
    apply(plugin = "org.jetbrains.gradle.plugin.idea-ext")
    apply(plugin = "org.jetbrains.dokka")
    apply(plugin = "org.jetbrains.kotlin.kapt")

    group = "net.mystoria.framework"
    version = "1.0.11-SNAPSHOT"

    repositories {
        mavenCentral()
        maven("https://repo.aikar.co/content/groups/aikar/")

        maven {
            name = "Mystoria"
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
    }

    tasks.withType<ShadowJar> {
        archiveClassifier.set("")
        archiveFileName.set("framework-${project.name}.jar")

        relocate("co.aikar.commands", "${project.group}.commands")
        relocate("co.aikar.locales", "${project.group}.locales")
        relocate("co.aikar.locales", "${project.group}.locales")
        relocate("net.wesjd.anvilgui", "${project.group}.anvil")

        relocate("fr.mrmicky.fastboard", "${project.group}.scoreboard.sidebar")
    }

    publishing {
        publications {
            register(
                name = "mavenJava",
                type = MavenPublication::class,
                configurationAction = shadow::component
            )
        }

        repositories {
            maven {
                name = "Mystoria"
                url = uri("$artifactory_contextUrl/$artifactory_release")

                credentials {
                    username = artifactory_user
                    password = artifactory_password
                }
            }
        }
    }

    tasks["build"]
        .dependsOn(
            "shadowJar",
            "publishMavenJavaPublicationToMystoriaRepository"
        )
}

kotlin {
    jvmToolchain(18)
}


idea {
    project {
        settings {
            runConfigurations {
                create<Gradle>("Clean build") {
                    setProject(project)
                    scriptParameters = "clean build"
                }
            }
        }
    }
}