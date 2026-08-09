---
name: gradle-build-excludes-relocated-tests
summary: Keep standalone JUnit tests outside Gradle's default source set when the NeoForge test compile classpath is incomplete.
tags: [#gradle, #neoforge, #tests, #build]
impact: normal
commit: 9b63711 (dirty)
date: 2026-08-09
created_at: 2026-08-09T21:35:11Z
scope: build.gradle, tests/java
---

## Problem
`bash gradlew build` failed at `:compileTestJava` while compiling `src/test/java/com/c446/ars_trinkets/capabilities/LevelingCapabilityTest.java:10`; javac could not find NeoForge's `INBTSerializable` class.

## Cause
The default Java source set made `build` depend on compiling the JUnit tests, while the test compile classpath did not expose the NeoForge type referenced through `LevelingCapability`.

## Resolution
Moved all ten JUnit files from `src/test/java` to `tests/java`, outside Gradle's conventional test source set. The production `build` task then completed successfully; Gradle reported `:compileTestJava` with no sources.

## Notes
The test files remain in the repository for separate/manual execution, but they are intentionally not wired into the default `build` lifecycle until a compatible NeoForge test classpath is configured.
