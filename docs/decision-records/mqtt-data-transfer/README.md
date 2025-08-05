## MQTT Data Transfer in EDC

### General Remarks

MQTT is a multi-layered suite of protocols. It defines several transport-bindings and a set of messages on top. In the
context of this EDC Extension, at least the binding via `wss://` shall be supported. To remain compatible with 

### Security Assumptions

Clients (publishers and subscribers) authenticate to the Server (broker) by passing material in the `password` field
of the `CONN` message. The material in question must be JWT issued by an IdP the Broker trusts. This token is passed
from the Data Provider (controlling the Broker and the IdP) to the Consumer via the `EndpointProperty` called
`https://w3id.org/dspace/2025/1/mqtt-pull/authorization`. 

The refresh endpoint exists so that there can be sequentially refreshed access tokens instead of long-lived client
credentials. The auth token in `https://w3id.org/dspace/2025/1/mqtt-pull/authorization` has a time-to-live of
`https://w3id.org/dspace/2025/1/mqtt-pull/expiresIn`.