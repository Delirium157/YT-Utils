plugins {
    id("java")
}

group = "net.delirium"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    // Source: https://mvnrepository.com/artifact/com.h2database/h2
    implementation("com.h2database:h2:2.4.240")
}

tasks.test {
    useJUnitPlatform()
}