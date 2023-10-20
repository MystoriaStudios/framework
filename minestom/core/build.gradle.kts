plugins {
    application
    java
}

application {
    mainClass.set("net.revive.framework.MinestomFrameworkServerKt")
}

dependencies {
    implementation("dev.hollowcube:minestom-ce:438338381e") {
        exclude("org.slf4j", "slf4j-api")
        exclude("ch.qos.logback", "logback-classic")
    }
    implementation("dev.hollowcube:minestom-ce-extensions:1.2.0")

    implementation(project(":independent:independent-api"))
    implementation(project(":minestom:minestom-api"))

    implementation(project(":minecraft-platform"))

    implementation("org.apache.commons:commons-lang3:3.13.0")

    implementation("net.kyori:adventure-text-serializer-gson:4.10.1")
    implementation("net.kyori:adventure-text-minimessage:4.10.1")

    implementation("org.jline:jline:3.23.0")
    implementation("org.jline:jline-terminal-jansi:3.23.0")

    implementation("net.java.dev.jna:jna:5.13.0")
    implementation("org.slf4j:slf4j-api:1.7.32")
    implementation("ch.qos.logback:logback-classic:1.2.6")

    // Add tinylog
    implementation("org.tinylog:tinylog-api-kotlin:2.6.1")
    implementation("org.tinylog:tinylog-impl:2.6.1")
}