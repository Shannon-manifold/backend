#!/usr/bin/env sh
set -eu

cd "$(dirname "$0")/.."

COMPOSE_FILE=compose.ec2.yaml exec scripts/setup-https.sh "$@"
