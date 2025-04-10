# Moving from BPN to DID

Factory-X doesn't use BPN as dataspace participant identifier. Instead, it uses DID as participant id.
As a result, following are the changes required while deploying a factoryx-edc connector.

## Connector Runtime Changes

- FactoryX EDC expects a configuration setting `edc.participiant.id` with value as participant DID.
  It can be configured via following ways.
    - Define an environment variable `EDC_PARTICIPANT_ID`while deploying the connector.
    - If you deploy FactoryX via helm chart, it can be configured via `values.yaml` with key `participant.id`.

## Management APIs Changes

- During connector to connector communications via management APIs, we need to pass `counterPartyId` in some of the
  request payload which should contain DID of the participant where request is being sent.
- During Initiate Negotiation, `policy#assigner` needs to be passed, which again should be counterparty participant DID.

## STS changes for Self Issued (SI) tokens

FactoryX Connector's Secure Token Service (STS) should issue a verifiable credential having a `credentialSubject` with
`id` as participant DID. This `id` is used as a participant identifier and used is policy evaluations.

```json
{
  "iat": 1723479215,
  "exp": 1755015198,
  "vc": {
    "id": "<VC ID>",
    "type": [
      "VerifiableCredential",
      "MembershipCredential"
    ],
    "issuer": "did:web:participant.si.issuer.com:particpiantA",
    "@context": [
      "https://www.w3.org/2018/credentials/v1"
    ],
    "issuanceDate": "2024-08-12T16:13:35.946Z",
    "expirationDate": "2025-08-12T16:13:18.180Z",
    "credentialStatus": {},
    "credentialSubject": {
      "id": "did:web:participant.si.issuer.com:particpiantA",
      "other": "data"
    }
  },
  "iss": "did:web:participant.si.issuer.com:particpiantA",
  "sub": "did:web:participant.si.issuer.com:particpiantA",
  "jti": "<JTI>"
}
```

## No More BPN to DID Resolution

Since BPN is no longer used and instead, DID is directly getting used as participant id, a BPN to DID resolution is not
needed anymore. Any internal / external service needed to resolve BPN to its DID, should not be deployed / included in
connector ecosystem.
