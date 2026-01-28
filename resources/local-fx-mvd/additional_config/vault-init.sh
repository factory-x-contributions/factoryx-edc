#!/usr/bin/env bash
set -euo pipefail

VAULT="${VAULT_ADDR:-http://shared-vault:8200}"
TOKEN="${VAULT_TOKEN:?missing VAULT_TOKEN}"

# function that creates and deploys a rsa keypair:

create_and_store_keypair() {
  local prefix=$1

  # create rsa keypair
  openssl genrsa -out /tmp/${prefix}_priv_pkcs1.pem 2048
  openssl pkcs8 -topk8 -nocrypt -in /tmp/${prefix}_priv_pkcs1.pem -out /tmp/${prefix}_priv.pem
  openssl rsa -in /tmp/${prefix}_priv_pkcs1.pem -pubout -out /tmp/${prefix}_pub.pem

  # deploy secrets to vault
  jq -n --rawfile content /tmp/${prefix}_priv.pem '{data:{content:$content}}' | \
    curl -fsS -H "X-Vault-Token: $TOKEN" -H "Content-Type: application/json" \
      -X POST --data-binary @- "$VAULT/v1/secret/data/${prefix}_priv"

  jq -n --rawfile content /tmp/${prefix}_pub.pem '{data:{content:$content}}' | \
    curl -fsS -H "X-Vault-Token: $TOKEN" -H "Content-Type: application/json" \
      -X POST --data-binary @- "$VAULT/v1/secret/data/${prefix}_pub"

  # cleanup temp files
  rm -f /tmp/${prefix}_priv_pkcs1.pem /tmp/${prefix}_priv.pem /tmp/${prefix}_pub.pem
}

# create keypair for consumer and provider dataplane:

create_and_store_keypair "cons"
create_and_store_keypair "prov"