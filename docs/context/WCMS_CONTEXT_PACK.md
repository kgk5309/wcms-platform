# WCMS Context Pack

이 문서는 새 채팅 또는 새 작업 세션에서 WCMS 프로젝트 맥락을 빠르게 복구하기 위한 기준 문서다.
Notion 전체를 다시 탐색하지 않고, 이 파일을 먼저 읽은 뒤 필요한 경우에만 Notion 허브를 확인한다.

## 1. 제품 기준

- 제품명: WCMS Platform
- 초기 제품 초점: DID 중심 WCMS
- 핵심 가치: 비개발자도 콘텐츠를 쉽게 올리고 디스플레이에 송출할 수 있게 한다.
- 주요 사용자: 도입/계약이 완료된 고객사, 테넌트 운영자, 클라이언트 운영자, 플랫폼 운영자
- WCMS Admin은 운영 관리 시스템이다.
- 공개 사용자 홈페이지는 도입 문의, 상담 신청, 데모 요청, 자료 요청을 처리하는 별도 접점이다.
- WCMS Admin에는 공개 회원가입, 테넌트 가입 요청, 권한 신청, 셀프 온보딩을 두지 않는다.

## 2. 조직 구조

기본 구조:

```text
Platform > Tenant > Client > Facility / Device / Content
```

- Tenant는 클라이언트들을 묶는 고객/조직 그룹이다.
- Client는 실제 운영 단위다.
- Facility는 클라이언트 안의 물리적 위치 또는 운영 위치다.
- 테넌트끼리의 상위 그룹은 MVP에 없다.
- 테넌트 위의 그룹 구조가 필요하면 별도 온프레미스 제공 시나리오로 본다.

## 3. 어드민 구조

프론트엔드는 별도 React 프로젝트로 분리한다.

- platform-admin: 플랫폼 내부 운영자용
- wcms-admin: 고객사/테넌트 운영자용
- qa-site: 운영 전 검증용
- public-website: 제품 소개와 도입 문의용

Nginx 라우팅 기준:

- admin.example.com -> platform-admin
- wcms.example.com -> wcms-admin
- qa.example.com -> qa-site
- www.example.com -> public-website

## 4. 계정/가입 정책

- 일반 개인 회원가입은 제공하지 않는다.
- 사용자는 상위 권한자가 직접 생성한다.
- 아이디는 관리자가 직접 입력한다.
- 이메일은 플랫폼 전체에서 중복 금지다.
- 계정 생성 완료 즉시 랜덤 임시 비밀번호를 이메일로 발송한다.
- 최초 로그인 시 비밀번호 변경을 강제한다.
- 비밀번호 분실 시 상위 관리자가 초기화하고 새 임시 비밀번호를 발급한다.
- 권한 신청/수락 흐름은 사용하지 않는다.

## 5. 역할 체계

역할:

1. 수퍼관리자
2. 플랫폼 마스터
3. 플랫폼 엔지니어
4. 플랫폼 유저
5. 테넌트 마스터
6. 테넌트 엔지니어
7. 테넌트 유저
8. 클라이언트 마스터
9. 클라이언트 엔지니어
10. 클라이언트 유저

접근 기준:

- 수퍼관리자: 플랫폼 전체와 수퍼계정 포함 전체 접근
- 플랫폼 마스터: 플랫폼 전체 접근, 수퍼계정 제외
- 플랫폼 엔지니어/유저: 기본 접근 없음, 위임받은 테넌트/클라이언트만 접근
- 테넌트 마스터: 자기 테넌트 전체 접근
- 테넌트 엔지니어/유저: 부여받은 클라이언트 접근
- 클라이언트 마스터/엔지니어/유저: 자기 클라이언트 접근

## 6. 계정 생성 권한

- 수퍼관리자: 모든 계정 생성 가능
- 플랫폼 마스터: 플랫폼 마스터, 플랫폼 엔지니어, 플랫폼 유저 생성 가능
- 플랫폼 엔지니어: 테넌트 마스터/엔지니어/유저, 클라이언트 마스터/엔지니어/유저 생성 가능
- 테넌트 마스터: 테넌트 엔지니어/유저, 클라이언트 마스터/엔지니어/유저 생성 가능
- 테넌트 엔지니어: 테넌트 유저, 클라이언트 마스터/엔지니어/유저 생성 가능
- 클라이언트 마스터: 클라이언트 엔지니어/유저 생성 가능
- 클라이언트 엔지니어: 클라이언트 유저 생성 가능
- 각 유저 역할: 생성 불가

규칙:

- 생성자는 자신 이상의 권한을 줄 수 없다.
- 플랫폼 마스터는 플랫폼 마스터 계정을 추가 생성할 수 있다.
- 테넌트/클라이언트 마스터는 플랫폼에서 생성되지 않으면 자체적으로 상위 마스터 계정을 만들 수 없다.

## 7. 리소스별 권한

대상 리소스:

- 콘텐츠
- 장비
- 시설

권한 값:

- 없음
- 읽기
- 읽기/쓰기

메뉴 노출:

- 없음: 메뉴 숨김
- 읽기: 메뉴 표시, 쓰기 기능 숨김
- 읽기/쓰기: 메뉴와 쓰기 기능 모두 제공

기본값:

- 마스터: 읽기/쓰기
- 엔지니어: 읽기/쓰기
- 유저: 읽기

권한 변경:

- 상위 권한자가 하위 권한자의 권한을 실시간 수정할 수 있다.
- 엔지니어와 유저 모두 리소스별 권한 설정 대상이다.
- 권한 설정 위치는 `사용자 관리 > 사용자 상세 > 권한 설정`이다.

## 8. 테넌트/클라이언트 생성 정책

테넌트 생성 시 필수:

- 테넌트 마스터 1명 이상
- 초기 클라이언트 1개 이상
- 각 초기 클라이언트별 클라이언트 마스터 1명 이상

테넌트 생성 플로우:

1. 테넌트 기본정보
2. 테넌트 마스터
3. 초기 클라이언트
4. 클라이언트별 클라이언트 마스터
5. 최종 확인

임시저장:

- 상태명: 작성중
- 작성중 상태에서는 실제 계정 생성 없음
- 작성중 상태에서는 이메일 발송 없음
- 생성자 본인과 상위 플랫폼 권한자만 재개 가능
- 보관 기간 제한 없음
- 삭제 가능

독립 클라이언트:

- 테넌트 없이 생성 가능
- 클라이언트 마스터 1명 이상 필수
- 임시저장 규칙은 테넌트 생성과 동일

## 9. 클라이언트 소속 변경

가능한 이동:

- 독립 클라이언트 -> 테넌트 편입
- 테넌트 소속 클라이언트 -> 독립 분리
- 테넌트 A -> 테넌트 B 이동

이동 가능 권한:

- 수퍼관리자
- 플랫폼 마스터
- 플랫폼 엔지니어

권한 처리:

- 이동 즉시 기존 테넌트 권한은 해제한다.
- 기존 클라이언트 마스터/엔지니어/유저는 유지한다.
- 새 테넌트 마스터는 즉시 접근 가능하다.
- 새 테넌트 엔지니어/유저는 별도 권한 부여 전까지 접근 불가다.
- 이동은 ClientMoved 이벤트와 권한 재계산을 동반한다.

## 10. 확정 기술 스택

- Backend: Spring Boot
- Java: 17
- Architecture: MSA
- ORM: JPA
- Database: MariaDB
- Frontend: React
- Auth/Security: Spring Security, JWT, Redis
- Messaging: RabbitMQ
- Routing: Nginx
- Runtime: Docker

## 11. 백엔드 서비스 경계

MVP 핵심 서비스:

- auth-service: 로그인, JWT, 비밀번호, 토큰 무효화
- user-service: 사용자, 역할, 권한, 소속, 리소스 권한
- organization-service: 테넌트, 클라이언트, 시설, 작성중, 소속 이동
- file-service: 이미지/동영상 업로드, 저장, 메타데이터, 용량 관리
- content-service: 콘텐츠, 콘텐츠 타입, 콘텐츠 상태, 파일 참조
- device-service: DID 장비, 장비 그룹, heartbeat, 상태
- schedule-service: 플레이리스트, 편성, 스케줄, 송출 규칙

추후 후보:

- notification-service
- billing-service
- inquiry-service

## 12. 프로젝트 구조 원칙

최상위 구조:

```text
domain/
infra/
core/
```

- domain: 서비스별 비즈니스 도메인
- infra: DB, Redis, RabbitMQ, 외부 연동, 배포 설정
- core: 공통 예외, 응답, 유틸, 보안, 이벤트 규격

## 13. DB 기준

- MariaDB 단일 인스턴스
- 서비스별 논리 schema 분리
- 서비스 간 DB join 금지
- 서비스 간 FK 금지
- 외부 데이터는 ID 참조
- 조회 최적화가 필요하면 스냅샷 허용

schema 예시:

- wcms_auth
- wcms_user
- wcms_organization
- wcms_file
- wcms_content
- wcms_device
- wcms_schedule

## 14. 인증/JWT/Redis 기준

- WCMS는 서버 세션을 사용하지 않는 무상태 API 서버 구조를 지향한다.
- 각 Spring Boot 서비스 인스턴스는 세션을 들고 있지 않는다.
- 인증은 JWT 기반으로 처리한다.
- 로그아웃, 강제 만료, 권한 실시간 반영처럼 순수 JWT만으로 처리하기 어려운 상태성 보조 데이터는 Redis에 외부화한다.
- Redis를 사용하더라도 서비스 인스턴스 자체는 stateless로 유지한다.
- JWT에는 세부 권한을 넣지 않는다.
- Access Token: 15분
- Refresh Token: 14일
- 세부 권한 원본은 user-service가 가진다.
- 빠른 조회용 권한 캐시는 Redis에 둔다.
- 권한 변경 시 tokenVersion 증가, 캐시 갱신, 기존 토큰 무효화 처리한다.

Redis에 저장할 주요 데이터:

- Refresh Token
- tokenVersion
- 권한 캐시
- 강제 로그아웃/토큰 무효화 정보
- 로그인 실패 횟수와 계정 잠금 보조 정보
- 필요 시 첫 로그인 비밀번호 변경 필요 여부 캐시

## 15. RabbitMQ 기준

- 도메인별 topic exchange
- 구독 서비스별 queue
- queue별 DLQ
- 재시도: 10초 -> 1분 -> 5분 후 DLQ

대표 이벤트:

- UserCreated
- UserPermissionChanged
- UserDisabled
- TenantCreated
- ClientCreated
- ClientMoved
- ContentStatusChanged
- FileStatusChanged
- DeviceStatusChanged

## 16. 파일서비스 기준

- MVP 파일 저장소: 로컬 디스크
- 파일 접근: file-service API 경유
- 저장 경로:
  - `tenant-{tenantId}/client-{clientId}/yyyy/MM/{fileId}.{ext}`
  - `independent-client-{clientId}/yyyy/MM/{fileId}.{ext}`
- 코드 구조: StorageProvider 인터페이스 + LocalStorageProvider 구현
- 삭제: DB soft delete 후 비동기 물리 삭제
- 허용 타입: 이미지, 동영상
- 문서 파일: MVP 제외
- 이미지 1개 최대: 20MB
- 동영상 1개 최대: 500MB
- 중복 업로드: 허용
- Free 플랜 총 용량: 300MB

## 17. 주요 화면 구조

Platform Admin:

- 대시보드
- 조직 관리
- 사용자 관리
- 정책 관리
- 시스템 관리

WCMS Admin:

- 대시보드
- 콘텐츠
- 장비
- 편성/스케줄
- 시설
- 사용자
- 설정

주요 화면:

- 사용자 관리
- 테넌트 생성
- 독립 클라이언트 생성
- 클라이언트 소속 이동
- 콘텐츠 업로드
- 장비 관리
- 시설 관리
- 플레이리스트/편성

## 18. MVP 개발 순서

1. 인프라 세팅
2. Docker Compose
3. core / infra 공통 모듈
4. auth-service
5. organization-service
6. user-service
7. file-service
8. content-service
9. device-service
10. schedule-service
11. platform-admin
12. wcms-admin
13. 통합 검증

## 19. Notion 기준

메인 허브:

- WCMS Platform Project Hub

중요 페이지:

- WCMS 프로젝트 페이지 지도
- 인증/가입/권한 정책
- 개발환경 및 아키텍처 기준
- 화면 및 메뉴 구조 기준
- 서비스별 데이터 설계 초안
- 서비스별 API 설계 초안
- 개발 태스크 분해

Tracker:

- Mission Board
- Screen & Menu Board
- Feature Backlog
- Data Design Tracker
- API Design Tracker
- Decision Log
- Issue & Risk Board
- Weekly Execution Plan
