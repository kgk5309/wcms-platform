# WCMS Important Decisions

이 문서는 WCMS 프로젝트에서 이미 닫힌 주요 결정을 모은다.
새 채팅에서는 이 문서를 기준으로 이미 확정된 내용을 다시 질문하지 않고 다음 작업으로 진행한다.

## 제품/범위

- 초기 MVP는 홈페이지 CMS가 아니라 DID 중심 WCMS에 집중한다.
- WCMS Admin은 운영 시스템이다.
- Public Website는 도입 문의, 상담, 데모, 자료 요청 접점이다.
- 공개 회원가입, 권한 신청, 테넌트 가입 요청, 셀프 온보딩은 MVP에서 제외한다.

## 조직/계정

- 조직 구조는 Platform > Tenant > Client > Facility / Device / Content다.
- Tenant는 상위 고객 그룹, Client는 실제 운영 단위다.
- 테넌트 없는 독립 클라이언트도 존재할 수 있다.
- 테넌트 생성 시 테넌트 마스터, 초기 클라이언트, 클라이언트 마스터를 반드시 함께 만든다.
- 클라이언트는 독립/테넌트 소속/테넌트 간 이동이 가능하다.
- 클라이언트 이동은 플랫폼 권한자만 가능하다.

## 권한

- 역할은 수퍼관리자, 플랫폼/테넌트/클라이언트의 마스터/엔지니어/유저로 구성한다.
- 플랫폼 엔지니어/유저는 기본 접근이 없고 위임받은 범위만 접근한다.
- 리소스 권한은 콘텐츠/장비/시설 기준으로 없음, 읽기, 읽기/쓰기다.
- 권한 없음이면 메뉴를 숨긴다.
- 권한 변경은 실시간 반영되어야 한다.

## 기술

- Spring Boot, Java 17, MSA, JPA, MariaDB, React, Docker로 진행한다.
- 보안은 Spring Security, JWT, Redis를 사용한다.
- 서버 세션을 사용하지 않는 무상태 API 서버 구조를 지향한다.
- Redis는 서버 세션 저장소가 아니라, Refresh Token, tokenVersion, 권한 캐시, 강제 만료 같은 상태성 보조 데이터를 외부화하는 용도로 사용한다.
- 각 서비스 인스턴스는 세션을 들고 있지 않고 stateless하게 유지한다.
- 메시징은 RabbitMQ를 사용한다.
- 라우팅은 Nginx가 담당한다.
- DB는 MariaDB 단일 인스턴스 + 서비스별 논리 schema 분리다.
- 서비스 간 DB join과 FK는 금지한다.

## 서비스

MVP 서비스:

- auth-service
- user-service
- organization-service
- file-service
- content-service
- device-service
- schedule-service

추후 후보:

- notification-service
- billing-service
- inquiry-service

## 파일/콘텐츠

- 파일 저장소는 MVP에서 로컬 디스크다.
- 파일 접근은 file-service API를 경유한다.
- content-service는 콘텐츠 업무 개념을 관리하고, file-service는 실제 파일을 관리한다.
- 콘텐츠는 contentType을 가진다.
- 이미지 최대 20MB, 동영상 최대 500MB다.
- Free 플랜 총 용량은 300MB다.

## 프론트엔드

별도 React 프로젝트:

- platform-admin
- wcms-admin
- qa-site
- public-website

## 개발 순서

1. Phase 1 작업보드
2. 스캐폴딩
3. Docker Compose
4. core / infra
5. auth-service
6. organization-service
7. user-service
8. file/content/device/schedule
9. 프론트 연결
10. 통합 검증
