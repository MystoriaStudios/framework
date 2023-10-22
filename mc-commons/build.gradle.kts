dependencies {
    compileOnly("net.kyori:adventure-api:4.14.0")
    compileOnly("net.kyori:adventure-key:4.14.0")
    compileOnly("net.kyori:adventure-text-minimessage:4.10.1")

    implementation("co.aikar:acf-core:0.5.1-SNAPSHOT")
    compileOnly(project(":independent:independent-api"))
}