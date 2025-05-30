# Data Plane

The FactoryX-EDC consists of a **Control Plane** and a **Data Plane** Application.
While the **Control Plane** managing several data transfers, the **Data Plane** is responsible for doing the actual
transfer. Like this data is never routed through the control plane itself and must always pass the data plane.

## Security

### Confidential Settings

Please be aware that there are several confidential settings, that should not be part of the actual EDC configuration
file (e.g. the Vault credentials).

As it is possible to configure EDC settings via environment variables, one way to do it would be via Kubernetes Secrets.
For other deployment scenarios than Kubernetes equivalent measures should be taken.

## Known Data Plane Issues

Please have a look at the open issues in the upstream open source
repository:
- [EDC Connector](https://github.com/eclipse-edc/Connector/issues)
- [TractusX-EDC](https://github.com/eclipse-tractusx/tractusx-edc/issues)
