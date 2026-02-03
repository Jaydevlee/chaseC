# ChaseC
> A comprehensive cargo tracking system.

## 프로젝트 개요
ChaseC는 통관 과정 조회 시스템으로, 사용자가 화물의 통관 상태를 실시간으로 확인할 수 있도록 도와줍니다. 이 시스템은 관세청 API와 연동하여 최신 통관 정보를 제공합니다.
그리고 사용자는 이메일을 통해 통관 상태 업데이트를 받을 수 있습니다.

## 주요 기능
- **화물 통관 상태 조회**: 사용자는 HBL 번호와 이메일을 입력하여 화물의 통관 상태를 조회할 수 있습니다.
- **이메일 알림**: 통관 상태가 변경될 때마다 사용자는 이메일로 알림을 받습니다.


## 기술 스택
- **백엔드**: Spring Boot
- **데이터베이스**: MySQL
- **빌드 도구**: Gradle
- **버전 관리**: Git

## 프로젝트 구조
```plaintext
chasec/
├── src/main/
│   ├── java/com/example/chasec/
│   │   ├── controller/        # REST API 컨트롤러
│   │   ├── service/           # 비즈니스 로직
│   │   ├── repository/        # 데이터베이스 접근 레이어
│   │   ├── entity/            # JPA 엔티티 클래스
│   │   ├── dto/               # 데이터 전송 객체
│   │   └── ChasecApplication.java  # 메인 애플리케이션 클래스
│   └── resources/
│       ├── application.properties  # 애플리케이션 설정 파일
│       └── templates/              # 이메일 템플릿
└── build.gradle                  # Gradle 빌드 스크립트
```   

## ERD
```mermaid
erDiagram
    TrackRequest ||--o{ TrackHistory : "has many"
    TrackRequest {
        Long id PK "(기본키 Auto Increment)"
        String hblNo "(HBL 번호)"
        String email "(이메일)"
        int blYear  "(BL 연도)"
        String status "(현재 진행 상태)"
        LocalDateTime createdAt "(최초 생성 일시)"
        LocalDateTime updatedAt "(최종 수정 일시)"
    }
    TrackHistory {
        Long id PK "(기본키 Auto Increment)"
        Long track_request_id FK "(외래키 TrackRequest 참조)"
        String status "(과거/현재 상태)"
        LocalDateTime processingTime "(처리 시간)"
    }
```

## 데이터 흐름
```mermaidflowchart TD
    A[사용자] -->|HBL 번호, 이메일 입력| B[ChaseC 시스템]
    B -->|통관 상태 조회 요청| C[관세청 API]
    C -->|통관 상태 응답| B
    B -->|상태 업데이트| D[데이터베이스]
    D -->|이메일 알림 발송| E[이메일 서비스]
    E -->|이메일 수신| A
``` 