package com.musinsa.category.service

import com.musinsa.category.dto.CategoryCreateRequest
import com.musinsa.category.dto.CategoryUpdateRequest
import com.musinsa.category.exception.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.jdbc.Sql
import org.springframework.transaction.annotation.Transactional
import org.junit.jupiter.api.Assertions.*

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@Sql("classpath:test-cleanup.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class CategoryServiceScenarioTest {

    @Autowired
    private lateinit var categoryService: CategoryService

    @Test
    fun `기본 CRUD 시나리오`() {
        // Given: 루트 카테고리 생성
        val fashion = categoryService.createCategory(
            CategoryCreateRequest("패션", null, 1)
        )
        
        // When: 하위 카테고리 생성
        val clothing = categoryService.createCategory(
            CategoryCreateRequest("의류", fashion.id, 1)
        )
        
        // Then: 생성 검증
        assertNotNull(fashion.id)
        assertNotNull(clothing.id)
        assertEquals("패션", fashion.name)
        assertEquals("의류", clothing.name)
        assertEquals(fashion.id, clothing.parentId)
        
        // When: 전체 조회
        val allCategories = categoryService.getAllCategories()
        
        // Then: 트리 구조 검증
        assertEquals(1, allCategories.size, "루트 카테고리는 1개여야 함")
        assertEquals("패션", allCategories.first().name)
        assertTrue(allCategories.first().children.isNotEmpty(), "하위 카테고리가 있어야 함")
        
        println("✅ 기본 CRUD 시나리오 성공")
    }

    @Test
    fun `카테고리 수정 및 삭제 시나리오`() {
        // Given: 카테고리 생성
        val electronics = categoryService.createCategory(
            CategoryCreateRequest("전자기기", null, 1)
        )
        
        val laptop = categoryService.createCategory(
            CategoryCreateRequest("노트북", electronics.id, 1)
        )
        
        categoryService.createCategory(
            CategoryCreateRequest("휴대폰", electronics.id, 2)
        )
        
        // When: 카테고리 수정
        val updatedLaptop = categoryService.updateCategory(
            laptop.id,
            CategoryUpdateRequest("랩톱컴퓨터", electronics.id, 1)
        )
        
        // Then: 수정 검증
        assertEquals("랩톱컴퓨터", updatedLaptop.name)
        
        // When: 개별 카테고리 조회
        val retrievedCategory = categoryService.getCategoryById(electronics.id)
        assertEquals("전자기기", retrievedCategory.name)
        println("전자기기 카테고리 자식 수: ${retrievedCategory.children.size}")
        retrievedCategory.children.forEach { child ->
            println("자식: ${child.name}, ID: ${child.id}")
        }
        assertEquals(2, retrievedCategory.children.size)
        
        // When: 카테고리 삭제 (하위 카테고리와 함께)
        val deleteResult = categoryService.deleteCategory(electronics.id)
        assertEquals(electronics.id, deleteResult.deletedCategoryId)
        assertEquals(2, deleteResult.deletedChildrenCount)
        
        // Then: 삭제 검증
        assertThrows<CategoryNotFoundException> {
            categoryService.getCategoryById(electronics.id)
        }
        
        println("✅ 수정 및 삭제 시나리오 성공")
    }

    @Test
    fun `카테고리 생성 제약 조건 검증 시나리오`() {
        // Given: 부모 카테고리 생성
        val parent = categoryService.createCategory(
            CategoryCreateRequest("부모", null, 1)
        )
        
        // When & Then: 중복 이름 검증
        assertThrows<DuplicateCategoryNameException> {
            categoryService.createCategory(
                CategoryCreateRequest("부모", null, 2)
            )
        }
        
        // When & Then: 존재하지 않는 부모 검증
        assertThrows<ParentCategoryNotFoundException> {
            categoryService.createCategory(
                CategoryCreateRequest("자식", 999L, 1)
            )
        }
        
        // When & Then: 최대 깊이 검증 (6단계까지 생성 시도)
        var currentParent = parent
        for (depth in 1..5) {
            currentParent = categoryService.createCategory(
                CategoryCreateRequest("레벨$depth", currentParent.id, 1)
            )
        }
        
        // 6번째 레벨 생성 시 실패해야 함
        assertThrows<MaxDepthExceededException> {
            categoryService.createCategory(
                CategoryCreateRequest("레벨6", currentParent.id, 1)
            )
        }
        
        println("✅ 제약 조건 검증 시나리오 성공")
    }

    @Test
    fun `순환 참조 방지 검증 시나리오`() {
        // Given: 3단계 계층 구조 생성
        val level1 = categoryService.createCategory(
            CategoryCreateRequest("1단계", null, 1)
        )
        
        val level2 = categoryService.createCategory(
            CategoryCreateRequest("2단계", level1.id, 1)
        )
        
        val level3 = categoryService.createCategory(
            CategoryCreateRequest("3단계", level2.id, 1)
        )
        
        // When & Then: 자기 자신을 부모로 설정 시도
        assertThrows<CircularReferenceException> {
            categoryService.updateCategory(
                level2.id,
                CategoryUpdateRequest("2단계", level2.id, 1)
            )
        }
        
        // When & Then: 하위 카테고리를 부모로 설정 시도
        assertThrows<CircularReferenceException> {
            categoryService.updateCategory(
                level1.id,
                CategoryUpdateRequest("1단계", level3.id, 1)
            )
        }
        
        println("✅ 순환 참조 방지 검증 성공")
    }
}