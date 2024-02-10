plugins {
    id("java")
    checkstyle
}

group = "pl.tfij"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    checkstyle("pl.tfij:check-tfij-style:1.5.1")
}

tasks.test {
    useJUnitPlatform()
}
checkstyle {
    toolVersion = "10.13.0"
    sourceSets = listOf(project.sourceSets.main.orNull)
}
