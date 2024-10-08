import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.jetbrains.dokka.DokkaConfiguration
import org.jetbrains.dokka.gradle.DokkaTaskPartial
import org.jetbrains.gradle.ext.Gradle
import org.jetbrains.gradle.ext.runConfigurations
import org.jetbrains.gradle.ext.settings

plugins {
    kotlin("jvm") version "1.9.10"
    kotlin("kapt") version "1.9.10"
    id("maven-publish")
    id("io.sentry.jvm.gradle") version "3.12.0"
    id("com.github.johnrengelman.shadow") version "7.1.2"
    id("org.jetbrains.gradle.plugin.idea-ext") version "1.1.7"
    id("org.jetbrains.dokka") version "1.9.0"
    id("com.google.protobuf") version "0.9.4" apply false
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

var projectVer = "1.2.2"

ext["grpcVersion"] = "1.57.2"
ext["grpcKotlinVersion"] = "1.4.0" // CURRENT_GRPC_KOTLIN_VERSION
ext["protobufVersion"] = "3.24.1"
ext["coroutinesVersion"] = "1.7.3"

allprojects {
    apply(plugin = "maven-publish")
    apply(plugin = "kotlin")
    apply(plugin = "com.github.johnrengelman.shadow")
    apply(plugin = "org.jetbrains.gradle.plugin.idea-ext")
    apply(plugin = "org.jetbrains.dokka")
    apply(plugin = "org.jetbrains.kotlin.kapt")

    group = "net.revive.framework"
    version = projectVer

    repositories {
        mavenCentral()
        maven("https://repo.aikar.co/content/groups/aikar/")
        maven("https://jitpack.io")
    }

    fun applyGlobalDepends(scope: DependencyHandlerScope, shade: Boolean = false) {
        scope {
            if (!shade) {
                compileOnly(kotlin("stdlib"))
                compileOnly(kotlin("reflect")) // Add this line

                compileOnly("org.mongodb:mongo-java-driver:3.12.11")
                compileOnly("io.lettuce:lettuce-core:6.2.4.RELEASE")
                compileOnly("io.sentry:sentry:6.29.0")
                compileOnly("com.konghq:unirest-java:3.13.6:standalone")

                // Docker
                compileOnly("com.github.docker-java:docker-java:3.3.4")

                // Cloudflare
                compileOnly("com.github.robinbraemer:CloudflareAPI:1.4.1")

                compileOnly("com.google.guava:guava:31.0.1-jre")
                compileOnly("commons-io:commons-io:2.11.0")
            } else {
                implementation(kotlin("stdlib"))
                implementation(kotlin("reflect")) // Add this line

                implementation("org.mongodb:mongo-java-driver:3.12.11")
                implementation("io.lettuce:lettuce-core:6.2.4.RELEASE")
                implementation("com.google.code.gson:gson:2.9.0")
                implementation("io.sentry:sentry:6.29.0")
                implementation("com.konghq:unirest-java:3.13.6:standalone")

                // Docker
                implementation("com.github.docker-java:docker-java:3.3.4")

                // Cloudflare
                implementation("com.github.robinbraemer:CloudflareAPI:1.4.1")

                implementation("com.google.guava:guava:31.0.1-jre")
                implementation("commons-io:commons-io:2.11.0")
            }
        }
    }

    dependencies {
        // Generate documentation
        dokkaPlugin("org.jetbrains.dokka:versioning-plugin:1.9.0")

        if (name.contains("api") || name.contains("protocol") || name.contains("minecraft-platform")) {
            applyGlobalDepends(this)
        } else if ((name.contains("core") && !name.contains("nms")) || name.contains("backend")) {
            applyGlobalDepends(this, true)
        } else {
            compileOnly(kotlin("stdlib"))
            compileOnly(kotlin("reflect")) // Add this line
        }
    }

    tasks.withType<ShadowJar> {
        archiveClassifier.set("")
        archiveFileName.set("framework-${project.name}.jar")

        mergeServiceFiles()

        relocate("co.aikar.commands", "${project.group}.commands")
        relocate("co.aikar.locales", "${project.group}.locales")
        relocate("co.aikar.locales", "${project.group}.locales")
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

    tasks.processResources {
        duplicatesStrategy = DuplicatesStrategy.WARN // or DuplicatesStrategy.FAIL
    }

    tasks["build"].dependsOn("shadowJar")
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