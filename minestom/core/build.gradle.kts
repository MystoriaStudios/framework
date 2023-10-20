plugins {
    application
    java
}

application {
    mainClass.set("net.revive.framework.MinestomFrameworkServerKt")
}

dependencies {
    implementation("dev.hollowcube:minestom-ce:517b195b5e") {
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
}