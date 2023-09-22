plugins {
    id("io.ktor.plugin") version "2.3.4"
}

application {
    mainClass.set("net.mystoria.framework.FrameworkAppKt")
}

dependencies {
    implementation(project(":independent:independent-api"))
    implementation("org.reflections:reflections:0.10.2")
}