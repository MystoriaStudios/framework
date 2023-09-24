plugins {
    id("com.github.johnrengelman.shadow") version "7.1.2"
}
dependencies {
    implementation("com.squareup.retrofit:retrofit:2.0.0-beta2")
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

