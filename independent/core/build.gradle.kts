plugins {
    application
}

application {
    mainClass.set("net.revive.framework.FrameworkAppKt")
}

dependencies {
    implementation(project(":independent:independent-api"))
    implementation("org.reflections:reflections:0.10.2")

    implementation("com.squareup.retrofit:retrofit:2.0.0-beta2")

    implementation("io.grpc:grpc-netty:${rootProject.ext["grpcVersion"]}")
    implementation("io.grpc:grpc-protobuf:${rootProject.ext["grpcVersion"]}")
    implementation("com.google.protobuf:protobuf-java-util:${rootProject.ext["protobufVersion"]}")
    implementation("io.grpc:grpc-kotlin-stub:${rootProject.ext["grpcKotlinVersion"]}")
    implementation(project(":independent:protocol-stub"))

    implementation("io.kubernetes:client-java-api:15.0.1")
    implementation("io.kubernetes:client-java:15.0.1")

    implementation("com.squareup.retrofit2:retrofit:2.9.0")
}