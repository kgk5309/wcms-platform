# Artifact Index

이 디렉토리는 새 채팅에서 WCMS 프로젝트를 빠르게 이어가기 위한 아티팩트 모음이다.

## 파일 목록

### WCMS_CONTEXT_PACK.md

프로젝트의 전체 압축 컨텍스트다.
제품 기준, 조직/권한, 아키텍처, 서비스 경계, DB/API, 개발 순서를 포함한다.

### WCMS_IMPORTANT_DECISIONS.md

이미 닫힌 주요 결정사항만 모은 문서다.
새 채팅에서 확정 내용을 다시 묻지 않기 위한 기준으로 사용한다.

### NEW_CHAT_PROMPT.md

새 채팅을 열었을 때 붙여넣을 시작 프롬프트다.
이 파일의 프롬프트를 사용하면 Notion 전체 탐색 없이 로컬 컨텍스트부터 읽고 이어갈 수 있다.

## 사용법

새 채팅을 열면 아래 순서로 요청한다.

1. `docs/context/WCMS_CONTEXT_PACK.md` 읽기
2. `docs/context/WCMS_IMPORTANT_DECISIONS.md` 읽기
3. `docs/context/NEW_CHAT_PROMPT.md`의 시작 프롬프트 기준으로 진행

## 주의

- Notion 허브는 운영 대시보드다.
- 새 채팅의 빠른 맥락 복구는 이 로컬 문서들을 우선한다.
- Notion은 필요한 페이지가 명확할 때만 확인한다.

