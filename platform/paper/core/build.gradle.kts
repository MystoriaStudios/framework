dependencies {
    // local project includes
    implementation(project(":platform:independent:api"))
    implementation(project(":platform:paper:api"))
    implementation(project(mapOf("path" to ":platform:paper:api")))

    // reference libraries
    compileOnly("io.papermc.paper:paper-api:1.20.1-R0.1-SNAPSHOT")


    // final compilation libraries
    implementation("me.lucko:helper:5.6.10")
    kapt("me.lucko:helper:5.6.10")
}
