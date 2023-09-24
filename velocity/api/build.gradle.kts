dependencies {
    implementation(project(":velocity"))
    implementation(project(":independent:independent-api"))

    implementation("com.squareup.retrofit:retrofit:2.0.0-beta2")
    compileOnly("com.velocitypowered:velocity-api:3.2.0-SNAPSHOT")

    implementation("co.aikar:acf-core:0.5.1-SNAPSHOT")
}
