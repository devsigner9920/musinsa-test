#!/bin/bash

# 복잡한 시나리오 API 테스트 스크립트

# 색상 정의
GREEN='\033[0;32m'
BLUE='\033[0;34m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

BASE_URL="http://localhost:8080/api/categories"

print_test() {
    echo -e "${BLUE}🧪 $1${NC}"
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
    print_test "서버 상태 확인"
    response=$(curl -s -o /dev/null -w "%{http_code}" http://localhost:8080/actuator/health)
    if [ "$response" = "200" ]; then
        print_success "서버가 정상적으로 실행중입니다"
        return 0
    else
        print_error "서버가 실행되지 않았습니다 (HTTP $response)"
        exit 1
    fi
}

# JSON 응답에서 특정 필드 추출
extract_json() {
    echo "$1" | jq -r "$2" 2>/dev/null || echo ""
}

# 1. 계층 구조 조회 및 분석
test_hierarchical_structure() {
    print_test "계층 구조 조회 및 분석"
    
    response=$(curl -s "$BASE_URL")
    status=$(extract_json "$response" ".status")
    
    if [ "$status" = "success" ]; then
        # 루트 카테고리 수 확인
        root_count=$(echo "$response" | jq '[.data[] | select(.parentId == null)] | length')
        print_success "루트 카테고리 $root_count개 조회됨"
        
        # 최대 깊이 확인
        max_depth=$(echo "$response" | jq '[.data[] | .depth] | max')
        print_success "최대 깊이: $max_depth"
        
        # 각 깊이별 카테고리 수
        for depth in $(seq 0 $max_depth); do
            count=$(echo "$response" | jq "[.data[] | select(.depth == $depth)] | length")
            print_info "깊이 $depth: $count개 카테고리"
        done
    else
        print_error "계층 구조 조회 실패"
    fi
}

# 2. 깊은 계층 카테고리 상세 조회
test_deep_category_details() {
    print_test "깊은 계층 카테고리 상세 조회"
    
    # 전체 카테고리에서 depth 4인 카테고리 찾기
    all_categories=$(curl -s "$BASE_URL/flat")
    deep_category_id=$(echo "$all_categories" | jq -r '[.data[] | select(.depth == 4)] | .[0] | .id // empty')
    
    if [ -n "$deep_category_id" ]; then
        response=$(curl -s "$BASE_URL/$deep_category_id")
        name=$(extract_json "$response" ".data.name")
        depth=$(extract_json "$response" ".data.depth")
        children_count=$(echo "$response" | jq '.data.children | length // 0')
        
        print_success "깊은 계층 카테고리 조회: $name (depth: $depth, 하위: $children_count개)"
    else
        print_info "깊이 4 카테고리가 없습니다"
    fi
}

# 3. 카테고리 경로 조회 테스트
test_category_path() {
    print_test "카테고리 경로 조회"
    
    # 전체 카테고리에서 깊은 계층 카테고리 찾기
    all_categories=$(curl -s "$BASE_URL/flat")
    deep_category_id=$(echo "$all_categories" | jq -r '[.data[] | select(.depth >= 3)] | .[0] | .id // empty')
    
    if [ -n "$deep_category_id" ]; then
        response=$(curl -s "$BASE_URL/$deep_category_id/path")
        path=$(extract_json "$response" ".data")
        status=$(extract_json "$response" ".status")
        
        if [ "$status" = "success" ]; then
            print_success "카테고리 경로: $path"
        else
            print_error "경로 조회 실패"
        fi
    else
        print_info "깊은 계층 카테고리가 없습니다"
    fi
}

# 4. 카테고리 통계 조회
test_category_statistics() {
    print_test "카테고리 통계 조회"
    
    response=$(curl -s "$BASE_URL/statistics")
    status=$(extract_json "$response" ".status")
    
    if [ "$status" = "success" ]; then
        total=$(extract_json "$response" ".data.totalCategories")
        roots=$(extract_json "$response" ".data.rootCategories")
        max_depth=$(extract_json "$response" ".data.maxDepth")
        active=$(extract_json "$response" ".data.activeCategories")
        inactive=$(extract_json "$response" ".data.inactiveCategories")
        
        print_success "통계 조회 성공:"
        print_info "  - 총 카테고리: $total개"
        print_info "  - 루트 카테고리: $roots개"
        print_info "  - 최대 깊이: $max_depth"
        print_info "  - 활성 카테고리: $active개"
        print_info "  - 비활성 카테고리: $inactive개"
        
        # 깊이별 분포
        depths=$(echo "$response" | jq -r '.data.depthDistribution | keys[]' | sort -n)
        print_info "  - 깊이별 분포:"
        for depth in $depths; do
            count=$(echo "$response" | jq -r ".data.depthDistribution.\"$depth\"")
            print_info "    * 깊이 $depth: $count개"
        done
    else
        print_error "통계 조회 실패"
    fi
}

# 5. 트리 구조 검증
test_tree_validation() {
    print_test "트리 구조 검증"
    
    response=$(curl -s "$BASE_URL/validate")
    status=$(extract_json "$response" ".status")
    
    if [ "$status" = "success" ]; then
        is_valid=$(extract_json "$response" ".data.isValid")
        error_count=$(extract_json "$response" ".data.errorCount")
        
        if [ "$is_valid" = "true" ]; then
            print_success "트리 구조가 유효합니다 (에러: $error_count개)"
        else
            print_error "트리 구조에 문제가 있습니다 (에러: $error_count개)"
            echo "$response" | jq -r '.data.errors[]' | while read error; do
                print_error "  - $error"
            done
        fi
    else
        print_error "트리 검증 실패"
    fi
}

# 6. 복잡한 카테고리 생성 테스트
test_complex_category_creation() {
    print_test "복잡한 카테고리 생성"
    
    # 의류 카테고리 찾기
    all_categories=$(curl -s "$BASE_URL/flat")
    clothing_id=$(echo "$all_categories" | jq -r '[.data[] | select(.name == "의류")] | .[0] | .id // empty')
    
    if [ -n "$clothing_id" ]; then
        # 새로운 하위 카테고리 생성
        new_category=$(curl -s -X POST "$BASE_URL" \
            -H "Content-Type: application/json" \
            -d "{
                \"name\": \"테스트복합카테고리_$(date +%s)\",
                \"parentId\": $clothing_id,
                \"sortOrder\": 999
            }")
        
        status=$(extract_json "$new_category" ".status")
        if [ "$status" = "success" ]; then
            new_id=$(extract_json "$new_category" ".data.id")
            name=$(extract_json "$new_category" ".data.name")
            print_success "새 카테고리 생성: $name (ID: $new_id)"
            
            # 생성된 카테고리에 하위 카테고리들 추가
            for i in {1..3}; do
                sub_category=$(curl -s -X POST "$BASE_URL" \
                    -H "Content-Type: application/json" \
                    -d "{
                        \"name\": \"하위테스트$i\",
                        \"parentId\": $new_id,
                        \"sortOrder\": $i
                    }")
                
                sub_status=$(extract_json "$sub_category" ".status")
                if [ "$sub_status" = "success" ]; then
                    sub_name=$(extract_json "$sub_category" ".data.name")
                    print_success "  하위 카테고리 생성: $sub_name"
                else
                    print_error "  하위 카테고리 $i 생성 실패"
                fi
            done
        else
            print_error "카테고리 생성 실패"
        fi
    else
        print_error "의류 카테고리를 찾을 수 없습니다"
    fi
}

# 7. 최대 깊이 제한 테스트
test_max_depth_limit() {
    print_test "최대 깊이 제한 테스트"
    
    # depth 4 카테고리 찾기
    all_categories=$(curl -s "$BASE_URL/flat")
    level4_id=$(echo "$all_categories" | jq -r '[.data[] | select(.depth == 4)] | .[0] | .id // empty')
    
    if [ -n "$level4_id" ]; then
        # depth 5 카테고리 생성 시도 (실패해야 함)
        response=$(curl -s -X POST "$BASE_URL" \
            -H "Content-Type: application/json" \
            -d "{
                \"name\": \"초과깊이테스트\",
                \"parentId\": $level4_id,
                \"sortOrder\": 1
            }")
        
        status=$(extract_json "$response" ".status")
        if [ "$status" = "error" ]; then
            print_success "최대 깊이 제한이 올바르게 작동합니다"
        else
            print_error "최대 깊이 제한이 작동하지 않습니다"
        fi
    else
        print_info "깊이 4 카테고리가 없어 테스트를 건너뜁니다"
    fi
}

# 8. 카테고리 활성화/비활성화 테스트
test_activation_deactivation() {
    print_test "카테고리 활성화/비활성화"
    
    # 활성 카테고리 찾기 (루트가 아닌)
    all_categories=$(curl -s "$BASE_URL/flat")
    active_id=$(echo "$all_categories" | jq -r '[.data[] | select(.isActive == true and .parentId != null)] | .[0] | .id // empty')
    
    if [ -n "$active_id" ]; then
        # 비활성화
        deactivate_response=$(curl -s -X PATCH "$BASE_URL/$active_id/deactivate")
        deactivate_status=$(extract_json "$deactivate_response" ".status")
        
        if [ "$deactivate_status" = "success" ]; then
            is_active=$(extract_json "$deactivate_response" ".data.isActive")
            if [ "$is_active" = "false" ]; then
                print_success "카테고리 비활성화 성공"
                
                # 다시 활성화
                activate_response=$(curl -s -X PATCH "$BASE_URL/$active_id/activate")
                activate_status=$(extract_json "$activate_response" ".status")
                
                if [ "$activate_status" = "success" ]; then
                    is_active_again=$(extract_json "$activate_response" ".data.isActive")
                    if [ "$is_active_again" = "true" ]; then
                        print_success "카테고리 재활성화 성공"
                    else
                        print_error "카테고리 재활성화 실패"
                    fi
                else
                    print_error "카테고리 활성화 API 호출 실패"
                fi
            else
                print_error "카테고리가 비활성화되지 않았습니다"
            fi
        else
            print_error "카테고리 비활성화 실패"
        fi
    else
        print_error "테스트할 활성 카테고리를 찾을 수 없습니다"
    fi
}

# 9. 성능 테스트 (대량 요청)
test_performance() {
    print_test "성능 테스트 (대량 요청)"
    
    start_time=$(date +%s%N)
    
    # 10번 연속 전체 카테고리 조회
    for i in {1..10}; do
        curl -s "$BASE_URL" > /dev/null
    done
    
    end_time=$(date +%s%N)
    duration=$(( (end_time - start_time) / 1000000 )) # 밀리초로 변환
    
    print_success "10회 연속 조회 완료: ${duration}ms"
    
    if [ $duration -lt 5000 ]; then
        print_success "성능이 양호합니다 (5초 이내)"
    else
        print_error "성능이 느립니다 (5초 초과)"
    fi
}

# 메인 실행
main() {
    echo "=========================================="
    echo "MUSINSA 카테고리 시스템 고급 시나리오 테스트"
    echo "=========================================="
    echo ""
    
    check_server
    echo ""
    
    test_hierarchical_structure
    echo ""
    
    test_deep_category_details
    echo ""
    
    test_category_path
    echo ""
    
    test_category_statistics
    echo ""
    
    test_tree_validation
    echo ""
    
    test_complex_category_creation
    echo ""
    
    test_max_depth_limit
    echo ""
    
    test_activation_deactivation
    echo ""
    
    test_performance
    echo ""
    
    print_success "모든 고급 시나리오 테스트가 완료되었습니다!"
}

# 스크립트 실행
main 