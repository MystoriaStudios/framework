plugins {
    id("com.github.johnrengelman.shadow") version "7.1.2"
    application
}

application {
    mainClass.set("net.mystoria.framework.FrameworkAppKt")
}

dependencies {
    implementation(project(":independent:independent-api"))
    implementation("org.reflections:reflections:0.10.2")
    implementation("com.squareup.retrofit:retrofit:2.0.0-beta2")
}

tasks {
    shadowJar {
        archiveClassifier.set("")
        archiveFileName.set("framework-${project.name}.jar")
    }

    build {
        dependsOn(shadowJar)
    }
}