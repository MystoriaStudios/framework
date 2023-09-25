repositories {
    maven("https://repo.papermc.io/repository/maven-public/")
}


dependencies {
    implementation(project(":independent:independent-api"))
    implementation(project(":paper:paper-api"))

    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")

    compileOnly("com.velocitypowered:velocity-api:3.2.0-SNAPSHOT")
    annotationProcessor("com.velocitypowered:velocity-api:3.2.0-SNAPSHOT")

}