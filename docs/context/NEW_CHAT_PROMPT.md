# New Chat Prompt

새 채팅을 열었을 때 아래 문장을 그대로 붙여넣고 시작한다.

```text
나는 WCMS Platform 프로젝트를 이어서 진행하려고 한다.
이 프로젝트는 DID 중심 WCMS SaaS이며, 이미 기획/권한/아키텍처/DB/API 초안이 정리되어 있다.

먼저 아래 로컬 문서를 순서대로 읽고 현재 프로젝트 맥락을 복구해줘.

1. docs/context/WCMS_CONTEXT_PACK.md
2. docs/context/WCMS_IMPORTANT_DECISIONS.md
3. docs/context/NEW_CHAT_PROMPT.md

읽은 뒤에는 Notion 전체를 탐색하지 말고, 필요한 경우에만 내가 지정한 Notion 페이지를 확인해줘.
현재 목표는 개발 착수를 위한 Phase 1 작업보드 생성과 프로젝트 스캐폴딩이다.

작업 순서는 다음 기준으로 진행해줘.

1. 현재 로컬 디렉토리 구조 확인
2. Phase 1 작업 범위 정리
3. 프로젝트 스캐폴딩 계획 제시
4. 내가 승인하면 폴더/파일 생성
5. Docker Compose 초안 작성
6. core / infra 공통 구조 작성
7. auth-service부터 구현 시작

중요한 기준:
- 백엔드: Spring Boot, Java 17, MSA, JPA
- DB: MariaDB 단일 인스턴스 + 서비스별 논리 schema
- 보안: Spring Security, JWT, Redis
- 서버 구조: 서버 세션을 사용하지 않는 무상태 API 서버 구조
- Redis 용도: Refresh Token, tokenVersion, 권한 캐시, 강제 만료 등 상태성 보조 데이터 외부화
- 메시징: RabbitMQ
- 라우팅: Nginx
- 프론트엔드: React, platform-admin / wcms-admin / qa-site / public-website 분리
- 프로젝트 구조: domain / infra / core
- 파일서비스는 로컬 디스크 저장소를 사용하고 StorageProvider 추상화를 둔다.

이미 확정된 정책을 다시 묻지 말고, 애매하거나 충돌하는 부분만 질문해줘.
```

## 새 채팅에서 진행할 추천 순서

1. 컨텍스트 파일 읽기
2. 현재 디렉토리 확인
3. Phase 1 작업보드 생성
4. 스캐폴딩 범위 확인
5. 백엔드/프론트/인프라 폴더 생성
6. Docker Compose 작성
7. 공통 README와 환경변수 샘플 작성
8. auth-service부터 시작
