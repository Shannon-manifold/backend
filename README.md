# Shannon Manifold Backend

Shannon Manifold 프로젝트의 Spring Boot 백엔드 서버입니다.
Docker Compose로 Spring Boot, MySQL, Nginx, Certbot을 함께 실행합니다.

## 구성

- `app`: Spring Boot 백엔드, 내부 포트 `8080`
- `mysql`: MySQL 8.0
- `nginx`: 외부 `80`, `443` 포트 수신 및 백엔드 프록시
- `certbot`: Let's Encrypt 인증서 발급/갱신

## EC2 빠른 배포

EC2에서는 `compose.ec2.yaml`과 `scripts/setup-https-ec2.sh`를 사용합니다.

1. EC2 보안그룹 인바운드를 엽니다.

```text
TCP 22   내 IP만 허용
TCP 80   0.0.0.0/0, ::/0
TCP 443  0.0.0.0/0, ::/0
```

2. 도메인의 A 레코드를 EC2 Elastic IP 또는 public IPv4로 연결합니다.

```text
shannonmanifold.p-e.kr -> EC2 public IPv4
```

3. EC2에 Docker와 Docker Compose를 설치한 뒤 `.env`를 준비합니다.

```bash
docker compose version
```

위 명령이 실패하면 Compose v2 plugin이 없는 상태입니다. 배포 스크립트는 구버전 `docker-compose`도 자동 감지하므로 둘 중 하나는 동작해야 합니다.

```bash
docker-compose version
```

둘 다 실패하면 Docker Compose를 먼저 설치하세요. 공식 설치 문서는 <https://docs.docker.com/compose/install/linux/> 입니다.

`docker compose version`은 성공하지만 `sudo docker compose version`이 실패한다면 Compose plugin이 일반 사용자 홈에만 설치된 상태입니다. 이 경우 Docker 권한을 사용자에게 부여해 `sudo` 없이 실행하거나, Compose plugin을 system-wide 경로에 설치하세요.

```bash
cp .env.ec2.example .env
vi .env
```

4. 최초 인증서를 발급하고 HTTPS nginx 설정까지 전환합니다.

```bash
sudo scripts/setup-https-ec2.sh
```

5. 상태를 확인합니다.

```bash
sudo docker compose -f compose.ec2.yaml ps
curl -I http://shannonmanifold.p-e.kr/health
curl -I https://shannonmanifold.p-e.kr/health
```

이후 재배포는 아래 명령을 사용합니다.

```bash
sudo docker compose -f compose.ec2.yaml up -d --build
```

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

EC2에서는 EC2 전용 compose 파일을 사용하는 래퍼 스크립트를 실행합니다.

```bash
sudo scripts/setup-https-ec2.sh
```

또는 `.env`를 쓰지 않고 직접 넘길 수 있습니다.

```bash
scripts/setup-https.sh example.com admin@example.com www.example.com
sudo scripts/setup-https-ec2.sh shannonmanifold.p-e.kr admin@example.com
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

EC2에서는 아래 명령을 사용합니다.

```bash
sudo docker compose -f compose.ec2.yaml up -d --build
```

로그 확인:

```bash
docker compose logs -f
docker compose logs -f nginx
docker compose logs -f app
docker compose logs -f certbot
```

EC2 로그 확인:

```bash
sudo docker compose -f compose.ec2.yaml logs -f nginx
sudo docker compose -f compose.ec2.yaml logs -f app
sudo docker compose -f compose.ec2.yaml logs -f certbot
```

종료:

```bash
docker compose down
```

EC2 종료:

```bash
sudo docker compose -f compose.ec2.yaml down
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

EC2에서 reload:

```bash
sudo docker compose -f compose.ec2.yaml exec nginx nginx -s reload
```

갱신 테스트:

```bash
docker compose run --rm --entrypoint certbot certbot renew --dry-run
```

EC2에서 갱신 테스트:

```bash
sudo docker compose -f compose.ec2.yaml run --rm --entrypoint certbot certbot renew --dry-run
```

## 문제 해결

Nginx 설정 검사:

```bash
docker compose exec nginx nginx -t
```

Nginx가 인증서 파일을 찾지 못한다면 아직 최초 발급이 끝나지 않았거나, `LETSENCRYPT_DOMAIN`과 인증서 경로의 도메인이 다를 가능성이 큽니다.

Certbot이 인증에 실패한다면 DNS가 서버 public IP를 가리키는지, 외부에서 `http://도메인/.well-known/acme-challenge/...` 경로에 접근 가능한지 먼저 확인하세요.

`unknown shorthand flag: 'f' in -f`가 나오면 Docker Compose가 설치되지 않았거나 현재 Docker CLI에서 `docker compose`를 사용할 수 없는 상태입니다.

```bash
docker compose version
docker-compose version
```

둘 중 하나가 동작해야 합니다. 최신 스크립트는 `docker compose`와 `docker-compose`를 모두 지원합니다.

`docker compose version`은 되는데 `sudo docker compose version`만 실패한다면 `sudo`로 실행한 root 환경에서 Compose plugin을 찾지 못하는 상태입니다. EC2에서는 Docker 권한을 사용자에게 부여해 `sudo` 없이 실행하거나 Compose plugin을 system-wide로 설치해야 합니다.

Let's Encrypt rate limit을 피하며 테스트하려면 `.env`에 아래처럼 설정한 뒤 스크립트를 실행하세요.

```dotenv
CERTBOT_STAGING=1
```

테스트 성공 후 운영 인증서를 받을 때는 다시 `CERTBOT_STAGING=0`으로 바꾸고 실행합니다.
