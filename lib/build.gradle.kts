plugins {
    kotlin("jvm") version "1.5.10"
    id("org.jlleitschuh.gradle.ktlint") version "10.0.0"
    id("org.jlleitschuh.gradle.ktlint-idea") version "10.0.0"
    `java-library`
    `maven-publish`
    signing
    jacoco
}

group = "com.thomaskint"
version = "0.1.0-SNAPSHOT"

repositories {
    mavenLocal()
    mavenCentral()
}

apply(plugin = "org.jlleitschuh.gradle.ktlint-idea")

val junitVersion = "5.7.1"
val kotestVersion = "4.6.1"
val jacocoVersion = "0.8.7"

dependencies {
    implementation(platform("org.jetbrains.kotlin:kotlin-bom"))
    implementation("org.jetbrains.kotlin:kotlin-stdlib")

    implementation(platform("org.http4k:http4k-bom:4.11.0.1"))
    implementation("org.http4k:http4k-core")
    implementation("org.http4k:http4k-contract")

    testImplementation(platform("org.junit:junit-bom:$junitVersion"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("org.junit.jupiter:junit-jupiter-params")

    testImplementation("io.kotest:kotest-assertions-core-jvm:$kotestVersion")
}

base {
    archivesName.set("kalidate")
}

java {
    withJavadocJar()
    withSourcesJar()
}

/**
 * -----------------------------------
 *       Configuration publish
 * -----------------------------------
 */
publishing {
    publications {
        create<MavenPublication>("maven") {
            artifactId = "kalidate"
            from(components["kotlin"])
            pom {
                name.set("kalidate")
                description.set("A concise description of my library")
                url.set("https://github.com/tkint/kalidate")
                licenses {
                    license {
                        name.set("ISC License")
                        url.set("https://opensource.org/licenses/ISC")
                    }
                }
                developers {
                    developer {
                        id.set("tkint")
                        name.set("Thomas Kint")
                        email.set("thomaskint.pro@gmail.com")
                    }
                }
                scm {
                    connection.set("scm:git:git://github.com/tkint/kalidate.git")
                    developerConnection.set("scm:git:ssh://github.com/tkint/kalidate.git")
                    url.set("https://github.com/tkint/kalidate")
                }
            }
        }
    }
    repositories {
        val nexusUsername = project.findProperty("nexusUsername")?.toString() ?: "notset"
        val nexusPassword = project.findProperty("nexusPassword")?.toString() ?: "notset"

        maven {
            name = "SonatypeStaging"
            url = uri("https://oss.sonatype.org/service/local/staging/deploy/maven2/")
            credentials {
                username = nexusUsername
                password = nexusPassword
            }
        }
        maven {
            name = "SonatypeSnapshot"
            url = uri("https://oss.sonatype.org/content/repositories/snapshots/")
            credentials {
                username = nexusUsername
                password = nexusPassword
            }
        }
    }
}

// signing {
//     sign(publishing.publications["maven"])
// }

tasks.javadoc {
    if (JavaVersion.current().isJava9Compatible) {
        (options as StandardJavadocDocletOptions).addBooleanOption("html5", true)
    }
}

/**
 * -----------------------------------
 *       Configuration tests
 * -----------------------------------
 */
jacoco {
    toolVersion = jacocoVersion
}

val failedTests = mutableListOf<String>()
tasks.test {
    useJUnitPlatform()
    testLogging {
        events("PASSED", "SKIPPED", "FAILED")
        exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
        showExceptions = true
        showCauses = true
    }
    addTestListener(
        object : TestListener {
            override fun beforeTest(p0: TestDescriptor?) = Unit
            override fun beforeSuite(p0: TestDescriptor?) = Unit
            override fun afterTest(desc: TestDescriptor, result: TestResult) {
                if (result.resultType == TestResult.ResultType.FAILURE) {
                    println(result.exception?.stackTrace)
                    val firstError = result.exception?.stackTrace?.first()
                    val error = "(${firstError?.fileName}:${firstError?.lineNumber})"
                    failedTests.add("${desc.className} > ${desc.name} > $error")
                }
            }

            override fun afterSuite(desc: TestDescriptor, result: TestResult) {
                printResults(desc, result)
            }
        }
    )
}

tasks.jacocoTestReport {
    val jacocoTestReport by tasks
    jacocoTestReport.dependsOn("test")

    excludeFiles(classDirectories)

    reports {
        xml.required.set(true)
        xml.outputLocation.set(file("$buildDir/reports/xml/coverage.xml"))
        html.required.set(true)
        html.outputLocation.set(file("$buildDir/reports/coverage"))
        csv.required.set(false)
    }

    jacocoTestReport.doLast {
        printCoverage("$buildDir/reports/coverage/index.html")
    }
}

tasks.jacocoTestCoverageVerification {
    violationRules {
        isFailOnViolation = true
        rule {
            // element = "CLASS"
            limit {
                minimum = "0".toBigDecimal()
            }
        }
    }

    excludeFiles(classDirectories)
}

tasks["check"].dependsOn(tasks["jacocoTestCoverageVerification"])
tasks["check"].dependsOn(tasks["jacocoTestReport"])
tasks.register("coverage") {
    doLast {
        printCoverage("$buildDir/reports/coverage/index.html")
    }
}

// fonctions propres
fun excludeFiles(classDirectories: ConfigurableFileCollection) {
    classDirectories.setFrom(
        files(
            classDirectories.files.map {
                fileTree(it).apply {
                    exclude("com/orange/kdore/Application*")
                    exclude("com/orange/kdore/Extensions*")
                    exclude("com/orange/kdore/utils/AppConf*")
                    exclude("com/orange/kdore/utils/CmdUtils*")
                    exclude("com/orange/kdore/utils/Logger*")
                    exclude("com/orange/kdore/client/requests/*")
                }
            }
        )
    )
}

fun printResults(desc: TestDescriptor, result: TestResult) {
    if (desc.parent != null) {
        val output = result.run {
            "Results: $resultType (" +
                "$testCount tests, " +
                "$successfulTestCount successes, " +
                "$failedTestCount failures, " +
                "$skippedTestCount skipped" +
                ")"
        }
        val testResultLine = "|  $output  |"
        val repeatLength = testResultLine.length
        val separationLine = "-".repeat(repeatLength)
        println(separationLine)
        println(testResultLine)
        println(separationLine)
    }
}

fun printCoverage(fileName: String) {
    val myFile = file(fileName)
    val value = if (!myFile.exists() || !myFile.isFile) {
        "(report file not found)"
    } else {
        val reg = "Total.*?([0-9]{1,3}).?%".toRegex()
        val content = myFile.readText()
        val found = reg.find(content)
        if (found != null && found.groups.size > 1 && found.groups[1] != null) {
            val result = found.groups[1]?.value?.toIntOrNull()
            if (result != null) {
                if (result >= 0) {
                    "$result %"
                } else {
                    "(incorrect value: $result)"
                }
            } else {
                "(value not found)"
            }
        } else {
            "(value not found)"
        }
    }
    val testResultLine = "| Total coverage: $value  |"
    val repeatLength = testResultLine.length
    val separationLine = "-".repeat(repeatLength)
    println(separationLine)
    println(testResultLine)
    println(separationLine)
}
