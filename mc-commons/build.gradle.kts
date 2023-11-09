dependencies {
    compileOnly("net.kyori:adventure-api:4.14.0")
    compileOnly("net.kyori:adventure-key:4.14.0")
    compileOnly("net.kyori:adventure-text-minimessage:4.10.1")

    compileOnly("co.aikar:acf-core:0.5.1-SNAPSHOT")
    compileOnly(project(":independent:independent-api"))

    compileOnly("io.grpc:grpc-netty:${rootProject.ext["grpcVersion"]}")
    compileOnly(project(":independent:protocol-stub"))
    compileOnly("io.grpc:grpc-protobuf:${rootProject.ext["grpcVersion"]}")
    compileOnly("com.google.protobuf:protobuf-java-util:${rootProject.ext["protobufVersion"]}")
    compileOnly("io.grpc:grpc-kotlin-stub:${rootProject.ext["grpcKotlinVersion"]}")
}