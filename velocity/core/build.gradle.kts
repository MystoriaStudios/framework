plugins {
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

tasks {
    build {
        dependsOn(shadowJar)
    }

    shadowJar {
        archiveClassifier.set("")
        archiveFileName.set("framework-${project.name}.jar")
    }
}

