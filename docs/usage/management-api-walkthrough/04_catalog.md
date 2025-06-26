# Fetching a Provider's Catalog

The catalog API is the first request in this sequence that passes through the Dataspace. It is executed by the Data
Consumer against their own Control Plane and triggers the retrieval of a catalog from a specified Data Provider. The
request
looks like this:

```http request
POST /v3/catalog/request HTTP/1.1
Host: https://consumer-control.plane/api/management
X-Api-Key: password
Content-Type: application/json
```

```json
{
  "@context": {
    "@vocab": "https://w3id.org/edc/v0.0.1/ns/",
    "odrl": "http://www.w3.org/ns/odrl/2/"
  },
  "@type": "CatalogRequest",
  "counterPartyId": "web:did:data-provider.com:dataspace",
  "counterPartyAddress": "https://provider-control.plane/api/v1/dsp",
  "protocol": "dataspace-protocol-http",
  "querySpec": {
    "@type": "QuerySpec",
    "offset": 0,
    "limit": 50,
    "sortField": "http://purl.org/dc/terms/type",
    "sortOrder": "ASC",
    "filterExpression": [
      {
        "operandLeft": "https://w3id.org/edc/v0.0.1/ns/someProperty",
        "operator": "=",
        "operandRight": "value"
      }
    ]
  }
}
```

The request body is lean. Mandatory properties are:

- `counterPartyAddress` (formerly `providerUrl`): This property points to the DSP-endpoint of the Data Provider's
  Control
  Plane. Usually this ends on `/api/v1/dsp`.
- `counterPartyId`: must be the provider did. This property is mandatory. If omitted the catalog request will fail.
- `protocol`: must be `"dataspace-protocol-http"`.

The `querySpec` section is optional and allows the Data Consumer to specify what entries from the catalog shall be
returned. How to write proper `filterExpression`s was previously [explained](03_contractdefinitions.md#assetsselector).

## What happens in the background

In this walkthrough's sequence of API-calls, this is the first that triggers interaction between two EDCs. The Consumer
requests the Provider's catalog of Data Offers. Partners in the Dataspace are authenticated via Verifiable Credentials
(VC).
These can broadly be conceptualized as another JSON-LD document that holds information on a business partner's identity.
It follows an aligned schema and is extensible with properties relevant to the Dataspace.

When the Consumer makes a catalog-request to the Provider, the provider collects the Consumer's VC and checks it against
each of the `accessPolicies` defined in his [Contract Definitions](03_contractdefinitions.md). If the VC passes the
`accessPolicy`, the Contract Definition is transformed to a Data Offer and added to the catalog. If the content of the
VC does not fulfil the `accessPolicy`, the Contract Definition is invisible for the requesting Data Consumer - rendering
any further communication between the Business Partners useless.

## Returned Payload

The returned payload is a `dcat:Catalog` as required by
the [DSP-Specification v0.8](https://docs.internationaldataspaces.org/ids-knowledgebase/v/dataspace-protocol/catalog/catalog.protocol).

```json
{
  "@id": "acd67c9c-a5c6-4c59-9474-fcd3f948eab8",
  "@type": "dcat:Catalog",
  "dspace:participantId": "web:did:data-provider.com:dataspace",
  "dcat:dataset": {
    "@id": "{{ASSET_ID}}",
    "@type": "dcat:Dataset",
    "odrl:hasPolicy": {
      "@id": "MQ==:MQ==:M2ZmZDRhY2MtMzkyNy00NGI4LWJlZDItNDcwY2RiZGRjN2Ex",
      "@type": "odrl:Offer",
      "odrl:permission": {
        "odrl:action": {
          "odrl:type": "http://www.w3.org/ns/odrl/2/use"
        },
        "odrl:constraint": {
          "odrl:leftOperand": {
            "@id": "https://w3id.org/factoryx/policy/certification"
          },
          "odrl:operator": {
            "@id": "odrl:eq"
          },
          "odrl:rightOperand": "MyCertification"
        }
      },
      "odrl:prohibition": [],
      "odrl:obligation": []
    },
    "dcat:distribution": [
      {
        "@type": "dcat:Distribution",
        "dct:format": {
          "@id": "HttpData-PULL"
        },
        "dcat:accessService": {
          "@id": "1338f9ac-1728-4a7e-b3dc-31fe5bc109f6",
          "@type": "dcat:DataService",
          "dct:terms": "connector",
          "dct:endpointUrl": "http://provider-data.plane/api/v1/dsp"
        }
      }
    ],
    "description": "Product EDC Demo Asset 1",
    "id": "1"
  },
  "dcat:service": {
    "@id": "1338f9ac-1728-4a7e-b3dc-31fe5bc109f6",
    "@type": "dcat:DataService",
    "dct:terms": "connector",
    "dct:endpointUrl": "http://provider-data.plane/api/v1/dsp"
  },
  "@context": {
    "@vocab": "https://w3id.org/edc/v0.0.1/ns/",
    "edc": "https://w3id.org/edc/v0.0.1/ns/",
    "tx": "https://w3id.org/tractusx/v0.0.1/ns/",
    "tx-auth": "https://w3id.org/tractusx/auth/",
    "cx-policy": "https://w3id.org/catenax/policy/",
    "dcat": "http://www.w3.org/ns/dcat#",
    "dct": "http://purl.org/dc/terms/",
    "odrl": "http://www.w3.org/ns/odrl/2/",
    "dspace": "https://w3id.org/dspace/v0.8/"
  }
}
```

In the payload above, some properties are meta-data that's independent of whether the Provider extends any Data Offers
to the Consumer.

- The `@id` is the identifier for this catalog. As the catalog is created dynamically, the id is a UUID regenerated for
  each
  request to the Provider's catalog.
- `dcat:service` holds data about the Provider's connector that the Consumer's connector communicated with.
- `dspace:participantId` signifies the did of the Provider.
- `@context` is part of every JSON-LD document.

The Data Offers are hidden in the `dcat:dataset` section, grouped by the [Asset](01_assets.md) that the offer is made
for.
Consequently, if there may be more than one offer for the same Asset, requiring a Data Consumer to select based on the
policies included.

- The `@id` corresponds to the id of the Asset that can be negotiated for.
- `dcat:Distribution` makes statements over which Data Planes an Asset's data can be retrieved.
- `dcat:hasPolicy` holds the Data Offer that is relevant for the Consumer.
    - `@id` is the identifier for the Data Offer. The EDC composes this id by concatenating three identifiers in
      base64-encoding.
      separated with `:` (colons). The format is `base64(contractDefinitionId):base64(assetId):base64(newUuidV4)`. The
      last of three UUIDs changes with every request as every /v3/catalog/request call yields a new catalog with new
      Data Offers.
    - The `odrl:permission`, `odrl:prohibition` and `odrl:obligation` will hold the content of the contractPolicy
      configured in the [Contract Definition](03_contractdefinitions.md) the Contract Offer was derived from.

## Notes on Participant Identification

### Cross-Dataspace Interoperability

Factory-X EDC uses web-dids (see [spec](https://github.com/w3c-ccg/did-method-web)) as **technical identifier** for a
participant in the DSP messages. Participant identification is
an [extension point in the DSP](https://github.com/w3c-ccg/did-method-web)
that each Dataspace has to populate. If multiple Dataspaces use the same definition, they have cleared a hurdle for
cross-Dataspace-interoperability. With the announcement
that [Catena-X standard CX-0018](https://github.com/catenax-eV/product-standardization-prod/issues/315)
and the reference
implementation [eclipse-tractusx/tractusx-edc](https://github.com/eclipse-tractusx/sig-release/issues/1268)
will use web-dids for identifying participants in DSP-communication starting with the Saturn-Release (Oct 2025), it
makes sense for Factory-X EDC to follow suit. A divergent decisions would necessarily break interoperability with
Catena-X.

### Relationship to Business Identifiers

Business Applications integrating with factoryx-edc may have use-case specific identifiers for participants. This may be
a Catena-X BPN, a VAT-id or any other identifier. Any such id has to be mapped to a web-did before using the EDC
Management API. In the future, factoryx-edc may integrate against services executing a mapping for particular types of
Business ID.

## Notice

This work is licensed under the [CC-BY-4.0](https://creativecommons.org/licenses/by/4.0/legalcode).

- SPDX-License-Identifier: CC-BY-4.0
- SPDX-FileCopyrightText: 2023 Contributors of the Eclipse Foundation
- Source URL: [https://github.com/eclipse-tractusx/tractusx-edc](https://github.com/eclipse-tractusx/tractusx-edc)

- SPDX-License-Identifier: CC-BY-4.0
- SPDX-FileCopyrightText: 2025 Contributors of Factory-X
- Source
  URL: [https://github.com/factory-x-contributions/factoryx-edc](https://github.com/factory-x-contributions/factoryx-edc)
