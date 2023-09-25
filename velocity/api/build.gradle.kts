dependencies {
    implementation(project(":velocity"))
    implementation(project(":independent:independent-api"))

    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    compileOnly("com.velocitypowered:velocity-api:3.2.0-SNAPSHOT")

    implementation("co.aikar:acf-core:0.5.1-SNAPSHOT")
}
