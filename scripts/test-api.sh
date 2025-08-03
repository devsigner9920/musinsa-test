#!/bin/bash

# MUSINSA 카테고리 시스템 API 테스트 스크립트
# 모든 API 엔드포인트를 테스트하여 정상 동작 여부를 확인합니다.

set -e

BASE_URL="http://localhost:8080/api"
CONTENT_TYPE="Content-Type: application/json"

# 색상 정의
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# 헬퍼 함수들
print_header() {
    echo -e "${BLUE}======================================${NC}"
    echo -e "${BLUE}$1${NC}"
    echo -e "${BLUE}======================================${NC}"
}

print_success() {
    echo -e "${GREEN}✅ $1${NC}"
}

print_error() {
    echo -e "${RED}❌ $1${NC}"
}

print_info() {
    echo -e "${YELLOW}ℹ️  $1${NC}"
}

# 서버 상태 확인
check_server() {
    print_header "서버 상태 확인"
    
    if curl -s "${BASE_URL%/api}/actuator/health" > /dev/null; then
        print_success "서버가 실행 중입니다."
    else
        print_error "서버가 실행되지 않았습니다. 먼저 애플리케이션을 시작해주세요."
        echo "실행 명령: ./gradlew bootRun"
        exit 1
    fi
}

# 전체 카테고리 조회 (초기 데이터 확인)
test_get_all_categories() {
    print_header "1. 전체 카테고리 조회 테스트"
    
    response=$(curl -s -w "\n%{http_code}" "$BASE_URL/categories")
    body=$(echo "$response" | sed '$d')
    status_code=$(echo "$response" | tail -n 1)
    
    if [ "$status_code" = "200" ]; then
        print_success "전체 카테고리 조회 성공"
        echo "응답 데이터:"
        echo "$body" | jq .
    else
        print_error "전체 카테고리 조회 실패 (HTTP $status_code)"
        echo "$body"
        return 1
    fi
}

# 개별 카테고리 조회
test_get_category_by_id() {
    print_header "2. 개별 카테고리 조회 테스트"
    
    # 의류 카테고리 조회 (ID: 1)
    response=$(curl -s -w "\n%{http_code}" "$BASE_URL/categories/1")
    body=$(echo "$response" | sed $d)
    status_code=$(echo "$response" | tail -n 1)
    
    if [ "$status_code" = "200" ]; then
        print_success "개별 카테고리 조회 성공"
        echo "응답 데이터:"
        echo "$body" | jq .
    else
        print_error "개별 카테고리 조회 실패 (HTTP $status_code)"
        echo "$body"
        return 1
    fi
}

# 카테고리 생성
test_create_category() {
    print_header "3. 카테고리 생성 테스트"
    
    # 루트 카테고리 생성
    create_data='{
        "name": "전자제품",
        "parentId": null,
        "sortOrder": 5
    }'
    
    response=$(curl -s -w "\n%{http_code}" -X POST "$BASE_URL/categories" \
        -H "$CONTENT_TYPE" \
        -d "$create_data")
    body=$(echo "$response" | sed $d)
    status_code=$(echo "$response" | tail -n 1)
    
    if [ "$status_code" = "201" ]; then
        print_success "루트 카테고리 생성 성공"
        # 생성된 카테고리 ID 추출
        CREATED_CATEGORY_ID=$(echo "$body" | jq -r '.data.id')
        echo "생성된 카테고리 ID: $CREATED_CATEGORY_ID"
        echo "응답 데이터:"
        echo "$body" | jq .
    else
        print_error "루트 카테고리 생성 실패 (HTTP $status_code)"
        echo "$body"
        return 1
    fi
    
    # 하위 카테고리 생성
    subcategory_data='{
        "name": "스마트폰",
        "parentId": '$CREATED_CATEGORY_ID',
        "sortOrder": 1
    }'
    
    response=$(curl -s -w "\n%{http_code}" -X POST "$BASE_URL/categories" \
        -H "$CONTENT_TYPE" \
        -d "$subcategory_data")
    body=$(echo "$response" | sed $d)
    status_code=$(echo "$response" | tail -n 1)
    
    if [ "$status_code" = "201" ]; then
        print_success "하위 카테고리 생성 성공"
        CREATED_SUBCATEGORY_ID=$(echo "$body" | jq -r '.data.id')
        echo "생성된 하위 카테고리 ID: $CREATED_SUBCATEGORY_ID"
        echo "응답 데이터:"
        echo "$body" | jq .
    else
        print_error "하위 카테고리 생성 실패 (HTTP $status_code)"
        echo "$body"
        return 1
    fi
}

# 카테고리 수정
test_update_category() {
    print_header "4. 카테고리 수정 테스트"
    
    if [ -z "$CREATED_CATEGORY_ID" ]; then
        print_error "수정할 카테고리 ID가 없습니다."
        return 1
    fi
    
    update_data='{
        "name": "IT기기",
        "parentId": null,
        "sortOrder": 5
    }'
    
    response=$(curl -s -w "\n%{http_code}" -X PUT "$BASE_URL/categories/$CREATED_CATEGORY_ID" \
        -H "$CONTENT_TYPE" \
        -d "$update_data")
    body=$(echo "$response" | sed $d)
    status_code=$(echo "$response" | tail -n 1)
    
    if [ "$status_code" = "200" ]; then
        print_success "카테고리 수정 성공"
        echo "응답 데이터:"
        echo "$body" | jq .
    else
        print_error "카테고리 수정 실패 (HTTP $status_code)"
        echo "$body"
        return 1
    fi
}

# 카테고리 비활성화
test_deactivate_category() {
    print_header "5. 카테고리 비활성화 테스트"
    
    if [ -z "$CREATED_SUBCATEGORY_ID" ]; then
        print_error "비활성화할 카테고리 ID가 없습니다."
        return 1
    fi
    
    response=$(curl -s -w "\n%{http_code}" -X PATCH "$BASE_URL/categories/$CREATED_SUBCATEGORY_ID/deactivate")
    body=$(echo "$response" | sed $d)
    status_code=$(echo "$response" | tail -n 1)
    
    if [ "$status_code" = "200" ]; then
        print_success "카테고리 비활성화 성공"
        echo "응답 데이터:"
        echo "$body" | jq .
    else
        print_error "카테고리 비활성화 실패 (HTTP $status_code)"
        echo "$body"
        return 1
    fi
}

# 카테고리 활성화
test_activate_category() {
    print_header "6. 카테고리 활성화 테스트"
    
    if [ -z "$CREATED_SUBCATEGORY_ID" ]; then
        print_error "활성화할 카테고리 ID가 없습니다."
        return 1
    fi
    
    response=$(curl -s -w "\n%{http_code}" -X PATCH "$BASE_URL/categories/$CREATED_SUBCATEGORY_ID/activate")
    body=$(echo "$response" | sed $d)
    status_code=$(echo "$response" | tail -n 1)
    
    if [ "$status_code" = "200" ]; then
        print_success "카테고리 활성화 성공"
        echo "응답 데이터:"
        echo "$body" | jq .
    else
        print_error "카테고리 활성화 실패 (HTTP $status_code)"
        echo "$body"
        return 1
    fi
}

# 비활성 카테고리 포함 조회
test_get_all_categories_with_inactive() {
    print_header "7. 비활성 카테고리 포함 조회 테스트"
    
    response=$(curl -s -w "\n%{http_code}" "$BASE_URL/categories?includeInactive=true")
    body=$(echo "$response" | sed $d)
    status_code=$(echo "$response" | tail -n 1)
    
    if [ "$status_code" = "200" ]; then
        print_success "비활성 카테고리 포함 조회 성공"
        echo "응답 데이터:"
        echo "$body" | jq .
    else
        print_error "비활성 카테고리 포함 조회 실패 (HTTP $status_code)"
        echo "$body"
        return 1
    fi
}

# 카테고리 삭제
test_delete_category() {
    print_header "8. 카테고리 삭제 테스트"
    
    if [ -z "$CREATED_CATEGORY_ID" ]; then
        print_error "삭제할 카테고리 ID가 없습니다."
        return 1
    fi
    
    response=$(curl -s -w "\n%{http_code}" -X DELETE "$BASE_URL/categories/$CREATED_CATEGORY_ID")
    body=$(echo "$response" | sed $d)
    status_code=$(echo "$response" | tail -n 1)
    
    if [ "$status_code" = "200" ]; then
        print_success "카테고리 삭제 성공 (CASCADE로 하위 카테고리도 함께 삭제)"
        echo "응답 데이터:"
        echo "$body" | jq .
    else
        print_error "카테고리 삭제 실패 (HTTP $status_code)"
        echo "$body"
        return 1
    fi
}

# 예외 상황 테스트
test_error_scenarios() {
    print_header "9. 예외 상황 테스트"
    
    # 존재하지 않는 카테고리 조회
    print_info "존재하지 않는 카테고리 조회 테스트"
    response=$(curl -s -w "\n%{http_code}" "$BASE_URL/categories/999999")
    status_code=$(echo "$response" | tail -n 1)
    
    if [ "$status_code" = "404" ]; then
        print_success "존재하지 않는 카테고리 조회 - 404 응답 확인"
    else
        print_error "존재하지 않는 카테고리 조회 - 예상과 다른 응답: $status_code"
    fi
    
    # 잘못된 요청 데이터로 카테고리 생성
    print_info "잘못된 데이터로 카테고리 생성 테스트"
    invalid_data='{"name": "", "sortOrder": -1}'
    response=$(curl -s -w "\n%{http_code}" -X POST "$BASE_URL/categories" \
        -H "$CONTENT_TYPE" \
        -d "$invalid_data")
    status_code=$(echo "$response" | tail -n 1)
    
    if [ "$status_code" = "400" ]; then
        print_success "잘못된 데이터로 카테고리 생성 - 400 응답 확인"
    else
        print_error "잘못된 데이터로 카테고리 생성 - 예상과 다른 응답: $status_code"
    fi
}

# OpenAPI 문서 확인
test_swagger_ui() {
    print_header "10. Swagger UI 접근 테스트"
    
    swagger_url="${BASE_URL%/api}/swagger-ui.html"
    if curl -s "$swagger_url" > /dev/null; then
        print_success "Swagger UI 접근 가능"
        print_info "Swagger UI URL: $swagger_url"
    else
        print_error "Swagger UI 접근 실패"
    fi
}

# 메인 실행 함수
main() {
    print_header "MUSINSA 카테고리 시스템 API 테스트 시작"
    
    # jq 설치 확인
    if ! command -v jq &> /dev/null; then
        print_error "jq가 설치되지 않았습니다. JSON 응답 파싱을 위해 jq를 설치해주세요."
        echo "설치 방법:"
        echo "  macOS: brew install jq"
        echo "  Ubuntu: sudo apt-get install jq"
        echo "  CentOS: sudo yum install jq"
        exit 1
    fi
    
    # 테스트 실행
    check_server
    test_get_all_categories
    test_get_category_by_id
    test_create_category
    test_update_category
    test_deactivate_category
    test_activate_category
    test_get_all_categories_with_inactive
    test_delete_category
    test_error_scenarios
    test_swagger_ui
    
    print_header "모든 API 테스트 완료"
    print_success "모든 테스트가 성공적으로 완료되었습니다!"
    
    echo ""
    print_info "추가 확인 사항:"
    echo "  - Swagger UI: http://localhost:8080/swagger-ui.html"
    echo "  - H2 Console: http://localhost:8080/h2-console"
    echo "  - Actuator Health: http://localhost:8080/actuator/health"
}

# 스크립트 실행
main "$@" 