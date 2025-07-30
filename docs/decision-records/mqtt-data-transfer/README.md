## MQTT Data Transfer in EDC

### General Remarks

MQTT is a multi-layered suite of protocols. It defines several transport-bindings and a set of messages on top. In the
context of this EDC Extension, at least the binding via `wss://` shall be supported. To remain compatible with 

### Security Assumptions

Clients (publishers and subscribers) authenticate to the Server (broker) by passing material in the `password` field
of the `CONN` message. The material in question must be JWT issued by an IdP the Broker trusts. This token is passed
from the Data Provider (controlling the Broker and the IdP) to the Consumer via the `EndpointProperty` called
`https://w3id.org/edc/v0.0.1/ns/authorization`. 