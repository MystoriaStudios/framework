dependencies {
    // local project includes
    implementation(project(":platform:independent:api"))

    // reference libraries
    compileOnly("io.papermc.paper:paper-api:1.20.1-R0.1-SNAPSHOT")

    // final compilation libraries
    implementation("com.github.cryptomorin:XSeries:9.5.0") { isTransitive = false }
    implementation("me.lucko:helper:5.6.10")
    kapt("me.lucko:helper:5.6.10")
}
