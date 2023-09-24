dependencies {
    implementation(project(":paper"))
    implementation(project(":independent:independent-api"))
    implementation("com.squareup.retrofit:retrofit:2.0.0-beta2")
    compileOnly("io.papermc.paper:paper-api:1.20.1-R0.1-SNAPSHOT")
    implementation("co.aikar:acf-core:0.5.1-SNAPSHOT")

    compileOnly("com.github.cryptomorin:XSeries:9.5.0") { isTransitive = false }
}