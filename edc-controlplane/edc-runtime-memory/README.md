# EDC Control-Plane backed by In-Memory Stores

## Security

https://github.com/eclipse-tractusx/tractusx-edc/blob/main/edc-controlplane/edc-runtime-memory/README.md#security
## Building

```shell
./gradlew :edc-controlplane:edc-runtime-memory:dockerize
```

## Configuration

https://github.com/eclipse-tractusx/tractusx-edc/blob/main/edc-controlplane/edc-runtime-memory/README.md#configuration
## Running

```shell
docker run \
    -e EDC_VAULT_SECRETS="key1:secret1;key2:secret2" \
    -p 8080:8080 -p 8181:8181 -p 8282:8282 -p 9090:9090 -p 9999:9999 \
    -v ${CONFIGURATION_PROPERTIES_FILE:-/dev/null}:/app/configuration.properties \
    -v ${LOGGING_PROPERTIES_FILE:-/dev/null}:/app/logging.properties \
    -i edc-runtime-memory:latest
```
