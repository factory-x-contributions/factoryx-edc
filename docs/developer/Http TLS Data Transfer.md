# HTTP TLS Data Transfer
Factory-X has added support for TLS authentication while doing HTTP to HTTP data transfer between Provider EDC and Consumer EDC.
An EDC Provider can now create an asset with a base url which supports TLS authentication.

## `HttpTlsData` Data Address Type
Factory-X defines a new data address type `HttpTlsData` which is an extension of existing data address type `HttpData`. Hence, `HttpTlsData` type along with TLS supports all existing attributes of `HttpData` type such as custom authentication, additional headers etc.

## Management APIs Changes
- While creating an HTTP asset, we provide a source data address with `baseUrl`. If `baseUrl` which supports TLS, we just need to change data address type from `HttpData` to `HttpTlsData`.
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

## TLS Configuration
For TLS authentication, we need to provide the server certifcate during HTTP API call. We need to provide these via configs to connector data plane server.
For each TLS host, we need to provide below config. Since it is a sensitive content, we can define empty values for these configs and put the actual content into vault against same keys. Values in vault will take precedence over values provided directly to connector data plane server.
```properties
fx.edc.http.tls.example.host=example.com
fx.edc.http.tls.example.certificate.type=PKCS12,
fx.edc.http.tls.example.certificate.content=<Base 64 encoded certificate file content>,
fx.edc.http.tls.example.certificate.password=<certificate file password>,
```
> If a TLS host is not registered via above configs and a data address has been defined with type `HttpTlsData`, it behaves like HttpData. Type will remain `HttpTlsData`, only behaviour will change.

