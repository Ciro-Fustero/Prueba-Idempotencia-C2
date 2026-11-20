## Run

Java 25 is required (`./gradlew` uses the wrapper). GitHub Codespaces is optional (`docs/GUIDE.md`). Clone this course repository; you do not fork it to submit.

```bash
git clone https://github.com/Ciro-Fustero/Prueba-Idempotencia-C2.git
./gradlew check
./gradlew bootRun
```

`./gradlew check` fails on this starter until the tests in `ClickIdempotencyTests` are finished. The API listens on <http://localhost:8080>.

```bash
./gradlew test
./gradlew ktlintCheck
```

## Layout

```
src/main/kotlin/es/unizar/webeng/lab3/Application.kt
src/main/kotlin/es/unizar/webeng/lab3/analytics/Click.kt
src/main/kotlin/es/unizar/webeng/lab3/analytics/ClickConsumer.kt
src/main/kotlin/es/unizar/webeng/lab3/analytics/ClickRepositories.kt
src/main/kotlin/es/unizar/webeng/lab3/links/LinkController.kt
src/test/kotlin/es/unizar/webeng/lab3/ClickIdempotencyTests.kt
docs/GUIDE.md
```

The controller is already implemented. You complete the SETUP and VERIFY blocks in the tests.
