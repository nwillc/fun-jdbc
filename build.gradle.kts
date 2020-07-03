/*
 * Copyright (c) 2016, nwillc@gmail.com
 *
 * Permission to use, copy, modify, and/or distribute this software for any
 * purpose with or without fee is hereby granted, provided that the above
 * copyright notice and this permission notice appear in all copies.
 *
 * THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES
 * WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR
 * ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
 * WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
 * ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF
 * OR IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
 */

import com.jfrog.bintray.gradle.tasks.BintrayUploadTask
import com.jfrog.bintray.gradle.BintrayExtension.PackageConfig

plugins {
    java
    idea
    `maven-publish`
    jacoco
    pmd
    id("com.github.nwillc.vplugin") version "3.0.5"
    id("com.jfrog.bintray") version "1.8.5"
    id("org.ajoberstar.git-publish") version "3.0.0-rc.1"
}

group = "com.github.nwillc"
version = "1.0.1-SNAPSHOT"

val archivesBaseName = "fun-jdbc"
val almostFunctionalVersion = "1.9.7"
val assertj_version = "3.9.1"
val embedded_db_junit_version = "2.0.0"
val jdk_contract_version = "2.0.0"
val jmockit_version = "1.39"
val junit_version = "5.7.0-M1"
val tinylog_version = "1.3.6"
val funkjdbc_test_version = "0.12.0"
val publication_name = "maven"

repositories {
    jcenter()
}

dependencies {
    implementation("com.github.nwillc:almost-functional:$almostFunctionalVersion")
    implementation("org.tinylog:tinylog:$tinylog_version")

    testImplementation("org.junit.jupiter:junit-jupiter:$junit_version")
    testImplementation("org.assertj:assertj-core:$assertj_version")
    testImplementation("org.jmockit:jmockit:$jmockit_version")
    testImplementation("org.zapodot:embedded-db-junit:$embedded_db_junit_version")

    testImplementation("com.github.nwillc:jdk_contract_tests:$jdk_contract_version")
}

val sourceJar by tasks.registering(Jar::class) {
    archiveClassifier.set("sources")
    from(sourceSets.main.get().allSource)
}

val javadocJar by tasks.registering(Jar::class) {
    dependsOn("javadoc")
    archiveClassifier.set("javadoc")
    from("build/docs/javadoc")
}

publishing {
    publications {
        create<MavenPublication>(publication_name) {
            groupId = "${project.group}"
            artifactId = project.name
            version = "${project.version}"

            artifact(sourceJar.get())
            artifact(javadocJar.get())
            from(components["java"])
        }
    }
}

bintray {
    user = System.getenv("BINTRAY_USER")
    key = System.getenv("BINTRAY_API_KEY")
    dryRun = false
    publish = true
    setPublications(publication_name)
    pkg(
        delegateClosureOf<PackageConfig> {
            repo = publication_name
            name = project.name
            desc = "Java 8 functional JDBC utility code, applying some of Java\\'s newer features to reduce JDBC boilerplate code"
            websiteUrl = "https://github.com/nwillc/fun-jdbc"
            issueTrackerUrl = "https://github.com/nwillc/fun-jdbc/issues"
            vcsUrl = "https://github.com/nwillc/fun-jdbc.git"
            version.vcsTag = "v${project.version}"
            setLicenses("ISC")
            setLabels("jdk8", "JDBC")
            publicDownloadNumbers = true
        }
    )
}

gitPublish {
    repoUri.set("git@github.com:nwillc/fun-jdbc.git")
    branch.set("gh-pages")
    repoDir.set(File("$buildDir/somewhereelse"))
    contents {
        from("src/main/pages")
        from("build/docs/javadoc") {
            into("javadoc")
        }
    }
}

tasks {
    withType<Test> {
        useJUnitPlatform()
        testLogging {
            showStandardStreams = true
            events("passed", "failed", "skipped")
        }
    }
    withType<JacocoReport> {
        dependsOn("test")
        reports {
            xml.isEnabled = true
            html.isEnabled = true
        }
    }
    withType<BintrayUploadTask> {
        onlyIf {
            if (project.version.toString().contains('-')) {
                logger.lifecycle("Version ${project.version} is not a release version - skipping upload.")
                false
            } else {
                true
            }
        }
    }
}
