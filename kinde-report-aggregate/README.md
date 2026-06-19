# kinde-report-aggregate

This module is **not a library**. It produces nothing that consumers of the
Kinde Java SDK ever depend on. Its only job is to merge per-module
[JaCoCo](https://www.jacoco.org/jacoco/) coverage data into a single
aggregate report that the CI pipeline uploads.

## Why a dedicated module?

JaCoCo's `report-aggregate` goal can only read `jacoco.exec` files from
modules that the aggregator module **depends on**. There's no way to do
this from the root parent pom alone (the parent pom doesn't have
dependencies). So the canonical JaCoCo recipe — followed here — is to
create a tiny leaf module whose `pom.xml`:

1. depends on every module you want covered, and
2. binds `jacoco:report-aggregate` to the `verify` phase.

The depended-on modules in this case are:

- `kinde-core`
- `kinde-j2ee`
- `kinde-management`
- `kinde-springboot-core`

The pom layout, the EPL header, and the structure all come from the
[official JaCoCo example](https://github.com/jacoco/jacoco/tree/master/jacoco-maven-plugin.test/it/it-report-aggregate)
maintained by the JaCoCo authors (Mountainminds GmbH).

## How to run it

`mvn verify` from the repo root runs the full multi-module build and, as
part of the `verify` phase on this module, produces:

```
kinde-report-aggregate/target/site/jacoco-aggregate/
    index.html      ← human-readable coverage report
    jacoco.xml      ← machine-readable, consumed by CI
```

## How CI uses the output

`.github/workflows/maven.yml` uploads `jacoco.xml` to the coverage service
(Codecov):

```yaml
files: 'kinde-report-aggregate/target/site/jacoco-aggregate/jacoco.xml'
```

That single file represents the merged coverage of every production module
in the repo.

## Why is there an empty `ReportTest.java`?

Maven's `test` phase produces the per-module `jacoco.exec` file via the
JaCoCo agent (configured in the parent pom). If a module has zero tests,
no `jacoco.exec` is written for it, and the surefire phase can in some
configurations skip the JaCoCo agent attachment entirely. A single empty
test guarantees the lifecycle runs end-to-end on this module. It is
intentional, not dead code.

## Maintenance tips

- **Adding a new production module that should count toward coverage:**
  add it as a `<dependency>` in this pom.
- **The artifactId is `report`, not `kinde-report-aggregate`.** That's
  a JaCoCo-recipe convention; the root pom's
  `<excludeArtifact>report</excludeArtifact>` (used by other plugins to
  skip this non-deployable module) relies on that exact name. If you ever
  rename it, update both places.
- **This module is excluded from Sonatype Central publishing** via the
  `central-publishing-maven-plugin` `<skip>true</skip>` configuration in
  its pom — it is intentionally never published to Maven Central.
