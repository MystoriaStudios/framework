plugins {
    id("io.ktor.plugin") version "2.3.4"
}

application {
    mainClass.set("io.ktor.server.netty.EngineMain")
}

dependencies {
    implementation(project(":independent:independent-api"))
    implementation("org.reflections:reflections:0.10.2")

    implementation("io.jooby:jooby-kotlin:3.0.5")
}