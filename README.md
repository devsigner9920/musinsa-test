# 카테고리 시스템

## 구현 기능

### 필수 기능
- **카테고리 등록**: 새로운 카테고리 생성 및 계층 구조 설정
- **카테고리 수정**: 기존 카테고리 정보 수정 및 계층 구조 변경
- **카테고리 삭제**: 카테고리 삭제 (하위 카테고리 CASCADE 삭제)
- **개별 카테고리 조회**: 특정 카테고리와 하위 카테고리 트리 구조 조회
- **전체 카테고리 조회**: 모든 카테고리의 계층적 트리 구조 조회

### 추가 기능
- **캐싱**: 카테고리 트리 구조 캐싱으로 성능 최적화
- **입력 검증**: 입력값 검증 및 비즈니스 규칙 적용
- **예외 처리**: 예외 처리 및 표준화된 오류 응답

### 비즈니스 규칙
- **최대 깊이 제한**: 5레벨까지 계층 구조 허용
- **중복 방지**: 동일 부모 하위에서 카테고리명 중복 불가
- **순환 참조 방지**: 자기 자신이나 하위 카테고리를 부모로 설정 불가
- **계층 무결성**: 부모-자식 관계의 데이터 무결성 보장

## 설치 및 실행

### 사전 요구사항
- JDK 17 이상
- Git

### 1. 프로젝트 클론
```bash
git clone <repository-url>
cd musinsa-category-system
```

### 2. 빌드 및 테스트
```bash
# 빌드
./gradlew clean build

# 테스트 실행
./gradlew test
```

### 3. 애플리케이션 실행
```bash
# 간편 실행 스크립트 (빌드 + 실행)
./scripts/run.sh

# 또는 직접 실행
./gradlew bootRun
```

### 4. 접속 확인
- **애플리케이션**: http://localhost:8080
- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **H2 Console**: http://localhost:8080/h2-console
  - JDBC URL: `jdbc:h2:mem:categorydb`
  - Username: `sa`
  - Password: (빈 값)

### 5. API 테스트
애플리케이션 실행 후 모든 API가 정상 동작하는지 확인할 수 있습니다:

```bash
./scripts/test-api.sh
```

이 스크립트는 다음 사항들을 자동으로 테스트합니다:
- 전체/개별 카테고리 조회
- 카테고리 생성/수정/삭제
- 예외 상황 처리
- Swagger UI 접근

## API 문서

### 기본 정보
- **Base URL**: `http://localhost:8080/api`
- **API 문서**: [Swagger UI](http://localhost:8080/swagger-ui.html)

### 주요 엔드포인트

| Method | Endpoint | 설명 |
|--------|----------|------|
| POST | `/api/categories` | 카테고리 생성 |
| PUT | `/api/categories/{id}` | 카테고리 수정 |
| DELETE | `/api/categories/{id}` | 카테고리 삭제 |
| GET | `/api/categories/{id}` | 개별 카테고리 조회 |
| GET | `/api/categories` | 전체 카테고리 조회 |

### 응답 형식
모든 API는 다음과 같은 표준 형식으로 응답합니다:

```json
{
  "status": "success|error",
  "data": { ... },
  "message": "응답 메시지",
  "error": {
    "code": "ERROR_CODE",
    "message": "오류 메시지",
    "details": "상세 정보"
  },
  "timestamp": "2024-01-01T10:00:00"
}
```

### 초기 데이터
애플리케이션 시작 시 다음과 같은 샘플 데이터가 자동으로 생성됩니다:

```
의류 (1)
├── 상의 (5)
│   ├── 티셔츠 (8)
│   ├── 셔츠 (9)
│   ├── 맨투맨 (10)
│   └── 후드티 (11)
├── 하의 (6)
│   ├── 진 (12)
│   ├── 면바지 (13)
│   └── 반바지 (14)
└── 아우터 (7)

신발 (2)
├── 운동화 (15)
├── 구두 (16)
├── 부츠 (17)
└── 샌들 (18)

가방 (3)
├── 백팩 (19)
├── 토트백 (20)
└── 크로스백 (21)

액세서리 (4)
├── 시계 (22)
├── 모자 (23)
└── 벨트 (24)
```

## 사용 예시

### 1. 카테고리 생성
```bash
# 루트 카테고리 생성
curl -X POST http://localhost:8080/api/categories \
  -H "Content-Type: application/json" \
  -d '{
    "name": "전자제품",
    "parentId": null,
    "sortOrder": 5
  }'

# 하위 카테고리 생성
curl -X POST http://localhost:8080/api/categories \
  -H "Content-Type: application/json" \
  -d '{
    "name": "스마트폰",
    "parentId": 25,
    "sortOrder": 1
  }'
```

### 2. 전체 카테고리 조회
```bash
curl http://localhost:8080/api/categories
```

### 3. 특정 카테고리 조회
```bash
curl http://localhost:8080/api/categories/1
```

### 4. 카테고리 수정
```bash
curl -X PUT http://localhost:8080/api/categories/1 \
  -H "Content-Type: application/json" \
  -d '{
    "name": "패션",
    "parentId": null,
    "sortOrder": 1
  }'
```

### 5. 카테고리 삭제
```bash
curl -X DELETE http://localhost:8080/api/categories/1
```
