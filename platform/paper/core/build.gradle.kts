dependencies {
    implementation(project(":platform:independent:api"))

    compileOnly("io.papermc.paper:paper-api:1.20.1-R0.1-SNAPSHOT")
    implementation(project(":platform:paper:api"))
}
