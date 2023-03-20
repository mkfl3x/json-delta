import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.7.21"
    id("io.github.gradle-nexus.publish-plugin") version "1.1.0"
    id("maven-publish")
    id("signing")
}

group = "io.github.mkfl3x"
version = "0.1-beta"

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.google.code.gson:gson:2.10.1")
    testImplementation(kotlin("test"))
}

java {
    withJavadocJar()
    withSourcesJar()
}

publishing {
    publications {
        register<MavenPublication>("publish") {
            from(components["java"])
            pom {
                name.set(project.name)
                url.set("https://github.com/mkfl3x/json-delta")
                description.set("Library for comparing json objects. Gson based")
                developers {
                    developer {
                        name.set("mkfl3x")
                    }
                }
                licenses {
                    license {
                        name.set("Apache 2.0")
                        url.set("https://www.apache.org/licenses/LICENSE-2.0")
                    }
                }
                scm {
                    url.set("https://github.com/mkfl3x/json-delta")
                }
            }
        }
    }
}

nexusPublishing {
    repositories {
        sonatype {
            stagingProfileId.set(System.getenv("SONATYPE_STAGING_PROFILE_ID"))
            username.set(System.getenv("SONATYPE_USERNAME"))
            password.set(System.getenv("SONATYPE_PASSWORD"))
            nexusUrl.set(uri("https://s01.oss.sonatype.org/service/local/"))
            snapshotRepositoryUrl.set(uri("https://s01.oss.sonatype.org/content/repositories/snapshots/"))
        }
    }
}

signing {
    useInMemoryPgpKeys(
        System.getenv("PGP_KEY_ID"),
        System.getenv("PGP_SECRET_KEY"),
        System.getenv("PGP_PASSPHRASE")
    )
    sign(publishing.publications)
}

tasks.jar {
    manifest.attributes["Main-Class"] = "JsonDelta"
    from(configurations.runtimeClasspath.get().map(::zipTree))
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}