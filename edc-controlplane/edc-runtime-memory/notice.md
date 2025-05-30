# Notice for Docker image
An EDC Control Plane + Data Plane using memory-based storage, and Azure KeyVault as secret store.

GitHub Container Registry: <https://ghcr.io/factory-x-contributions/edc-runtime-memory>

Factory-X product(s) installed within the image:

## Factory-X EDC Control Plane
- GitHub: <https://github.com/factory-x-contributions/factoryx-edc>
- Dockerfile: <https://github.com/factory-x-contributions/factoryx-edc/blob/main/resources/Dockerfile>
- Project license: [Apache License, Version 2.0](https://github.com/factory-x-contributions/factoryx-edc/blob/main/LICENSE)

## Factory-X EDC Data Plane
- GitHub: <https://github.com/factory-x-contributions/factoryx-edc>
- Dockerfile: <https://github.com/factory-x-contributions/factoryx-edc/blob/main/resources/Dockerfile>
- Project license: [Apache License, Version 2.0](https://github.com/factory-x-contributions/factoryx-edc/blob/main/LICENSE)

## Used base image
- [eclipse-temurin:23_37-jre-alpine](https://github.com/adoptium/containers)
- Official Eclipse Temurin DockerHub page: <https://hub.docker.com/_/eclipse-temurin>
- Eclipse Temurin Project: <https://projects.eclipse.org/projects/adoptium.temurin>
- Additional information about the Eclipse Temurin
  images: <https://github.com/docker-library/repo-info/tree/master/repos/eclipse-temurin>

## Third-Party Software
- OpenTelemetry Agent v1.32.0: <https://github.com/open-telemetry/opentelemetry-java-instrumentation/releases/tag/v1.32.0>

As with all Docker images, these likely also contain other software which may be under other licenses (such as Bash, etc
from the base distribution, along with any direct or indirect dependencies of the primary software being contained).

As for any pre-built image usage, it is the image user's responsibility to ensure that any use of this image complies
with any relevant licenses for all software contained within.
