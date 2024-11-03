plugins {
    id("java")
    id("jacoco")
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
    finalizedBy(tasks.jacocoTestReport)
}

tasks.jacocoTestReport {
    dependsOn(tasks.test)
    reports {
        xml.required.set(false)
        csv.required.set(false)
        html.required.set(true)
    }
}

tasks.check {
    dependsOn(tasks.jacocoTestReport)
}

tasks.jar {
    archiveFileName.set("my-app.jar")
    manifest {
        attributes["Main-Class"] = "org.example.Main"
    }
    from(configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) })
    duplicatesStrategy = DuplicatesStrategy.INCLUDE
}

tasks.register("prepareNetlifyDist") {
    doLast {
        val targetDir = file("${buildDir}/netlify_dist")
        delete(targetDir)
        mkdir(targetDir)

        // Copy Javadoc
        copy {
            from("${buildDir}/docs/javadoc")
            into(targetDir)
        }

        // Copy Jacoco reports
        copy {
            from("${buildDir}/reports/jacoco/test/html")
            into(targetDir.resolve("coverage"))
        }
    }
}

tasks.named("javadoc").configure {
    finalizedBy("prepareNetlifyDist")
}

tasks.named("jacocoTestReport").configure {
    finalizedBy("prepareNetlifyDist")
}
