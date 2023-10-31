import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.jetbrains.dokka.DokkaConfiguration
import org.jetbrains.dokka.gradle.DokkaTaskPartial
import org.jetbrains.gradle.ext.Gradle
import org.jetbrains.gradle.ext.runConfigurations
import org.jetbrains.gradle.ext.settings

plugins {
    id("maven-publish")
    id("io.sentry.jvm.gradle") version "3.12.0"
    id("com.github.johnrengelman.shadow") version "7.1.2"
    kotlin("jvm") version "1.9.10"
    id("org.jetbrains.gradle.plugin.idea-ext") version "1.1.7"
    id("org.jetbrains.dokka") version "1.9.0"
    kotlin("kapt") version "1.9.10"
    id("com.google.protobuf") version "0.8.15"
}

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

var projectVer = "1.1.1-SNAPSHOT"

ext["grpcVersion"] = "1.37.0"
ext["grpcKotlinVersion"] = "1.1.0"
ext["protobufVersion"] = "3.15.8"

val grpcVersion = "1.48.0"
val grpcStubVersion = "1.48.0"
val grpcKotlinVersion = "1.3.0"

val protobufVersion = "3.21.9"
val protobufPluginVersion = "0.8.19"

allprojects {
    apply(plugin = "maven-publish")
    apply(plugin = "kotlin")
    apply(plugin = "com.github.johnrengelman.shadow")
    apply(plugin = "com.github.johnrengelman.shadow")
    apply(plugin = "org.jetbrains.gradle.plugin.idea-ext")
    apply(plugin = "org.jetbrains.dokka")
    apply(plugin = "org.jetbrains.kotlin.kapt")
    apply(plugin = "com.google.protobuf")

    group = "net.revive.framework"
    version = projectVer

    repositories {
        mavenCentral()
        maven("https://repo.aikar.co/content/groups/aikar/")
        maven("https://jitpack.io")
    }

    dependencies {
        implementation(kotlin("stdlib"))
        implementation(kotlin("reflect")) // Add this line

        implementation("org.mongodb:mongo-java-driver:3.12.11")
        implementation("io.lettuce:lettuce-core:6.2.4.RELEASE")
        implementation("com.google.code.gson:gson:2.9.0")
        implementation("io.sentry:sentry:6.29.0")
        implementation("com.konghq:unirest-java:3.13.6:standalone")

        implementation("io.grpc:grpc-netty:$grpcVersion")
        implementation("io.grpc:grpc-stub:$grpcVersion")
        implementation("io.grpc:grpc-all:$grpcVersion")
        implementation("io.grpc:grpc-protobuf:$grpcVersion")
        implementation("io.grpc:grpc-kotlin-stub:$grpcKotlinVersion")
        implementation("com.google.protobuf:protobuf-kotlin:$protobufVersion")
        implementation("com.google.protobuf:protobuf-java:$protobufVersion")
        implementation("io.grpc:grpc-kotlin-stub:1.3.0")
        implementation("io.grpc:protoc-gen-grpc-kotlin:1.3.0")


        implementation("com.google.guava:guava:31.0.1-jre")
        implementation("commons-io:commons-io:2.11.0")

        testImplementation(kotlin("test"))

        // Generate documentation
        dokkaPlugin("org.jetbrains.dokka:versioning-plugin:1.9.0")
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

    tasks.withType<DokkaTaskPartial>().configureEach {
        dokkaSourceSets.configureEach {
            documentedVisibilities.set(
                setOf(
                    DokkaConfiguration.Visibility.PUBLIC,
                    DokkaConfiguration.Visibility.PROTECTED
                )
            )
        }
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
            mavenLocal()

            maven {
                name = "MystoriaDev"
                url = uri("https://repo.nopox.xyz/mystoria-dev")
                credentials(PasswordCredentials::class)
                authentication {
                    create<BasicAuthentication>("basic")
                }
            }

            maven {
                name = "MystoriaProd"
                url = uri("https://repo.nopox.xyz/mystoria-prod")
                credentials(PasswordCredentials::class)
                authentication {
                    create<BasicAuthentication>("basic")
                }
            }
        }
    }

    tasks.withType<Copy> {
        from("$projectDir/src/main/resources") {
            include("**/*.proto")
            duplicatesStrategy = DuplicatesStrategy.INCLUDE
        }
    }


    tasks["build"]
        .dependsOn(
            "shadowJar",
            "publishMavenJavaPublicationToMavenLocalRepository",
        )
}

kotlin {
    jvmToolchain(17)
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