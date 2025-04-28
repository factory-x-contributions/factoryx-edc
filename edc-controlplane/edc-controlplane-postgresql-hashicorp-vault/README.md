# EDC Control-Plane PostgreSQL & Hashicorp Vault
This version of the EDC Control-Plane is backed by [PostgreSQL](https://www.postgresql.org/) and [HashiCorp Vault](https://www.vaultproject.io/docs).

## Building
```shell
./gradlew :edc-controlplane:edc-controlplane-postgresql-hashicorp-vault:dockerize
```

## Configuration
Details regarding each configuration property can be found in the [docs for the chart](../../charts/factoryx-connector/README.md).

Please note that the properties list may not be complete as the factoryx-edc may elect to fall back to the default behavior of an extension.
When in doubt, check the extensions' README that will likely be found at below places.
- [FactoryX-EDC Extensions](../../edc-extensions)
- [Tractusx-EDC Extensions](https://github.com/eclipse-tractusx/tractusx-edc/tree/main/edc-extensions)
- [Eclipse Connector Extensions](https://github.com/eclipse-edc/Connector/tree/main/extensions)

## Running
```shell
docker run \
  -p 8080:8080 -p 8181:8181 -p 8282:8282 -p 9090:9090 -p 9999:9999 \
  -v ${CONFIGURATION_PROPERTIES_FILE:-/dev/null}:/app/configuration.properties \
  -v ${LOGGING_PROPERTIES_FILE:-/dev/null}:/app/logging.properties \
  -v ${OPENTELEMETRY_PROPERTIES_FILE:-/dev/null}:/app/opentelemetry.properties \
  -i edc-controlplane-postgresql-hashicorp-vault:latest
```
