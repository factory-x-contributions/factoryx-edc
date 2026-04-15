# Credential-Gated Policy Example

This is a simple example showing how Factory-X EDC policies can control access to assets based on credentials the consumer has.

## How it works

Policies are written in ODRL and checked by the provider's control plane. When a consumer asks for the catalog, the provider checks if the consumer has the right credentials for each asset. If not, the asset is hidden from the catalog.

### Credentials used

| Credential              | Left Operand      | Right Operand | Issued by default? |
|-------------------------|-------------------|---------------|--------------------|
| MembershipCredential    | FxMembership      | active        | Yes                |
| CertificationCredential | CertificationType | ISO9001       | No                 |

- If the consumer has a MembershipCredential, they can see and use the membership-gated asset.
- If the consumer does not have a CertificationCredential, they can't see the certification-gated asset.

## Policy Examples

FxMembership Policy:

```json
{
    "permission": {
      "action": "use",
      "constraint": {
        "leftOperand": "https://w3id.org/factoryx/policy/v1.0/FxMembership",
        "operator": "eq",
        "rightOperand": "active"
      }
  }
}
```

Certification Policy:

```json
{
  "permission": {
    "action": "use",
    "constraint": {
      "leftOperand": "CertificationType",
      "operator": "eq",
      "rightOperand": "ISO9001"
    }
  }
}
```

Both policies are checked when showing the catalog and during contract negotiation.

## Prerequisites

> [!NOTE]
> Run this example before the default transaction flow, or in a fresh environment (`docker compose down -v`).
> The default flow's contract definition previously used an empty asset selector that would match all assets,
> bypassing the credential-gated access policies.

## Steps

1. Start everything: `docker compose up`
2. Run the identity setup requests (issuer, consumer, provider, credential issuance)
3. Run the provider setup requests in order:
   - CreateMembershipGatedAsset
   - CreateCertificationGatedAsset
   - CreateMembershipPolicy
   - CreateCertificationPolicy
   - CreateMembershipContractDef
   - CreateCertificationContractDef
4. Test without credential: run `RequestCatalog-NoCertification`. You shouldn't see the certification-gated asset.
5. Test with credential: run `RequestCatalog-WithMembership`, then the negotiation and transfer steps. You should see the membership-gated asset and be able to transfer data.

## Notes

A third EDC for this example isn't required. The consumer already has MembershipCredential but not CertificationCredential, so you can see both allowed and denied cases.

For more details, see the docs in the [EDC Management API Walkthrough](../../docs/usage/management-api-walkthrough/README.md).
