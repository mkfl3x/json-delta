plugins {
    kotlin("jvm") version "1.7.21"
    id("io.github.gradle-nexus.publish-plugin") version "1.1.0"
    id("maven-publish")
    id("signing")
}

group = "io.github.mkfl3x"
version = "1.4"

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.google.code.gson:gson:2.10.1")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.9.2")
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
                        name.set("MIT License")
                        url.set("https://opensource.org/license/mit/")
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

tasks.test {
    useJUnitPlatform()
}