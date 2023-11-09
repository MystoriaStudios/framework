import com.google.protobuf.gradle.protobuf

plugins {
    id("com.google.protobuf")
}


dependencies {
    protobuf(project(":independent:protocol"))

    compileOnly("org.jetbrains.kotlinx:kotlinx-coroutines-core:${rootProject.ext["coroutinesVersion"]}")

    compileOnly("io.grpc:grpc-stub:${rootProject.ext["grpcVersion"]}")
    compileOnly("io.grpc:grpc-protobuf:${rootProject.ext["grpcVersion"]}")
    compileOnly("com.google.protobuf:protobuf-java-util:${rootProject.ext["protobufVersion"]}")
    compileOnly("com.google.protobuf:protobuf-kotlin:${rootProject.ext["protobufVersion"]}")
    compileOnly("io.grpc:grpc-kotlin-stub:${rootProject.ext["grpcKotlinVersion"]}")
}


kotlin {
    jvmToolchain(17)
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().all {
    kotlinOptions {
        freeCompilerArgs = listOf("-opt-in=kotlin.RequiresOptIn")
    }
}

protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:${rootProject.ext["protobufVersion"]}"
    }
    plugins {
        create("grpc") {
            artifact = "io.grpc:protoc-gen-grpc-java:${rootProject.ext["grpcVersion"]}"
        }
        create("grpckt") {
            artifact = "io.grpc:protoc-gen-grpc-kotlin:${rootProject.ext["grpcKotlinVersion"]}:jdk8@jar"
        }
    }
    generateProtoTasks {
        all().forEach {
            it.plugins {
                create("grpc")
                create("grpckt")
            }
            it.builtins {
                create("kotlin")
            }
        }
    }
}