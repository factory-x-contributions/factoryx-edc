# Policies

## Policies in Factory-X

In the EDC, policies are pure [ODRL (Open Digital Rights Language)](https://www.w3.org/TR/odrl-model/).
Like the payloads of the [Dataspace Protocol](README.md), they are written in **JSON-LD**.
It is important to keep in mind that the extensive ODRL-context (that the EDC is aware of)
allows for ergonomic reuse of the vocabulary in individual policies.

### Policies & Verifiable Credentials (VC)

#### General Information

Factory-X uses policies to determine access to and use of data. The policies refer to verifiable credentials (VC) that
are stored in the Wallets. Factory-X uses the principle of self-sovereign identity (SSI).

The key architectural principle underlying this specification is that policy definitions must be decoupled from their
corresponding VC schema. Namely, the specific **constraints** (
see [ODRL-classes](#odrl-information-model-classes-excerpt)) and shape of the VC schema must not be reflected in the
policy definition. This allows VC schemas to be altered without impacting policy definitions.

### Creating a Policy Definition

Policies can be created in the EDC as follows:

```http request
POST /v3/policydefinitions HTTP/1.1
Host: https://provider-control.plane/api/management
X-Api-Key: password
Content-Type: application/json
```

```json
{
  "@context": [
    "https://w3id.org/factoryx/policy/v1.0/context.jsonld",
    "http://www.w3.org/ns/odrl.jsonld",
    {
      "@vocab": "https://w3id.org/edc/v0.0.1/ns/"
    }
  ],
  "@type": "PolicyDefinition",
  "@id": "{{POLICY_ID}}",
  "policy": {
    "@type": "Set",
    "permission": [
      {
        "action": "use",
        "constraint": {
          "leftOperand": "BusinessPartnerDID",
          "operator": "eq",
          "rightOperand": "did:web:consumer-on-whitelist.com:path"
        }
      }
    ]
  }
}
```

| Variable                           | Content                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                         |
|------------------------------------|-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `@context`                         | In JSON-LD, `@context` is a fundamental concept used to define the mapping of terms used within the JSON-LD document to specific IRIs (Internationalized Resource Identifiers). It provides a way to establish a shared understanding of the vocabulary used in a JSON-LD document, making it possible to create structured and semantically rich data that can be easily integrated with other data sources on the web.                                                                                                                                                                                        |
| `@context`.`odrl:`                 | Prefixes allow you to define short aliases for longer IRIs. For example, instead of repeatedly using the full IRI [http://www.w3.org/ns/odrl/2/](http://www.w3.org/ns/odrl/2/), you can define a prefix like "odrl" and append a segment/fragment to identify the resource in the namespace.                                                                                                                                                                                                                                                                                                                    |
| `@id`                              | A Policy MUST have one uid property value (of type IRI) to identify the Policy.  Note: The `@id` is on the upper level. It is a database policy definition which wraps the ODRL policy.                                                                                                                                                                                                                                                                                                                                                                                                                         |
| `policy`.`@type`                   | A Set Policy is the default Policy subclass. The Set is aimed at scenarios where there is an open criteria for the semantics of the policy expressions and typically refined by other systems/profiles that process the information at a later time. No privileges are granted to any Party (if defined). More detailed information about the possible policy subclasses can be found [here](https://w3c.github.io/poe/model/#infoModel).                                                                                                                                                                       |
| `policy`.`permission`              | A Policy MUST have at least one permission, prohibition, or obligation property values.                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                         |
| `policy`.`permission`.`action`     | "use" the target asset (under a specific permission), currently only the action "use" is used by Catena-X                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                       |
| `policy`.`permission`.`constraint` | A boolean/logical expression that refines an Action and Party/Asset collection or the conditions applicable to a Rule. The leftOperand instances MUST clearly be defined to indicate the semantics of the Constraint. Catena-X will use the **left operand** of a *constraint* to associate a specific verifiable credential (VC). As most are use-case-agreements, [this notation](https://github.com/eclipse-tractusx/tractusx-profiles/blob/main/cx/policy/specs/policy.mapping.md) is useful. **Right Operand:** The rightOperand is the value of the Constraint that is to be compared to the leftOperand. |

Please note that in JSON-LD, structures that may look different may actually have the same meaning. They may be expanded
or compacted, define additional `@context` objects, refer to a predefined outside `@context` or others. Using a parser
or the [json-ld playground](https://json-ld.org/playground/) helps to be consistent. Changing the `@context` will change
the entire structure and meaning of the payload. So be careful and always include `http://www.w3.org/ns/odrl.jsonld` as
a string in the `@context`.

If the creation of the `policy-definition` was successful, the Management-API will return HTTP 200.

#### Factory-X specific `constraints`

This implementation (`factoryx-edc`) contains extensions that trigger specific behavior when encountering specific
policies.

##### Checks against the did of a Business Partner:

The [did-validation extension](./../../../edc-extensions/did-validation-core)
allows to define either a single Business Partner authorized to pass the constraint or define a group of dids that
may pass and can be extended at runtime. This is validated against the identity of a Business Partner, currently
extracted from the Factory-X membership credential.

```json
{
  "@context": {
    "dcat": "http://www.w3.org/ns/dcat#",
    "dct": "http://purl.org/dc/terms/",
    "odrl": "http://www.w3.org/ns/odrl/2/",
    "dspace": "https://w3id.org/dspace/v0.8/"
  },
      "@id": "something",
      "@type": "odrl:Offer",
      "odrl:permission": {
        "odrl:action": {
          "@id": "odrl:use"
        },
        "odrl:constraint": {
          "odrl:leftOperand": {
            "@id": "BusinessPartnerDID"
          },
          "odrl:operator": {
            "@id": "odrl:eq"
          },
          "odrl:rightOperand": "did:web:my-partner.com:who:is:cool"
        }
      },
      "odrl:prohibition": [],
      "odrl:obligation": []
}
```


```json
{
  "@context": [
    "https://www.w3.org/2018/credentials/v1",
    "https://w3id.org/factoryx/credentials/v1.0/context.jsonld"
  ],
  "id": "1f36af58-0fc0-4b24-9b1c-e37d59668089",
  "type": [
    "VerifiableCredential",
    "MembershipCredential"
  ],
  "issuer": "did:web:com.example.issuer",
  "issuanceDate": "2021-06-16T18:56:59Z",
  "expirationDate": "2022-06-16T18:56:59Z",
  "credentialSubject": {
    "id": "did:web:my-partner.com:who:is:cool",
    "baseUrl": "https://super-cool.corp"
  }
}
```

##### Checks against the membership of a Business Partner in FX

A policy holding this constraint can be created like this:
```json
{
  "@context": {
    "dcat": "http://www.w3.org/ns/dcat#",
    "dct": "http://purl.org/dc/terms/",
    "odrl": "http://www.w3.org/ns/odrl/2/"
  },
  "@id": "something",
  "@type": "odrl:Offer",
  "odrl:permission": {
    "odrl:action": {
      "@id": "odrl:use"
    },
    "odrl:constraint": {
      "odrl:leftOperand": {
        "@id": "https://w3id.org/factoryx/policy/v1.0/Membership"
      },
      "odrl:operator": {
        "@id": "odrl:eq"
      },
      "odrl:rightOperand": "active"
    }
  },
  "odrl:prohibition": [],
  "odrl:obligation": []
}



```
It is validated against the membership credential:
```json
{
  "@context": [
    "https://www.w3.org/2018/credentials/v1",
    "https://w3id.org/factoryx/credentials/v1.0/context.jsonld"
  ],
  "id": "1f36af58-0fc0-4b24-9b1c-e37d59668089",
  "type": [
    "VerifiableCredential",
    "MembershipCredential"
  ],
  "issuer": "did:web:com.example.issuer",
  "issuanceDate": "2021-06-16T18:56:59Z",
  "expirationDate": "2022-06-16T18:56:59Z",
  "credentialSubject": {
    "id": "did:web:com.example.participant",
    "baseUrl": "https://participant.com"
  }
}
```

##### Checks against a certification

A Data Provider can extend an offer to only those holders of a certain trusted certification. This is open for all
types of certification and maps the kind from the Credential to the Constraint like with `MyCertification` in the
matching examples that follow.

```json
{
  "@context": {
    "dcat": "http://www.w3.org/ns/dcat#",
    "dct": "http://purl.org/dc/terms/",
    "odrl": "http://www.w3.org/ns/odrl/2/",
    "dspace": "https://w3id.org/dspace/v0.8/"
  },
  "@id": "something",
  "@type": "odrl:Offer",
  "odrl:permission": {
    "odrl:action": {
      "@id": "odrl:use"
    },
    "odrl:constraint": {
      "odrl:leftOperand": {
        "@id": "https://w3id.org/factoryx/policy/v1.0/certification"
      },
      "odrl:operator": {
        "@id": "odrl:eq"
      },
      "odrl:rightOperand": "MyCertification"
    }
  },
  "odrl:prohibition": [],
  "odrl:obligation": []
} 
```

```json
{
  "@context": [
    "https://www.w3.org/2018/credentials/v1",
    "https://w3id.org/factoryx/credentials/v1.0/context.jsonld"
  ],
  "id": "1f36af58-0fc0-4b24-9b1c-e37d59663089",
  "type": [
    "VerifiableCredential",
    "CertificationCredential"
  ],
  "issuer": "did:web:com.example.issuer",
  "issuanceDate": "2021-06-16T18:56:59Z",
  "expirationDate": "2022-06-16T18:56:59Z",
  "credentialSubject": {
    "id": "did:web:com.example.participant",
    "certificationType": "MyCertification"
  }
}
```

##### **Checks for temporal validity**: 

If a usage policy is defined against a HTTP-based asset accessible via EDR-tokens,

the Data Provider can prohibit issuance of new tokens by defining a specific constraint based on the
[contract validity check extension](https://eclipse-edc.github.io/documentation/for-adopters/control-plane/policy-engine/#in-force-policy)

### Access & Usage Policies

In EDC, a distinction is made between **Access** and **Usage** Policies.

- **access policy:** determines whether a particular consumer is offered an asset or not. For example, we may want to
  restrict certain assets such that only consumers within a particular geography can see them. Consumers outside that
  geography wouldn't even have them in their catalog.
- **usage policy or contract policy:** determines the conditions for initiating a contract negotiation for a particular
  asset. Note that does not automatically guarantee the successful creation of a contract, it merely expresses the
  eligibility to start the negotiation. The terms "usage policy" and "contract policy" are used synonymously!

**The Access and Usage Policies are not distinguished by any special semantics, but rather by the time at which they are
checked.**

Whether a policy is used as access or usage policy is determined
during [contract definition](03_contractdefinitions.md).

## Notice

This work is licensed under the [CC-BY-4.0](https://creativecommons.org/licenses/by/4.0/legalcode).

- SPDX-License-Identifier: CC-BY-4.0
- SPDX-FileCopyrightText: 2023 Contributors of the Eclipse Foundation
- Source URL: [https://github.com/eclipse-tractusx/tractusx-edc](https://github.com/eclipse-tractusx/tractusx-edc)

- SPDX-License-Identifier: CC-BY-4.0
- SPDX-FileCopyrightText: 2025 Contributors of Factory-X
- Source
  URL: [https://github.com/factory-x-contributions/factoryx-edc](https://github.com/factory-x-contributions/factoryx-edc)