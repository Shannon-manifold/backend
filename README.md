# Shannon Manifold Backend

Shannon Manifold 프로젝트의 Spring Boot 백엔드 서버입니다.
Docker Compose로 Spring Boot, MySQL, Nginx, Certbot을 함께 실행합니다.

## 구성

- `app`: Spring Boot 백엔드, 내부 포트 `8080`
- `mysql`: MySQL 8.0
- `nginx`: 외부 `80`, `443` 포트 수신 및 백엔드 프록시
- `certbot`: Let's Encrypt 인증서 발급/갱신

## 1. 환경 변수 준비

```bash
cp .env.example .env
```

`.env`에서 아래 값을 실제 환경에 맞게 수정합니다.

```dotenv
MYSQL_USER=shannon
MYSQL_PASSWORD=shannon1234
MYSQL_ROOT_PASSWORD=rootpassword

LETSENCRYPT_DOMAIN=example.com
LETSENCRYPT_WWW_DOMAIN=www.example.com
LETSENCRYPT_EMAIL=admin@example.com
CERTBOT_STAGING=0
```

`www` 도메인을 쓰지 않는다면 `LETSENCRYPT_WWW_DOMAIN=`처럼 비워두세요.

## 2. 인증서 발급 전 확인

Let's Encrypt HTTP-01 인증은 외부에서 서버의 `80` 포트로 접근할 수 있어야 합니다.

확인할 항목:

- `LETSENCRYPT_DOMAIN`의 A 레코드가 이 서버의 public IP를 가리키는지
- `LETSENCRYPT_WWW_DOMAIN`을 쓴다면 해당 DNS도 설정되어 있는지
- 서버 방화벽 또는 클라우드 보안그룹에서 `80`, `443` 포트가 열려 있는지
- 같은 서버에서 다른 nginx/apache 프로세스가 `80` 포트를 이미 쓰고 있지 않은지

## 3. 최초 HTTPS 설정

아래 스크립트가 최초 인증서 발급 순서를 자동으로 진행합니다.

```bash
scripts/setup-https.sh
```

또는 `.env`를 쓰지 않고 직접 넘길 수 있습니다.

```bash
scripts/setup-https.sh example.com admin@example.com www.example.com
```

스크립트가 수행하는 순서:

1. `nginx/default.http.conf`를 `nginx/default.conf`로 복사합니다.
2. 인증서 없이 동작하는 HTTP 전용 nginx를 먼저 실행합니다.
3. Certbot을 webroot 방식으로 실행해 인증서를 발급합니다.
4. `nginx/default.https.conf.template`으로 HTTPS nginx 설정을 생성합니다.
5. Spring Boot, MySQL, Certbot 갱신 컨테이너를 실행한 뒤 nginx 설정을 검사하고 reload합니다.

성공하면 인증서가 아래 경로에 생성됩니다.

```bash
certbot/conf/live/<도메인>/fullchain.pem
certbot/conf/live/<도메인>/privkey.pem
```

확인:

```bash
curl -I http://example.com
curl -I https://example.com
docker compose ps
```

`http` 요청은 `https`로 `301` 리다이렉트되고, `https` 요청은 nginx를 통해 백엔드로 전달됩니다.

## 4. 이후 일반 실행

최초 인증서 발급이 끝난 뒤에는 일반적으로 아래 명령만 사용하면 됩니다.

```bash
docker compose up -d --build
```

로그 확인:

```bash
docker compose logs -f
docker compose logs -f nginx
docker compose logs -f app
docker compose logs -f certbot
```

종료:

```bash
docker compose down
```

MySQL 데이터까지 삭제하려면:

```bash
docker compose down -v
```

## 5. 인증서 갱신

`certbot` 컨테이너는 12시간마다 `certbot renew`를 실행합니다.
인증서 파일이 갱신된 뒤 nginx가 새 인증서를 읽게 하려면 reload가 필요합니다.

```bash
docker compose exec nginx nginx -s reload
```

갱신 테스트:

```bash
docker compose run --rm --entrypoint certbot certbot renew --dry-run
```

## 문제 해결

Nginx 설정 검사:

```bash
docker compose exec nginx nginx -t
```

Nginx가 인증서 파일을 찾지 못한다면 아직 최초 발급이 끝나지 않았거나, `LETSENCRYPT_DOMAIN`과 인증서 경로의 도메인이 다를 가능성이 큽니다.

Certbot이 인증에 실패한다면 DNS가 서버 public IP를 가리키는지, 외부에서 `http://도메인/.well-known/acme-challenge/...` 경로에 접근 가능한지 먼저 확인하세요.

Let's Encrypt rate limit을 피하며 테스트하려면 `.env`에 아래처럼 설정한 뒤 스크립트를 실행하세요.

```dotenv
CERTBOT_STAGING=1
```

테스트 성공 후 운영 인증서를 받을 때는 다시 `CERTBOT_STAGING=0`으로 바꾸고 실행합니다.
