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
}