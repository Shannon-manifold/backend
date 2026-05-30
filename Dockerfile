# ===== Stage 1: Build =====
FROM eclipse-temurin:25-jdk AS builder

WORKDIR /app

# Gradle 파일 먼저 복사 (캐시 활용)
COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .

# Gradle Wrapper 실행 권한 부여
RUN chmod +x gradlew

# 의존성 미리 다운로드 (캐시 레이어)
RUN ./gradlew dependencies --no-daemon || true

# 소스 코드 복사 및 빌드
COPY src src
RUN ./gradlew bootJar --no-daemon -x test

# ===== Stage 2: Runtime =====
FROM eclipse-temurin:25-jre

# elan 및 lake 실행에 필요한 패키지 설치
RUN apt-get update && apt-get install -y \
    curl \
    git \
    tar \
    ca-certificates \
    && rm -rf /var/lib/apt/lists/*

WORKDIR /app

# 빌드 결과물 복사
COPY --from=builder /app/build/libs/*.jar app.jar

# 비root 사용자 생성 및 홈 디렉토리 마련
RUN groupadd -r appuser && useradd -r -m -g appuser appuser

# 검증을 위한 Lean test 프로젝트 복사
COPY --chown=appuser:appuser test /app/test

# 권한 정리
RUN chown -R appuser:appuser /app

# 사용자 변경
USER appuser

# elan 및 lean 환경 변수 등록
ENV ELAN_HOME=/home/appuser/.elan
ENV PATH="${ELAN_HOME}/bin:${PATH}"

# elan 설치 및 기본 lean4 툴체인(v4.29.1) 다운로드
RUN curl https://raw.githubusercontent.com/leanprover/elan/master/elan-init.sh -sSf | sh -s -- -y --default-toolchain leanprover/lean4:v4.29.1

# 빌드 시점에 Mathlib 의존성 및 캐시를 다운로드하여 런타임에 딜레이가 발생하지 않도록 캐싱
WORKDIR /app/test
RUN lake update && lake exe cache get

# 실행을 위한 작업 디렉토리 복귀
WORKDIR /app

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
