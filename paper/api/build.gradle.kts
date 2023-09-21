dependencies {
    implementation(project(":paper"))
    implementation(project(":independent:independent-api"))
    compileOnly("io.papermc.paper:paper-api:1.20.1-R0.1-SNAPSHOT")

    compileOnly("com.github.cryptomorin:XSeries:9.5.0") { isTransitive = false }
}