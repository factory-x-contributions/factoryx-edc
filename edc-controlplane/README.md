# Control Plane

The FactoryX-EDC consists of a **Control Plane** and a **Data Plane** Application.
While the **Data Plane** handles the actual Data Transfer, the **Control Plane** is responsible for:

- Resource Management (e.g. Assets, Policies & Contract Definitions CRUD)
- Contract Offering & Contract Negotiation
- Data Transfer Coordination / Management

The only API that is protected by some kind of security mechanism is the Data Management API. At the time of writing
this is done by a simple API key.The key value must be configured in `edc.api.auth.key`. All requests to the Data
Management API must have `X-Api-Key` header with the key value.

Example:

```bash
curl -X GET <URL> --header "X-Api-Key: <edc.api.auth.key>"
```

## Security

### Confidential Settings

Please be aware that there are several confidential settings, that should not be part of the actual EDC configuration
file.

Some of these confidential settings are

- Vault credentials
- Data Management API key
- Wallet credentials
- Database credentials

As it is possible to configure EDC settings via environment variables, one way to do it would be via Kubernetes Secrets.
For other deployment scenarios than Kubernetes equivalent measures should be taken.

## Known Control Plane Issues

Please have a look at the open issues in the upstream open source
repository:
- [EDC Connector](https://github.com/eclipse-edc/Connector/issues)
- [TractusX-EDC](https://github.com/eclipse-tractusx/tractusx-edc/issues)
