# Participant Identification

## Cross-Dataspace Interoperability

Factory-X EDC uses web-dids (see [spec](https://github.com/w3c-ccg/did-method-web)) as **technical identifier** for a
participant in the DSP messages. Participant identification is an [extension point in the DSP](https://github.com/w3c-ccg/did-method-web)
that each Dataspace has to populate. If multiple Dataspaces use the same definition, they have cleared a hurdle for
cross-Dataspace-interoperability. With the announcement that [Catena-X standard CX-0018](https://github.com/catenax-eV/product-standardization-prod/issues/315)
and the reference implementation [eclipse-tractusx/tractusx-edc](https://github.com/eclipse-tractusx/sig-release/issues/1268)
will use web-dids for identifying participants in DSP-communication starting with the Saturn-Release (Oct 2025), it 
makes sense for Factory-X EDC to follow suit. A divergent decisions would necessarily break interoperability with 
Catena-X.

## Relationship to Business Identifiers

Business Applications integrating with factoryx-edc may have use-case specific identifiers for participants. This may be
a Catena-X BPN, a VAT-id or any other identifier. Any such id has to be mapped to a web-did before using the EDC
Management API, as documented in the [usage docs](../usage/management-api-walkthrough/04_catalog.md#fetching-a-providers-catalog).
In the future, factoryx-edc may integrate against services executing a mapping for particular types of Business ID.
