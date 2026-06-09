# Initiating a Contract Negotiation

Contract Negotiation is the second check a Data Consumer has to pass before getting access rights to a backend resource.
It includes

- a check of the Consumer's VC against the Offer's `contractPolicy`.
- a check of the `contractPolicy` against the policy the Data Consumer signals in the negotiation request to.

## Creating a new Contract Negotiation

To trigger the process, the Data Consumer POSTs against their own Control Plane.

```http request
POST /v3/contractnegotiations HTTP/1.1
Host: https://consumer-control.plane/api/management
X-Api-Key: password
Content-Type: application/json
```

```json
{
  "@context": [
    {
      "@vocab": "https://w3id.org/edc/v0.0.1/ns/"
    },
    "https://w3id.org/catenax/2025/9/policy/odrl.jsonld"
  ],
  "@type": "ContractRequest",
  "counterPartyAddress": "https://provider-control.plane/dsp/2025-1",
  "protocol": "dataspace-protocol-http:2025-1",
  "policy": {
    "@type": "Offer",
    "@id": "{{OFFER_ID}}",
    "target": "{{ASSET_ID}}",
    "assigner": "{{PROVIDER_DID}}",
    "permission": [
      {
        "action": "use",
        "constraint": {
          "leftOperand": "BusinessPartnerDID",
          "operator": "eq",
          "rightOperand": "{{CONSUMER_DID}}"
        }
      }
    ],
    "prohibition": [],
    "obligation": []
  },
  "callbackAddresses": [
    {
      "transactional": false,
      "uri": "http://callback/url",
      "events": [
        "contract.negotiation"
      ],
      "authKey": "auth-key",
      "authCodeId": "auth-code-id"
    }
  ]
}
```
- `counterPartyAddress` sets the coordinates for the connector that the Consumer-EDC shall negotiate with (Provider EDC).
  It will usually end in `/dsp/2025-1`
- `protocol` must be `dataspace-protocol-http:2025-1`
- In the `policy` section, the Data Consumer specifies the Data Offer for the negotiation. As there may be multiple
  Data Offers for the same DataSet, the Data Consumer must choose one.
  It must hold an identical copy of the Data Offer's contract policy as provided via the catalog-API in the `odrl:hasPolicy` field plus:
    - `assigner` must hold the DID of the Provider
    - `target` must be the id of the EDC-Asset/dcat:DataSet that the offer was made for.
- `callbackAddresses` is a list of Consumer-side endpoints that the Provider's Data Plane writes events to.
    - `uri` is the http endpoint of the token repository. Mandatory.
    - `events` is a list of the strings, signifying for what callbacks the specified API shall be used. They are
      structured hierarchically, so if a Consumer is interested in all events about status changes, the
      `contract.negotiation` marker can be added. If only events about the `requested` stage of a transfer are relevant,
      they can be subscribed via `transfer.process.requested`. This enables the consumer to wait for arrival of a
      relevant event instead of having to poll for transition into a desired state.
    - `transactional` Optional, default false.
    - `authCodeId` is the key of a secret stored in the Consumer's vault that can be used to unlock the callback API if
      it is protected. Optional.
    - `authKey` Key of the HTTP-header that will be sent to the callbackAddress for authentication. Optional. If
      `authCodeId` is set and `authKey` isn't, it defaults to `Authorization`.

This call does not yet return a negotiation result but rather a server-side generated id for the contract negotiation in
the `@id` property.

```json
{
	  "@type": "IdResponse",
	  "@id": "773b8795-45f2-4c57-a020-dc04e639baf3",
	  "createdAt": 1701289079455,
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

## Checking for Completion

### Polling

```http request
GET /v3/contractnegotiations/773b8795-45f2-4c57-a020-dc04e639baf3 HTTP/1.1
Host: https://consumer-control.plane/api/management
X-Api-Key: password
Content-Type: application/json
```

This request (holding the previously returned `contractNegotiationId` in its path) returns details on the negotiation
that will look like this:

```json
{
  "@type": "ContractNegotiation",
  "@id": "50bf14b9-8f6e-4975-8ada-6f24379a58a2",
  "type": "CONSUMER",
  "protocol": "dataspace-protocol-http:2025-1",
  "state": "REQUESTING",
  "counterPartyId": "{{PROVIDER_DID}}",
  "counterPartyAddress": "https://provider-control.plane/dsp/2025-1",
  "callbackAddresses": [
    {
      "transactional": false,
      "uri": "http://call.back/url",
      "events": [
        "contract.negotiation"
      ],
      "authKey": "auth-key",
      "authCodeId": "auth-code-id"
    }
  ],
  "createdAt": 1701351116766,
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

The Contract Negotiation was successful when `state == FINALIZED`.
## Notice

This work is licensed under the [CC-BY-4.0](https://creativecommons.org/licenses/by/4.0/legalcode).

- SPDX-License-Identifier: CC-BY-4.0
- SPDX-FileCopyrightText: 2023 Contributors of the Eclipse Foundation
- Source URL: [https://github.com/eclipse-tractusx/tractusx-edc](https://github.com/eclipse-tractusx/tractusx-edc)

- SPDX-License-Identifier: CC-BY-4.0
- SPDX-FileCopyrightText: 2025 Contributors of Factory-X
- Source
  URL: [https://github.com/factory-x-contributions/factoryx-edc](https://github.com/factory-x-contributions/factoryx-edc)
