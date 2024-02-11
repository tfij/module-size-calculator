plugins {
    id("java")
    checkstyle
    jacoco
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

// checkstyle
checkstyle {
    toolVersion = "10.13.0"
    sourceSets = listOf(project.sourceSets.main.orNull)
}

// jacoco test coverage
tasks.test {
    finalizedBy(tasks.jacocoTestReport) // report is always generated after tests run
}
tasks.jacocoTestReport {
    dependsOn(tasks.test) // tests are required to run before generating the report
}
tasks.jacocoTestReport {
    reports {
        xml.required.set(true)
        csv.required.set(false)
        html.required.set(true)
    }
}
