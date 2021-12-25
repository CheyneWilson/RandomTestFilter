plugins {
    java
    signing
    `maven-publish`
    id("fr.brouillard.oss.gradle.jgitver") version "0.9.1"
}

group = "nz.cheyne.junit"

jgitver {
    useDirty = true
    nonQualifierBranches = "main"
}


repositories {
    mavenCentral()
}

dependencies {
    implementation("org.junit.platform:junit-platform-launcher:1.8.2")

    implementation("org.slf4j:jul-to-slf4j:1.7.32")
    implementation("org.slf4j:slf4j-api:1.7.32")
    implementation("ch.qos.logback:logback-classic:1.2.9")

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.2")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.2")

}

tasks.withType<Test> {
    useJUnitPlatform()

    // Did not class-randomize with junit 5.7.0... it worked with 5.8.2
    testLogging {
        events("FAILED", "PASSED", "SKIPPED")
    }
}



tasks.create<Test>("randomTest") {
    group = "verification"
    systemProperty("nz.cheyne.junit.test.limit", 2)

    // Set the random seed for JUnit and the RandomTestFilter.
    val seed: String = System.getProperty("junit.jupiter.execution.order.random.seed") ?: kotlin.random.Random.nextLong().toString()
    systemProperty("junit.jupiter.execution.order.random.seed", seed)
    // Randomize the order that test classes are run.
    systemProperty("junit.jupiter.testclass.order.default", "org.junit.jupiter.api.ClassOrderer\$Random")
    // Randomize the order that individual tests within a class are run
    systemProperty("junit.jupiter.testmethod.order.default", "org.junit.jupiter.api.MethodOrderer\$Random")

    // Log the seeds. Note, this is disabled because we also log the same seed in the filter.
    // systemProperty("java.util.logging.config.file", file("src/test/resources/logging.properties"))

    include("**/Dummy**")

    // As convenience, we can set and log the seed in the task.
    doFirst {
        logger.quiet("junit.jupiter.execution.order.random.seed is {}", seed)
    }
}

java {
    withSourcesJar()
    withJavadocJar()
}

val ossrhPassword: String? by project
val ossrhUsername: String? by project

publishing {
    repositories {
        maven {
            name = "Snapshot"
            setUrl("https://s01.oss.sonatype.org/content/repositories/snapshots")
            credentials {
                username = ossrhUsername
                password = ossrhPassword
            }
        }

        maven {
            name = "MavenCentral"
            setUrl("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/")
            credentials {
                username = ossrhUsername
                password = ossrhPassword
            }
        }
    }

    publications {
        create<MavenPublication>("randomTestFilter") {
            from(components["java"])
            pom {
                name.set("Random Test Filter")
                description.set("A filter to run a randomized subset of JUnit tests.")
                url.set("https://cheyne.nz/random-test-filter/")
                licenses {
                    license {
                        name.set("MIT Licence")
                        url.set("https://github.com/CheyneWilson/RandomTestFilter/blob/main/LICENSE")
                    }
                }
                developers {
                    developer {
                        id.set("cheynewilson")
                        name.set("Cheyne Wilson")
                        email.set("dev+random-test-filter@cheyne.nz")
                        url.set("https://cheyne.nz")
                    }
                }
                scm {
                    connection.set("scm:git:git://github.com/cheynewilson/random-test-filter.git")
                    developerConnection.set("scm:git:ssh://github.com/cheynewilson/random-test-filter.git")
                    url.set("https://github.com/cheynewilson/random-test-filter")
                }
            }
        }
    }
}

val signingKeyId: String? by project
val signingKey: String? by project
val signingPassword: String? by project

signing {
    useInMemoryPgpKeys(signingKeyId, signingKey, signingPassword)
    sign(publishing.publications["randomTestFilter"])
}
