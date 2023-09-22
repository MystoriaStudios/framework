plugins {
    id("io.papermc.paperweight.userdev") version "1.5.5"
}

dependencies {
    compileOnly(project(":paper:nms:nms-core"))
    paperweight.paperDevBundle("1.20.1-R0.1-SNAPSHOT")
}

tasks.named("build") {
    dependsOn("reobfJar")
}