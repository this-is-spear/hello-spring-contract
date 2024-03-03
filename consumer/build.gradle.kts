import org.gradle.api.tasks.testing.logging.TestExceptionFormat

plugins{
    id("maven-publish")
}

group="tis.consumer"
version = "0.0.1-SNAPSHOT"


dependencies {
    testImplementation("org.springframework.cloud:spring-cloud-starter-contract-stub-runner")
    testImplementation("org.springframework.cloud:spring-cloud-contract-spec-kotlin")
}


tasks {
    test {
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
