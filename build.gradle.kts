import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    kotlin("jvm") version "1.8.21"
    kotlin("plugin.serialization") version "1.8.21"
    application
    id("io.ktor.plugin") version "2.3.0"
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = "com.eventify"
version = "0.0.1"

application {
    mainClass.set("com.eventify.ApplicationKt")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("io.ktor:ktor-server-core-jvm:2.3.0")
    implementation("io.ktor:ktor-server-netty-jvm:2.3.0")
    implementation("io.ktor:ktor-server-content-negotiation-jvm:2.3.0")
    implementation("io.ktor:ktor-serialization-kotlinx-json-jvm:2.3.0")
    implementation("io.ktor:ktor-server-auth-jvm:2.3.0")
    implementation("io.ktor:ktor-server-auth-jwt-jvm:2.3.0")
    implementation("io.ktor:ktor-client-core:2.3.4")
    implementation("io.ktor:ktor-client-cio:2.3.4")
    implementation("io.ktor:ktor-client-content-negotiation:2.3.4")
    implementation("io.ktor:ktor-serialization-kotlinx-json:2.3.4")
    implementation("org.jetbrains.exposed:exposed-core:0.41.1")
    implementation("org.jetbrains.exposed:exposed-dao:0.41.1")
    implementation("org.jetbrains.exposed:exposed-jdbc:0.41.1")
    implementation("com.zaxxer:HikariCP:5.0.1")
    implementation("mysql:mysql-connector-java:8.0.33")
    implementation("org.mindrot:jbcrypt:0.4")
    implementation("ch.qos.logback:logback-classic:1.4.7")
    testImplementation("io.ktor:ktor-server-tests-jvm:2.3.0")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:1.8.21")
    implementation("io.ktor:ktor-server-auth-jwt:2.x.x")
    implementation("at.favre.lib:bcrypt:0.9.0")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        jvmTarget = "17"
    }
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

tasks {
    // Nonaktifkan default jar agar Railway tidak mengambil jar salah
    named<Jar>("jar") {
        enabled = false
    }

    // Konfigurasi shadowJar agar hasilkan EventifyBackend-all.jar
    named<ShadowJar>("shadowJar") {
        archiveBaseName.set("EventifyBackend")
        archiveClassifier.set("all")       // <-- Penting agar Railway bisa mengenali file ini!
        archiveVersion.set("")             // <-- Tidak pakai "-0.0.1" di akhir
        mergeServiceFiles()
        manifest {
            attributes(mapOf("Main-Class" to "com.eventify.ApplicationKt"))
        }
    }

    // Pastikan perintah build juga menjalankan shadowJar
    build {
        dependsOn(shadowJar)
    }
}
