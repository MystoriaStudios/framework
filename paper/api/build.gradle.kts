repositories {
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://repo.codemc.io/repository/maven-snapshots/")
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
    maven("https://repo.dmulloy2.net/repository/public/")
}

dependencies {
    // all of our stuff
    implementation(project(":paper"))
    implementation(project(":independent:independent-api"))
    implementation(project(":minecraft-platform"))

    implementation("me.lucko:helper:5.6.10")

    compileOnly("com.squareup.retrofit2:retrofit:2.9.0")
    compileOnly("net.wesjd:anvilgui:1.9.0-SNAPSHOT")
    compileOnly("com.squareup.retrofit2:converter-gson:2.9.0")
    compileOnly("io.papermc.paper:paper-api:1.20.1-R0.1-SNAPSHOT")
    compileOnly("co.aikar:acf-paper:0.5.1-SNAPSHOT")
    compileOnly("fr.mrmicky:fastboard:2.0.0")
    compileOnly("me.lucko:helper:5.6.10")
    compileOnly("com.comphenix.protocol:ProtocolLib:5.1.0")

    compileOnly(project(":paper:nms:nms-core"))
    compileOnly("com.github.cryptomorin:XSeries:9.5.0") { isTransitive = false }
}