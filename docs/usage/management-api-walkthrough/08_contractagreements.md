# Checking existing Contract Agreements

The Management API has a provider-internal endpoint to retrieve existing Contract Agreements. It also exposes a `/request`
endpoint (to be used with the previously explained `QuerySpec` object) but allows retrieval of single agreements by id
like this:

```http request
GET /v3/contractagreements/{{AGREEMENT_ID}} HTTP/1.1
Host: https://consumer-control.plane/api/management
X-Api-Key: password
Content-Type: application/json
```

A Contract Agreement looks like this:

```json
{
  "@type": "ContractAgreement",
  "@id": "{{AGREEMENT_ID}}",
  "assetId": "{{ASSET_ID}}",
  "policy": {
    "@id": "{{POLICY_ID}}",
    "@type": "Agreement",
    "permission": {
      "action": {
        "type": "http://www.w3.org/ns/odrl/2/use"
      },
      "constraint": {
        "or": {
          "leftOperand": "https://w3id.org/catenax/2025/9/policy/FrameworkAgreement",
          "operator": "eq",
          "rightOperand": "Pcf"
        }
      }
    },
    "prohibition": [],
    "obligation": [],
    "assignee": "<DID_CONSUMER>",
    "assigner": "<DID_PROVIDER>",
    "target": "{{ASSET_ID}}"
  },
  "contractSigningDate": 1713441910,
  "consumerId": "{{DID_CONSUMER}}",
  "providerId": "{{DID_PROVIDER}}",
  "@context": [
    "https://w3id.org/tractusx/auth/v1.0.0",
    "https://w3id.org/catenax/2025/9/policy/context.jsonld",
    "https://w3id.org/catenax/2025/9/policy/odrl.jsonld",
    "https://w3id.org/dspace/2025/1/context.jsonld",
    "https://w3id.org/edc/dspace/v0.0.1",
    {
      "fx-policy": "https://w3id.org/factoryx/policy/v1.0/"
    }
  ]
}
```

Most of this data should already be known to the Data Provider from the negotiation and transfer processes but can be
retrieved at a glance via this API.

## Notice

This work is licensed under the [CC-BY-4.0](https://creativecommons.org/licenses/by/4.0/legalcode).

- SPDX-License-Identifier: CC-BY-4.0
- SPDX-FileCopyrightText: 2023 Contributors of the Eclipse Foundation
- Source URL: [https://github.com/eclipse-tractusx/tractusx-edc](https://github.com/eclipse-tractusx/tractusx-edc)
