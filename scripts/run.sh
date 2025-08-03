#!/bin/bash

# MUSINSA 카테고리 시스템 실행 스크립트

# 색상 정의
GREEN='\033[0;32m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

print_info() {
    echo -e "${BLUE}ℹ️  $1${NC}"
}

print_success() {
    echo -e "${GREEN}✅ $1${NC}"
}

echo "======================================"
echo "MUSINSA 카테고리 시스템 시작"
echo "======================================"

print_info "기존 프로세스를 정리합니다..."
pkill -f "bootRun" || true
sleep 2

print_info "빌드를 진행합니다..."
./gradlew clean build

if [ $? -eq 0 ]; then
    print_success "빌드가 완료되었습니다."
else
    echo "❌ 빌드에 실패했습니다."
    exit 1
fi

print_info "애플리케이션을 시작합니다..."
print_info "접속 URL:"
echo "  - 애플리케이션: http://localhost:8080"
echo "  - Swagger UI: http://localhost:8080/swagger-ui.html"
echo "  - H2 Console: http://localhost:8080/h2-console"
echo "    * JDBC URL: jdbc:h2:mem:categorydb"
echo "    * Username: sa"
echo "    * Password: (빈 값)"
echo ""
print_success "데이터베이스는 매번 새로 초기화됩니다."
echo ""
print_info "종료하려면 Ctrl+C를 누르세요."
echo ""

# 애플리케이션 실행
./gradlew bootRun 