plugins {
    id("io.papermc.paperweight.userdev") version "1.5.5"
}

dependencies {
    compileOnly(project(":paper:nms:nms-core"))
    paperweight.paperDevBundle("1.19.4-R0.1-SNAPSHOT")
}

tasks {
    build {
        dependsOn(reobfJar)
    }
}