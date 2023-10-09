repositories {
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://repo.codemc.io/repository/maven-snapshots/")
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
    maven("https://repo.dmulloy2.net/repository/public/")
}

dependencies {
    // local project includes
    implementation(project(":independent:independent-api"))
    implementation(project(":paper:paper-api"))


    // reference libraries
    compileOnly("io.papermc.paper:paper-api:1.20.1-R0.1-SNAPSHOT")
    compileOnly("me.clip:placeholderapi:2.11.2")
    compileOnly("com.comphenix.protocol:ProtocolLib:5.1.0")

    compileOnly("net.luckperms:api:5.4")
    compileOnly("net.kyori:adventure-platform-bukkit:4.1.0")

    compileOnly("co.aikar:acf-paper:0.5.1-SNAPSHOT")
    // final compilation libraries
    implementation("net.wesjd:anvilgui:1.9.0-SNAPSHOT")
    implementation("net.kyori:adventure-text-serializer-gson:4.10.1")
    implementation("net.kyori:adventure-text-minimessage:4.10.1")
    implementation("fr.mrmicky:fastboard:2.0.0")
    implementation("net.wesjd:anvilgui:1.9.0-SNAPSHOT")
    implementation("com.github.cryptomorin:XSeries:9.5.0") { isTransitive = false }
    compileOnly("me.lucko:helper:5.6.10")
    kapt("me.lucko:helper:5.6.10")

    // NMS Stuff
    implementation(project(":paper:nms:nms-core"))
    implementation(project(":paper:nms:nms-v1_20_R1", "reobf"))
    implementation(project(":paper:nms:nms-v1_19_R4", "reobf"))
    implementation(project(":paper:nms:nms-v1_18_R2", "reobf"))
    implementation(project(":paper:nms:nms-v1_17_R1", "reobf"))
    
    //These don't use deobf because Official Mojang Mappings didn't exist for these.
    implementation(project(":paper:nms:nms-v1_12_R2"))
    implementation(project(":paper:nms:nms-v1_8_R8"))
}