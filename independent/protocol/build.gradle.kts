java {
    sourceSets.getByName("main")
        .resources.srcDir("src/main/proto")
}

dependencies {
    implementation("com.google.protobuf:protobuf-java:4.0.0-rc-2")
}