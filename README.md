# Factory-X EDC (Eclipse Dataspace Connector)

[![Contributors][contributors-shield]][contributors-url]
[![Stargazers][stars-shield]][stars-url]
[![Latest Release][release-shield]][release-url]

[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=factory-x-contributions_factoryx-edc&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=factory-x-contributions_factoryx-edc)

Container images and deployments of the Eclipse Dataspace Components for the Factory-X project.

Please also refer to:

- [Our docs](https://github.com/eclipse-tractusx/tractusx-edc/tree/main/docs)
- [Our Releases](https://github.com/eclipse-tractusx/tractusx-edc/releases)
- [Eclipse Dataspace Components](https://github.com/eclipse-edc/Connector)
- [Eclipse Tractus-X](https://github.com/eclipse-tractusx/tractusx-edc)
- [Report Bug / Request Feature]

## About The Project

The project is an extension for the [Eclipse Tractus-X](https://github.com/eclipse-tractusx/tractusx-edc) for the Manufacturing sector and is based on
 [Eclipse DataSpaceConnector Project](https://github.com/eclipse-edc/Connector).

## Inventory

The eclipse data space connector is split up into Control-Plane and Data-Plane, whereas the Control-Plane functions as
administration layer and has responsibility of resource management, contract negotiation and administer data transfer.
The Data-Plane does the heavy lifting of transferring and receiving data streams.

Depending on your environment there are different derivatives of the control-plane prepared:

- [edc-controlplane-postgresql-hashicorp-vault](edc-controlplane/edc-controlplane-postgresql-hashicorp-vault) with
  dependency onto
  - [Hashicorp Vault](https://www.vaultproject.io/)
  - [PostgreSQL 8.2 or newer](https://www.postgresql.org/)

Derivatives of the Data-Plane can be found here

- [edc-dataplane-hashicorp-vault](edc-dataplane/edc-dataplane-hashicorp-vault) with dependency onto
  - [Hashicorp Vault](https://www.vaultproject.io/)

For testing/development purposes:

- [edc-runtime-memory](edc-controlplane/edc-runtime-memory)

## Getting Started

### Build

Build Factory-X EDC together with its Container Images

```shell
./gradlew dockerize
```

## License

Distributed under the Apache 2.0 License.
See [LICENSE](https://github.com/factory-x-contributions/factoryx-edc/blob/main/LICENSE) for more information.

<!-- MARKDOWN LINKS & IMAGES -->
<!-- https://www.markdownguide.org/basic-syntax/#reference-style-links -->

[contributors-shield]: https://img.shields.io/github/contributors/factory-x-contributions/factoryx-edc.svg?style=for-the-badge

[contributors-url]: 

[stars-shield]: https://img.shields.io/github/stars/factory-x-contributions/factoryx-edc.svg?style=for-the-badge

[stars-url]: https://github.com/factory-x-contributions/factoryx-edc/stargazers

[license-shield]: https://img.shields.io/github/license/factory-x-contributions/factoryx-edc.svg?style=for-the-badge

[license-url]: https://github.com/factory-x-contributions/factoryx-edc/blob/main/LICENSE

[release-shield]: https://img.shields.io/github/v/release/factory-x-contributions/factoryx-edc.svg?style=for-the-badge

[release-url]: https://github.com/factory-x-contributions/factoryx-edc/releases
