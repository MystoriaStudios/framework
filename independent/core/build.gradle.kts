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