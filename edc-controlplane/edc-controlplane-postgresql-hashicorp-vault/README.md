# EDC Control-Plane PostgreSQL & Hashicorp Vault

This version of the EDC Control-Plane is backed by [PostgreSQL](https://www.postgresql.org/) and [HashiCorp Vault](https://www.vaultproject.io/docs).

## Building

```shell
./gradlew :edc-controlplane:edc-controlplane-postgresql-hashicorp-vault:dockerize
```

## Configuration

https://github.com/eclipse-tractusx/tractusx-edc/blob/main/edc-controlplane/edc-controlplane-postgresql-hashicorp-vault/README.md

## Running

```shell
docker run \
  -p 8080:8080 -p 8181:8181 -p 8282:8282 -p 9090:9090 -p 9999:9999 \
  -v ${CONFIGURATION_PROPERTIES_FILE:-/dev/null}:/app/configuration.properties \
  -v ${LOGGING_PROPERTIES_FILE:-/dev/null}:/app/logging.properties \
  -v ${OPENTELEMETRY_PROPERTIES_FILE:-/dev/null}:/app/opentelemetry.properties \
  -i edc-controlplane-postgresql-hashicorp-vault:latest
```
