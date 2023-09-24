plugins {
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

repositories {
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://repo.codemc.io/repository/maven-snapshots/")
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
}

dependencies {
    // local project includes
    implementation(project(":independent:independent-api"))
    implementation(project(":paper:paper-api"))

    // reference libraries
    compileOnly("io.papermc.paper:paper-api:1.20.1-R0.1-SNAPSHOT")
    compileOnly("me.clip:placeholderapi:2.11.2")

    compileOnly("net.kyori:adventure-platform-bukkit:4.1.0")

    // final compilation libraries
    implementation("co.aikar:acf-paper:0.5.1-SNAPSHOT")
    implementation("net.kyori:adventure-text-serializer-gson:4.10.1")
    implementation("net.kyori:adventure-text-minimessage:4.10.1")
    implementation("fr.mrmicky:fastboard:2.0.0")
    implementation("net.wesjd:anvilgui:1.9.0-SNAPSHOT")
    implementation("com.github.cryptomorin:XSeries:9.5.0") { isTransitive = false }
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("me.lucko:helper:5.6.10")
    kapt("me.lucko:helper:5.6.10")

    // NMS Stuff
    implementation(project(":paper:nms:nms-core"))
    implementation(project(":paper:nms:nms-v1_20_R1", "reobf"))
}

tasks.shadowJar {
    relocate("co.aikar.commands", "${group}.commands")
    relocate("co.aikar.locales", "${group}.locales")
    relocate("co.aikar.locales", "${group}.locales")
    relocate("net.wesjd.anvilgui", "${group}.anvil")

    relocate("fr.mrmicky.fastboard", "${group}.scoreboard.sidebar")
}