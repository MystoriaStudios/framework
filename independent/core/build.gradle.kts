plugins {
    application
}

application {
    mainClass.set("net.revive.framework.FrameworkAppKt")
}

dependencies {
    implementation(project(":independent:independent-api"))
    implementation("org.reflections:reflections:0.10.2")

    implementation("io.kubernetes:client-java-api:15.0.1")
    implementation("io.kubernetes:client-java:15.0.1")

    implementation("com.squareup.retrofit2:retrofit:2.9.0")
}