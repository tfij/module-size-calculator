plugins {
    id("java")
    checkstyle
    jacoco

    // release
    `maven-publish`
    signing
    id("pl.allegro.tech.build.axion-release") version "1.18.16"
}

group = "pl.tfij"
project.version = scmVersion.version

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.11.3"))
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

// release
java {
    withJavadocJar()
    withSourcesJar()
}
tasks.getByName<Javadoc>("javadoc") {
    if(JavaVersion.current().isJava9Compatible) {
        (options as? StandardJavadocDocletOptions)?.addBooleanOption("html5", true)
    }
}
publishing {
    publications {
        create<MavenPublication>("sonatype") {
            artifactId = "module-size-calculator"
            from(components.getByName("java"))
            versionMapping {
                usage("java-api") {
                    fromResolutionOf("runtimeClasspath")
                }
                usage("java-runtime") {
                    fromResolutionResult();
                }
            }
            pom {
                name.set("module-size-calculator")
                description.set("The Module Size Calculator is a Java library designed for analyzing the size of modules within a project based on lines of code (LOC). It provides functionalities to calculate and visualize the distribution of code among different modules, helping developers understand the codebase's structure and identify potential areas for optimization or refactoring.")
                url.set("https://github.com/tfij/module-size-calculator")
                licenses {
                    license {
                        name.set("The Apache License, Version 2.0")
                        url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                    }
                }
                developers {
                    developer {
                        id.set("tfij")
                        name.set("Tomasz Fijakowski")
                        email.set("tomasz@chi.pl")
                    }
                }
                scm {
                    connection.set("scm:git:https://github.com/tfij/module-size-calculator.git")
                    developerConnection.set("scm:git:https://github.com/tfij/module-size-calculator.git")
                    url.set("https://github.com/tfij/module-size-calculator.git")
                }
            }
        }
    }
    repositories {
        maven {
            val releasesRepoUrl = uri("https://oss.sonatype.org/service/local/staging/deploy/maven2/")
            val snapshotsRepoUrl = uri("https://oss.sonatype.org/content/repositories/snapshots/")
            url = if (version.toString().endsWith("SNAPSHOT")) snapshotsRepoUrl else releasesRepoUrl
            credentials {
                username = System.getenv("SONATYPE_USERNAME")
                password = System.getenv("SONATYPE_PASSWORD")
            }
        }
    }
}
if (System.getenv().containsKey("SIGNING_GPG_KEY_ID")) {
    signing {
        useInMemoryPgpKeys(
            System.getenv("SIGNING_GPG_KEY_ID"),
            System.getenv("SIGNING_GPG_PRIVATE_KEY"),
            System.getenv("SIGNING_GPG_PRIVATE_KEY_PASSWORD")
        )
        sign(publishing.publications.getByName("sonatype"))
    }
}
