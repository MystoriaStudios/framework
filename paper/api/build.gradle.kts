repositories {
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://repo.codemc.io/repository/maven-snapshots/")
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
}

dependencies {
    implementation(project(":paper"))
    implementation(project(":independent:independent-api"))
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    compileOnly("io.papermc.paper:paper-api:1.20.1-R0.1-SNAPSHOT")
    implementation("co.aikar:acf-paper:0.5.1-SNAPSHOT")
    implementation("fr.mrmicky:fastboard:2.0.0")
    implementation("me.lucko:helper:5.6.10")

    compileOnly(project(":paper:nms:nms-core"))
    compileOnly("com.github.cryptomorin:XSeries:9.5.0") { isTransitive = false }
}