repositories {
    maven("https://repo.papermc.io/repository/maven-public/")
}


dependencies {
    implementation(project(":independent:independent-api"))
    implementation(project(":paper:paper-api"))

    implementation("com.squareup.retrofit:retrofit:2.0.0-beta2")

    compileOnly("com.velocitypowered:velocity-api:3.2.0-SNAPSHOT")
    annotationProcessor("com.velocitypowered:velocity-api:3.2.0-SNAPSHOT")

}