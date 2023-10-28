val protobufVersion = "3.17.3"

dependencies {
    implementation("io.grpc:grpc-kotlin-stub:1.3.0")
    implementation("io.grpc:protoc-gen-grpc-kotlin:1.3.0")
    implementation("com.google.protobuf:protobuf-java:$protobufVersion")
    implementation("com.google.protobuf:protobuf-kotlin:$protobufVersion")
}

java {
    sourceSets.getByName("main")
        .resources.srcDir("src/main/proto")
}