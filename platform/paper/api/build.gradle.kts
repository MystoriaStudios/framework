dependencies {
    implementation(project(":platform:paper"))
    compileOnly("io.papermc.paper:paper-api:1.20.1-R0.1-SNAPSHOT")

    implementation("com.github.cryptomorin:XSeries:9.5.0") { isTransitive = false }
}