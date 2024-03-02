import org.gradle.api.tasks.testing.logging.TestExceptionFormat

plugins{
    id("org.springframework.cloud.contract") version "4.1.1"
    id("maven-publish")
}

group = "tis.producer"
version = "0.0.1-SNAPSHOT"

dependencies {
    testImplementation("org.springframework.cloud:spring-cloud-starter-contract-verifier")
    testImplementation("org.springframework.cloud:spring-cloud-contract-spec-kotlin")
}

contracts {
    contractsDslDir.set(file("src/test/resources/contracts"))
    testFramework.set(org.springframework.cloud.contract.verifier.config.TestFramework.JUNIT5)
    packageWithBaseClasses.set("tis.producer")
}

tasks.withType<Delete> {
    doFirst {
        delete("~/.m2/repository/tis/producer/")
    }
}

tasks {
    contractTest {
        useJUnitPlatform()
        systemProperty("spring.profiles.active", "gradle")
        testLogging {
            exceptionFormat = TestExceptionFormat.FULL
        }
        afterSuite(KotlinClosure2({ desc: TestDescriptor, result: TestResult ->
            if (desc.parent == null) {
                if (result.testCount == 0L) {
                    throw IllegalStateException("No tests were found. Failing the build")
                }
                else {
                    println("Results: (${result.testCount} tests, ${result.successfulTestCount} successes, ${result.failedTestCount} failures, ${result.skippedTestCount} skipped)")
                }
            } else { /* Nothing to do here */ }
        }))
    }
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            artifact(tasks.named("bootJar"))
            artifact(tasks.named("verifierStubsJar"))
            versionMapping {
                usage("java-api") {
                    fromResolutionOf("runtimeClasspath")
                }
                usage("java-runtime") {
                    fromResolutionResult()
                }
            }
        }
    }
}
