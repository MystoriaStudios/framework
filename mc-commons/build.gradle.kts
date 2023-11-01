dependencies {
    compileOnly("net.kyori:adventure-api:4.14.0")
    compileOnly("net.kyori:adventure-key:4.14.0")
    compileOnly("net.kyori:adventure-text-minimessage:4.10.1")

    implementation("co.aikar:acf-core:0.5.1-SNAPSHOT")
    compileOnly(project(":independent:independent-api"))

    implementation("io.grpc:grpc-netty:${rootProject.ext["grpcVersion"]}")
    implementation(project(":independent:protocol-stub"))
    implementation("io.grpc:grpc-protobuf:${rootProject.ext["grpcVersion"]}")
    implementation("com.google.protobuf:protobuf-java-util:${rootProject.ext["protobufVersion"]}")
    implementation("io.grpc:grpc-kotlin-stub:${rootProject.ext["grpcKotlinVersion"]}")
}