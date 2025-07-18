# Creating an Asset

An Asset is the fundamental representation of an arbitrary backend interface in the EDC. The Data Provider registers it
with its Control Plane as a first step to expose it to the Dataspace via the Dataplane later on. This registration is
executed via the following Request:

```http request
POST /v3/assets HTTP/1.1
Host: https://provider-control.plane/api/management
X-Api-Key: password
Content-Type: application/json
```

```json
{
  "@context": {
    "@vocab": "https://w3id.org/edc/v0.0.1/ns/",
    "dct": "http://purl.org/dc/terms/"
  },
  "@type": "Asset",
  "@id": "{{ASSET_ID}}",
  "dataAddress": {
    "@type": "DataAddress",
    "type": "{{SUPPORTED_TYPE}}"
  },
  "properties": {
    "dct:type": {
      "@id": "https://my-namespa.ce/my-asset-type"
    }
  }
}
```

The `@id` parameter will identify the configured endpoint access permanently. This is the same id that a
data consumer will see when being presented the corresponding data offers when retrieving the [catalog](04_catalog.md).
However, there it won't be styled as an `edc:asset` but as a `dcat:Dataset`. Additionally, there is the possibility to
add `properties` to the Asset, which are exposed in the catalog to potential Data Consumers.
There are conventions in the Factory-X Dataspace how Data Providers should set properties. This enables Data Consumers
to decide what Data Offers they want to negotiate for. This matters especially when the Data Consumer has to add
URL-segements or HTTP bodies to its requests. The value entered as the Asset's `@id` will automatically be added as a
redundant `edc:id` property.

Most consequential however is the `dataAddress` section of the asset-APIs payload. It configures the Data Plane's
behavior. Depending on the protocol used for data exchange, an EDC will use different Data Planes. This is manifested by
the `type` property of the `dataAddress` object. There
may be arbitrary extensions extending the required parameters in the `dataAddress` section. That's why the following
examples are not complete but should rather be viewed as archetypes of established combinations of technologies.

The effects of each parameter will be explained by the following examples.

## HTTP Data Plane

The HTTP Data Plane of the EDC will proxy an HTTP request that a Data Consumer sends via HTTP. However, the incoming
request will be manipulated by the Data Plane - to what degree depends on the configuration.

```json
{
  "@context": {
    "@vocab": "https://w3id.org/edc/v0.0.1/ns/",
    "dct": "http://purl.org/dc/terms/"
  },
  "@type": "Asset",
  "@id": "{% uuid 'v4' %}",
  "properties": {
    "dct:type": {
      "@id": "{{ _.asset_type }}"
    }
  },
  "dataAddress": {
    "@type": "DataAddress",
    "type": "HttpData",
    "baseUrl": "https://mycorp.org/api",
    "oauth2:tokenUrl": "https://accounts.mycorp.org/path/oauth/token",
    "oauth2:clientId": "client-id",
    "oauth2:clientSecretKey": "client-secret-alias",
    "proxyQueryParams": "true",
    "proxyPath": "false",
    "proxyMethod": "true",
    "header:customHeaderKey": "custom-header-value"
  }
}
```

The following table shall explain a selection of the parameters. There's a whole lot more in the source code but these
have proven to enable an integration that's quite complete from a functional view.

| parameter                | description                                                                                                                                                                                                                                                                                                                                                                                                        | mandatory | default |
|--------------------------|--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|-----------|---------|
| `baseUrl`                | This parameter indicates the location of the backend data source. It's invisible to the Data Consumer and thus, the Data Plane will always resolve the contractId associated with the request's token to the baseUrl, forward the request and pass on the returned data. Thus, for the example above, a request to `https://data.plane` with a valid token attached will be forwarded to `https://mycorp.org/api`. | yes       | -       |
| `proxyPath`              | This string determines whether the Data Plane allows the Data Consumer to attach additional URL-segments to the request. Can be either `"true"` or `"false"`. If this parameter is set `"true"`, the a request `https://data.plane/resources/abcd` will be forwarded to `https://mycorp.org/api/resources/abcd`.                                                                                                   | no        | false   |
| `path`                   | If `proxyPath` is false, this parameter can be used to add an additional path to the request when it passes the Data Plane.                                                                                                                                                                                                                                                                                        | no        | null    |
| `proxyMethod`            | This string determines whether the Data Plane allows incoming requests to use HTTP-verbs that are not GET. Can be either `"true"` or `"false"`. If this parameter is set `"false"`, the Data Plane will rewrite `POST https://data.plane` to `GET https://mycorp.org/api`                                                                                                                                          | no        | false   |
| `method`                 | If `proxyMethod` is false, this parameter can be used to change the HTTP verb that the Http Data Plane will forward to the backend.                                                                                                                                                                                                                                                                                | no        | "GET"   |
| `proxyBody`              | This string determines whether the Data Plane forwards the Data Plane request's body to the backend. Can be either `"true"` or `"false"`.                                                                                                                                                                                                                                                                          | no        | false   |
| `body`                   | If `proxyBody` is false, this parameter can be used to set a fixed request body that the Http Data Plane will forward to the backend.                                                                                                                                                                                                                                                                              | no        | null    |
| `proxyQueryParams`       | This string determines whether the Data Plane forwards the headers that a request has attached. Thus, if `"true"`, a request `GET https://data.plane?q=z` will be rewritten to `https://mycorp.org/api?q=z`. Can be either `"true"` or `"false"`.                                                                                                                                                                  | no        | false   |
| `queryParams`            | Independently of `proxyQueryParams`, this string may include fixed headers that the Data Plane will attach to the incoming request and forward to the backend.                                                                                                                                                                                                                                                     | no        | null    |
| `oauth2:tokenUrl`        | If the backend is secured by an oauth2 authentication mechanism, the Data Plane will request an access token at this URL.                                                                                                                                                                                                                                                                                          | no        | null    |
| `oauth2:clientId`        | This is the clientId of the (technical) user that the credential was created for by the backend application.                                                                                                                                                                                                                                                                                                       | no        | null    |
| `oauth2:clientSecretKey` | The Data Provider must store his backend-issued client-secret in a Vault. The key under which the Data Plane can retrieve the secret's value is configured in this field.                                                                                                                                                                                                                                          | no        | null    |
| `header:customHeaderKey` | If the Data Provider wants to attach a static header to the request that the Provider Data Plane forwards to the backend, this can be achieved using the header name prefixed with `header:` and the constant value as the value.                                                                                                                                                                                  | no        | null    |

For all URLs that are registered (like the `tokenUrl` and the `baseUrl`) it is advisable to set them to a domain
controlled by the Data Provider himself. If the service is hosted by a Business Application Provider (like in a SaaS
scenario), that service should be redirected to through a proxy. That way, in a migration scenario, the existing Assets
can be preserved by reconfiguring the proxy to pointing to the new service.

## HTTP TLS Data Plane

Factory-X has added support for TLS authentication while doing HTTP to HTTP data transfer between Provider EDC and
Consumer EDC.
An EDC Provider can now create an asset with a base url which supports TLS authentication.

### `HttpTlsData` Data Address Type

For cases where a backend is protected by an mTLS authentication setup, there's a data address type `HttpTlsData` which
is an extension of existing data address type `HttpData`. Hence, `HttpTlsData` type along with TLS supports all existing
attributes of `HttpData` type such as custom authentication, additional headers etc.

### Management APIs Additions

- While creating an HTTP asset, we provide a source data address with `baseUrl`. If `baseUrl` which supports TLS, we
  just need to change data address type from `HttpData` to `HttpTlsData`.
  Below is an example of an HTTP TLS asset creation request.

```json
{
  "@context": {},
  "@id": "1",
  "properties": {
    "description": "EDC Demo Asset"
  },
  "dataAddress": {
    "@type": "DataAddress",
    "type": "HttpTlsData",
    "baseUrl": "https://jsonplaceholder.typicode.com/todos"
  }
}
```

- Similarly, While initiating transfer, we need to provide `"transferType": "HttpTlsData-PULL",`.

### TLS Configuration

For TLS authentication, we need to provide the server certificate during HTTP API call. We need to provide these via
configs to connector data plane server.
For each TLS host, we need to provide below config. Since it is a sensitive content, we can define empty values for
these configs and put the actual content into vault against same keys. Values in vault will take precedence over values
provided directly to connector data plane server.

```properties
fx.edc.http.tls.example.host=example.com
fx.edc.http.tls.example.certificate.type=PKCS12,
fx.edc.http.tls.example.certificate.content=<Base 64 encoded certificate file content>,
fx.edc.http.tls.example.certificate.password=<certificate file password>,
```

> If a TLS host is not registered via above configs and a data address has been defined with type `HttpTlsData`, it
> behaves like HttpData. Type will remain `HttpTlsData`, only behaviour will change.

## Notice

This work is licensed under the [CC-BY-4.0](https://creativecommons.org/licenses/by/4.0/legalcode).

- SPDX-License-Identifier: CC-BY-4.0
- SPDX-FileCopyrightText: 2023 Contributors of the Eclipse Foundation
- Source URL: [https://github.com/eclipse-tractusx/tractusx-edc](https://github.com/eclipse-tractusx/tractusx-edc)

- SPDX-License-Identifier: CC-BY-4.0
- SPDX-FileCopyrightText: 2025 Contributors of Factory-X
- Source
  URL: [https://github.com/factory-x-contributions/factoryx-edc](https://github.com/factory-x-contributions/factoryx-edc)