plugins {
    application
    kotlin("jvm")
    kotlin("plugin.serialization")
}

group = "com.akash.kontactplus"
version = "0.0.1"

application {
    mainClass.set("com.akash.kontactplus.ai.ApplicationKt")
}

kotlin {
    jvmToolchain(11)
}

val ktor_version = libs.versions.ktor.get()

dependencies {
    implementation("io.ktor:ktor-server-core-jvm:$ktor_version")
    implementation("io.ktor:ktor-server-netty-jvm:$ktor_version")
    implementation("io.ktor:ktor-server-content-negotiation-jvm:$ktor_version")
    implementation("io.ktor:ktor-serialization-kotlinx-json-jvm:$ktor_version")
    implementation("io.ktor:ktor-client-core-jvm:$ktor_version")
    implementation("io.ktor:ktor-client-cio-jvm:$ktor_version")
    implementation("io.ktor:ktor-client-content-negotiation-jvm:$ktor_version")
    
    implementation(libs.logback)
    implementation("io.github.cdimascio:dotenv-kotlin:6.4.1")
}
