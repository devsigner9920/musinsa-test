-- 초기 카테고리 데이터 삽입 (복합적이고 깊은 계층 구조)

-- ========================================
-- 1단계: 루트 카테고리들 (depth 0)
-- ========================================
INSERT INTO category (name, parent_id, depth, sort_order, is_active, created_at, updated_at) 
VALUES ('의류', NULL, 0, 1, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO category (name, parent_id, depth, sort_order, is_active, created_at, updated_at) 
VALUES ('신발', NULL, 0, 2, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO category (name, parent_id, depth, sort_order, is_active, created_at, updated_at) 
VALUES ('가방', NULL, 0, 3, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO category (name, parent_id, depth, sort_order, is_active, created_at, updated_at) 
VALUES ('액세서리', NULL, 0, 4, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO category (name, parent_id, depth, sort_order, is_active, created_at, updated_at) 
VALUES ('스포츠/레저', NULL, 0, 5, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- ========================================
-- 2단계: 의류 하위 카테고리들 (depth 1)
-- ========================================
INSERT INTO category (name, parent_id, depth, sort_order, is_active, created_at, updated_at) 
VALUES ('남성의류', 1, 1, 1, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO category (name, parent_id, depth, sort_order, is_active, created_at, updated_at) 
VALUES ('여성의류', 1, 1, 2, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO category (name, parent_id, depth, sort_order, is_active, created_at, updated_at) 
VALUES ('아동의류', 1, 1, 3, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO category (name, parent_id, depth, sort_order, is_active, created_at, updated_at) 
VALUES ('언더웨어', 1, 1, 4, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- ========================================
-- 3단계: 남성의류 하위 카테고리들 (depth 2)
-- ========================================
INSERT INTO category (name, parent_id, depth, sort_order, is_active, created_at, updated_at) 
VALUES ('상의', 6, 2, 1, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO category (name, parent_id, depth, sort_order, is_active, created_at, updated_at) 
VALUES ('하의', 6, 2, 2, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO category (name, parent_id, depth, sort_order, is_active, created_at, updated_at) 
VALUES ('아우터', 6, 2, 3, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO category (name, parent_id, depth, sort_order, is_active, created_at, updated_at) 
VALUES ('정장', 6, 2, 4, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- ========================================
-- 4단계: 상의 하위 카테고리들 (depth 3)
-- ========================================
INSERT INTO category (name, parent_id, depth, sort_order, is_active, created_at, updated_at) 
VALUES ('티셔츠', 10, 3, 1, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO category (name, parent_id, depth, sort_order, is_active, created_at, updated_at) 
VALUES ('셔츠', 10, 3, 2, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO category (name, parent_id, depth, sort_order, is_active, created_at, updated_at) 
VALUES ('맨투맨', 10, 3, 3, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO category (name, parent_id, depth, sort_order, is_active, created_at, updated_at) 
VALUES ('후드티', 10, 3, 4, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO category (name, parent_id, depth, sort_order, is_active, created_at, updated_at) 
VALUES ('니트/스웨터', 10, 3, 5, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO category (name, parent_id, depth, sort_order, is_active, created_at, updated_at) 
VALUES ('조끼/베스트', 10, 3, 6, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- ========================================
-- 5단계: 티셔츠 하위 카테고리들 (depth 4 - 최대 깊이)
-- ========================================
INSERT INTO category (name, parent_id, depth, sort_order, is_active, created_at, updated_at) 
VALUES ('반팔티셔츠', 14, 4, 1, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO category (name, parent_id, depth, sort_order, is_active, created_at, updated_at) 
VALUES ('긴팔티셔츠', 14, 4, 2, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO category (name, parent_id, depth, sort_order, is_active, created_at, updated_at) 
VALUES ('민소매티셔츠', 14, 4, 3, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO category (name, parent_id, depth, sort_order, is_active, created_at, updated_at) 
VALUES ('프린팅티셔츠', 14, 4, 4, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- ========================================
-- 3단계: 여성의류 하위 카테고리들 (depth 2)
-- ========================================
INSERT INTO category (name, parent_id, depth, sort_order, is_active, created_at, updated_at) 
VALUES ('블라우스/셔츠', 7, 2, 1, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO category (name, parent_id, depth, sort_order, is_active, created_at, updated_at) 
VALUES ('니트/가디건', 7, 2, 2, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO category (name, parent_id, depth, sort_order, is_active, created_at, updated_at) 
VALUES ('원피스', 7, 2, 3, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO category (name, parent_id, depth, sort_order, is_active, created_at, updated_at) 
VALUES ('스커트', 7, 2, 4, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO category (name, parent_id, depth, sort_order, is_active, created_at, updated_at) 
VALUES ('바지', 7, 2, 5, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO category (name, parent_id, depth, sort_order, is_active, created_at, updated_at) 
VALUES ('코트/아우터', 7, 2, 6, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- ========================================
-- 2단계: 신발 하위 카테고리들 (depth 1)
-- ========================================
INSERT INTO category (name, parent_id, depth, sort_order, is_active, created_at, updated_at) 
VALUES ('운동화', 2, 1, 1, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO category (name, parent_id, depth, sort_order, is_active, created_at, updated_at) 
VALUES ('구두', 2, 1, 2, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO category (name, parent_id, depth, sort_order, is_active, created_at, updated_at) 
VALUES ('부츠', 2, 1, 3, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO category (name, parent_id, depth, sort_order, is_active, created_at, updated_at) 
VALUES ('샌들/슬리퍼', 2, 1, 4, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO category (name, parent_id, depth, sort_order, is_active, created_at, updated_at) 
VALUES ('하이힐', 2, 1, 5, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- ========================================
-- 3단계: 운동화 하위 카테고리들 (depth 2)
-- ========================================
INSERT INTO category (name, parent_id, depth, sort_order, is_active, created_at, updated_at) 
VALUES ('러닝화', 28, 2, 1, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO category (name, parent_id, depth, sort_order, is_active, created_at, updated_at) 
VALUES ('농구화', 28, 2, 2, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO category (name, parent_id, depth, sort_order, is_active, created_at, updated_at) 
VALUES ('축구화', 28, 2, 3, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO category (name, parent_id, depth, sort_order, is_active, created_at, updated_at) 
VALUES ('캐주얼 스니커즈', 28, 2, 4, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO category (name, parent_id, depth, sort_order, is_active, created_at, updated_at) 
VALUES ('트레킹화', 28, 2, 5, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- ========================================
-- 2단계: 가방 하위 카테고리들 (depth 1)
-- ========================================
INSERT INTO category (name, parent_id, depth, sort_order, is_active, created_at, updated_at) 
VALUES ('백팩/배낭', 3, 1, 1, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO category (name, parent_id, depth, sort_order, is_active, created_at, updated_at) 
VALUES ('토트백', 3, 1, 2, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO category (name, parent_id, depth, sort_order, is_active, created_at, updated_at) 
VALUES ('크로스백', 3, 1, 3, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO category (name, parent_id, depth, sort_order, is_active, created_at, updated_at) 
VALUES ('클러치백', 3, 1, 4, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO category (name, parent_id, depth, sort_order, is_active, created_at, updated_at) 
VALUES ('여행가방', 3, 1, 5, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO category (name, parent_id, depth, sort_order, is_active, created_at, updated_at) 
VALUES ('노트북가방', 3, 1, 6, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- ========================================
-- 2단계: 스포츠/레저 하위 카테고리들 (depth 1)
-- ========================================
INSERT INTO category (name, parent_id, depth, sort_order, is_active, created_at, updated_at) 
VALUES ('피트니스', 5, 1, 1, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO category (name, parent_id, depth, sort_order, is_active, created_at, updated_at) 
VALUES ('수영', 5, 1, 2, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO category (name, parent_id, depth, sort_order, is_active, created_at, updated_at) 
VALUES ('골프', 5, 1, 3, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO category (name, parent_id, depth, sort_order, is_active, created_at, updated_at) 
VALUES ('등산/아웃도어', 5, 1, 4, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO category (name, parent_id, depth, sort_order, is_active, created_at, updated_at) 
VALUES ('자전거', 5, 1, 5, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- ========================================
-- 테스트용 비활성 카테고리들
-- ========================================
INSERT INTO category (name, parent_id, depth, sort_order, is_active, created_at, updated_at) 
VALUES ('비활성카테고리1', NULL, 0, 99, false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO category (name, parent_id, depth, sort_order, is_active, created_at, updated_at) 
VALUES ('비활성하위카테고리', 49, 1, 1, false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP); 