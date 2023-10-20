dependencies {
    compileOnly("dev.hollowcube:minestom-ce:517b195b5e") {
        exclude("org.slf4j", "slf4j-api")
        exclude("ch.qos.logback", "logback-classic")
    }
    compileOnly("dev.hollowcube:minestom-ce-extensions:1.2.0")

    implementation(project(":independent:independent-api"))
    compileOnly(project(":minecraft-platform"))

    implementation("org.apache.commons:commons-lang3:3.13.0")

}