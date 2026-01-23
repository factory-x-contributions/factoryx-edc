#!/bin/sh
set -euo pipefail

VAULT="${VAULT_ADDR:-http://shared-vault:8200}"
TOKEN="${VAULT_TOKEN:?missing VAULT_TOKEN}"

# create rsa keypair

openssl genrsa -out /tmp/priv_pkcs1.pem 2048
openssl pkcs8 -topk8 -nocrypt -in /tmp/priv_pkcs1.pem -out /tmp/priv_pkcs8.pem
openssl rsa -in /tmp/priv_pkcs1.pem -pubout -out /tmp/pub.pem

# deploy secrets for provider dataplane to vault

jq -n --rawfile content /tmp/priv_pkcs8.pem '{data:{content:$content}}' | \
  curl -fsS -H "X-Vault-Token: $TOKEN" -H "Content-Type: application/json" \
    -X POST --data-binary @- "$VAULT/v1/secret/data/privkey"

jq -n --rawfile content /tmp/pub.pem '{data:{content:$content}}' | \
  curl -fsS -H "X-Vault-Token: $TOKEN" -H "Content-Type: application/json" \
    -X POST --data-binary @- "$VAULT/v1/secret/data/pubkey"