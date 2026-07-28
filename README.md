# dietlog — 식단 기록 웹 서비스

개인이 끼니별로 식단을 기록하고, 칼로리/음식군별 통계를 확인하는 웹 서비스입니다.
(멋쟁이사자처럼 백엔드스쿨 24기 5일 캡스톤 — 원래 과제 "머니로그(가계부)"를 "dietlog(식단기록)"로 도메인 전환하여 진행)

## 🔗 배포 URL
- **서비스**: http://13.124.48.149:8080/login.html
- **Swagger API 문서**: http://13.124.48.149:8080/swagger-ui.html
- **GitHub 저장소**: https://github.com/jdo120893/dietlog

## 🛠 기술 스택
- **Backend**: Java 21, Spring Boot 4.1, Spring Security + JWT, Spring Data JPA
- **DB**: MySQL 8 (운영) / H2 (로컬 개발)
- **Infra**: Docker, Docker Compose, AWS EC2 (t3.small)
- **Frontend**: 순수 HTML/JavaScript (프레임워크 없음)
- **Docs**: springdoc-openapi (Swagger UI)

## ✅ 핵심 기능 (기본 요구사항)
- 회원가입/로그인(JWT 발급, BCrypt 암호화)
- 회원가입 시 기본 카테고리(아침/점심/저녁/간식) 자동 생성
- 카테고리 CRUD
- 식단 기록 CRUD, 월별 필터링/페이징
- 월별 통계 (총 섭취 칼로리, 끼니별/음식군별 집계)
- 본인 데이터만 접근 가능한 인가(Authorization) 처리
- Swagger API 문서 자동화

## 🚀 도전 과제
| 과제 | 설명 |
|---|---|
| **목표 칼로리 설정 (F-11)** | 월 단위 전체/카테고리별 목표 칼로리 등록·조회 |
| **검색 · CSV 내보내기 (F-13)** | 키워드/기간/칼로리 범위로 기록 검색, 월별 기록 CSV 다운로드 |
| **Refresh Token (F-14)** | accessToken(1시간) 만료 시 재로그인 없이 새 토큰 재발급 |

## 📂 설계 문서
- [요구사항 정의서](docs/requirements.md)
- [ERD](docs/erd.md)
- [DB 스키마](docs/schema.sql)
- [API 명세서](docs/api-spec.md)

## 🖥 실행 방법 (로컬)
```bash
git clone https://github.com/jdo120893/dietlog.git
cd dietlog

# .env 파일 생성 (프로젝트 루트)
# DB_ROOT_PASSWORD=...
# DB_USERNAME=...
# DB_PASSWORD=...
# JWT_SECRET=...

docker compose up -d --build
```
접속: http://localhost:8080/login.html

## 🏗 아키텍처
[브라우저]
↓ HTTP
[Spring Boot 컨테이너] ← JWT 인증
↓ JDBC
[MySQL 컨테이너]

※ 위 두 컨테이너는 AWS EC2(t3.small) 위 docker-compose로 실행


## 🧩 트러블슈팅 하이라이트
- **YAML/DDL 인코딩 깨짐**: 파일이 EUC-KR로 저장되어 문법 에러 발생 → UTF-8로 재작성
- **MySQL 컬럼명 특수 취급**: `year_month` 컬럼명이 DDL과 Hibernate 자동 생성 쿼리 양쪽에서 파싱 에러 유발 → DDL은 백틱으로 감싸고, 엔티티는 `@Column(name = "\`year_month\`")`로 이스케이프해 해결
- **EC2 메모리 부족**: t3.micro(1GB)에서 Docker 빌드+MySQL+앱 동시 실행 시 SSH 접속 불능 → t3.small로 업그레이드 + 2GB 스왑 메모리 추가
- **LazyInitializationException**: 지연 로딩된 연관 엔티티 접근 시 세션 종료 문제 → JPQL `JOIN FETCH`로 해결
- **Spring Security 403 마스킹**: 500 에러가 `/error` 리다이렉트 과정에서 403으로 가려짐 → `/error` 경로 permitAll 처리로 실제 에러 노출

## 📝 회고
5일 동안 자바 문법부터 Spring Boot, JPA, Spring Security, Docker, AWS EC2까지 배운 전 과정을 하나의 프로젝트로 연결해봤습니다.
특히 4일차 EC2 배포 과정에서 메모리 부족, 인코딩 문제, 컬럼명 파싱 에러 등 실제 운영 환경에서만 마주치는 문제들을 직접 디버깅하며 해결한 경험이 가장 값졌습니다.