plugins {
    kotlin("jvm") version "1.5.10"
    id("org.jlleitschuh.gradle.ktlint") version "10.0.0"
    `java-library`
    `maven-publish`
    signing
}

group = "com.thomaskint"
version = "0.1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

apply(plugin = "org.jlleitschuh.gradle.ktlint-idea")

val kotestVersion = "4.6.1"

dependencies {
    implementation(platform("org.jetbrains.kotlin:kotlin-bom"))
    implementation("org.jetbrains.kotlin:kotlin-stdlib")

    implementation(platform("org.http4k:http4k-bom:4.11.0.1"))
    implementation("org.http4k:http4k-core")
    implementation("org.http4k:http4k-contract")

    testImplementation("org.jetbrains.kotlin:kotlin-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit")

    testImplementation("io.kotest:kotest-assertions-core-jvm:$kotestVersion")
}

base {
    archivesName.set("kalidate")
}

java {
    withJavadocJar()
    withSourcesJar()
}

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

//signing {
//    sign(publishing.publications["maven"])
//}

tasks.javadoc {
    if (JavaVersion.current().isJava9Compatible) {
        (options as StandardJavadocDocletOptions).addBooleanOption("html5", true)
    }
}
