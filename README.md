# Shannon Manifold Backend

이 저장소는 Shannon Manifold 프로젝트의 Spring Boot 백엔드 서버입니다.
Docker와 Docker Compose를 활용하여 손쉽게 실행할 수 있도록 구성되어 있습니다.

## 🚀 빠른 시작 (Getting Started)

저장소를 Clone한 후, 제공되는 Docker 환경을 통해 즉시 애플리케이션을 실행할 수 있습니다.

### 1. 저장소 Clone
먼저 터미널을 열고 본 저장소를 로컬 머신에 clone 합니다.
```bash
git clone <이 레포지토리의 URL>
cd backend
```

### 2. 환경 변수 설정
프로젝트 루트 디렉토리에서 `.env.example` 파일을 복사하여 `.env` 파일을 생성합니다.
(MySQL 계정 정보 등이 포함되어 있습니다. 필요 시 값을 수정할 수 있습니다.)
```bash
cp .env.example .env
```

### 3. Docker로 즉시 실행
아래 명령어를 실행하면, 내부적으로 `Dockerfile`을 통해 **Spring Boot 애플리케이션 이미지가 자동으로 빌드**되며, Nginx 및 MySQL 컨테이너와 함께 즉시 실행됩니다.

```bash
docker compose up -d --build
```

- `--build` 옵션을 통해 최신 코드가 반영된 도커 이미지를 새로 빌드하고 실행합니다.
- `-d` 옵션은 백그라운드에서 컨테이너를 실행하게 해줍니다.

### 4. 실행 확인
컨테이너가 정상적으로 실행되었는지 확인하려면 다음 명령어를 사용하세요:
```bash
docker compose ps
```

- **Nginx (API Gateway)**: `https://실제도메인` 으로 접속하여 백엔드 API를 호출할 수 있습니다. (HTTP 접근 시 HTTPS로 자동 리다이렉트됩니다.)
- **Spring Boot App**: 포트 `8080` (내부망 통신 전용)
- **MySQL DB**: 포트 `3306`

실행 로그를 확인하려면 아래 명령어를 입력합니다.
```bash
# 전체 로그 확인
docker compose logs -f

# 특정 컨테이너(app) 로그만 확인
docker compose logs -f app
```

## 🔐 SSL 인증서 최초 발급 및 적용

현재 프로젝트의 `nginx/default.conf`는 **SSL 인증서가 이미 발급된 상태**를 가정하여 443 포트와 HTTPS 리다이렉션이 적용되어 있습니다. 
따라서 로컬 환경이나 인증서가 없는 상태에서 최초로 `docker compose up -d`를 실행하면 Nginx 컨테이너가 시작되지 않을 수 있습니다.

실제 서버에 배포하여 도메인을 연결하는 경우 다음 순서로 세팅해 주세요.

1. **도메인 변경**: `nginx/default.conf` 파일을 열어 `example.com`으로 되어 있는 부분을 보유하신 **실제 도메인**으로 모두 변경합니다. (현재 자동 치환 완료됨)
2. **최초 인증서 발급 (Standalone)**: 아직 Nginx가 켜지지 않은 상태이므로, 80 포트를 사용하여 인증서를 먼저 발급받습니다. (`compose.yaml`에 지정된 무한루프 entrypoint를 덮어쓰기 위해 `--entrypoint` 옵션이 필요합니다.)
   ```bash
   sudo docker compose run --rm --entrypoint "certbot" -p 80:80 certbot certonly --standalone -d example.com -d www.example.com
   ```
3. **전체 서비스 실행**: 인증서가 발급되면(내부적으로 `./certbot/conf`에 저장됨) 서비스를 정상적으로 실행합니다.
   ```bash
   docker compose up -d --build
   ```

> **참고**: 이후 인증서 갱신은 백그라운드에 떠 있는 `certbot` 컨테이너가 웹루트(`/var/www/certbot`) 방식을 통해 자동으로 만료 전에 처리합니다.

## 🛑 컨테이너 종료 및 삭제
작업을 마치고 서버를 내릴 때는 다음 명령어를 사용합니다:
```bash
docker compose down
```
> **참고**: `docker compose down` 명령어는 컨테이너와 기본 네트워크를 삭제하지만, MySQL 데이터베이스의 데이터 볼륨은 유지됩니다. 데이터까지 완전히 초기화하려면 `docker compose down -v`를 사용하세요.
