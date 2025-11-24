# provision-additional-headers

This library provides plain old java objects (POJOs) to represent a resource definition `AdditionalHeadersResourceDefinition` of type `dataspaceconnector:additionalheadersresourcedefinition`

## Usage
When a `HttpData` transfer goes into a `PROVISIONING` state, an `AdditionalHeadersResourceDefinition` is provisioned.

The provisioner was defined in tractusx-edc's  [provision-additional-headers](https://github.com/eclipse-tractusx/tractusx-edc/tree/release/0.11.0/edc-extensions/provision-additional-headers) extension which has been excluded from factoryx-edc.
So going forward, `AdditionalHeadersResourceDefinition` will not be provisioned anymore for new transfers. But for existing transfers, where `AdditionalHeadersResourceDefinition` has already been provisioned, factoryx-edc will fail to serialize it.

Hence, this library provides `AdditionalHeadersResourceDefinition` POJO to serialize existing transfers successfully.
