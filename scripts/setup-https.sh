#!/usr/bin/env sh
set -eu

cd "$(dirname "$0")/.."

if [ -f .env ]; then
    set -a
    . ./.env
    set +a
fi

DOMAIN="${1:-${LETSENCRYPT_DOMAIN:-}}"
EMAIL="${2:-${LETSENCRYPT_EMAIL:-}}"
WWW_DOMAIN="${3:-${LETSENCRYPT_WWW_DOMAIN:-}}"
CERTBOT_STAGING="${CERTBOT_STAGING:-0}"

if [ -z "$DOMAIN" ] || [ -z "$EMAIL" ]; then
    echo "Usage: scripts/setup-https.sh <domain> <email> [www-domain]"
    echo
    echo "Or set LETSENCRYPT_DOMAIN, LETSENCRYPT_EMAIL, and optional LETSENCRYPT_WWW_DOMAIN in .env."
    exit 1
fi

SERVER_NAMES="$DOMAIN"
CERTBOT_DOMAINS="-d $DOMAIN"

if [ -n "$WWW_DOMAIN" ]; then
    SERVER_NAMES="$SERVER_NAMES $WWW_DOMAIN"
    CERTBOT_DOMAINS="$CERTBOT_DOMAINS -d $WWW_DOMAIN"
fi

STAGING_ARG=""
if [ "$CERTBOT_STAGING" = "1" ]; then
    STAGING_ARG="--staging"
fi

mkdir -p certbot/conf certbot/www

echo "1/5 Activating HTTP-only nginx config."
cp nginx/default.http.conf nginx/default.conf
docker compose up -d --no-deps nginx

echo "2/5 Requesting certificate for: $SERVER_NAMES"
docker compose run --rm \
    --entrypoint certbot \
    certbot certonly \
    --webroot \
    -w /var/www/certbot \
    $CERTBOT_DOMAINS \
    --email "$EMAIL" \
    --agree-tos \
    --no-eff-email \
    $STAGING_ARG

echo "3/5 Activating HTTPS nginx config."
sed \
    -e "s|__DOMAIN__|$DOMAIN|g" \
    -e "s|__SERVER_NAMES__|$SERVER_NAMES|g" \
    nginx/default.https.conf.template > nginx/default.conf

echo "4/5 Starting backend services and certbot renewal container."
docker compose up -d --build app mysql certbot

echo "5/5 Testing and reloading nginx."
docker compose exec -T nginx nginx -t
docker compose exec -T nginx nginx -s reload

echo "Done. Check with: curl -I https://$DOMAIN"
