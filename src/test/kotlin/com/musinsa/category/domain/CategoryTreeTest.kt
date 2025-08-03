package com.musinsa.category.domain

import com.musinsa.category.entity.Category
import com.musinsa.category.exception.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.Assertions.*
import java.time.LocalDateTime

class CategoryTreeTest {

    @Test
    fun `빈 트리 생성 테스트`() {
        // Given & When
        val tree = CategoryTree.empty()

        // Then
        assertTrue(tree.getAllCategories().isEmpty())
        assertTrue(tree.getRootCategories().isEmpty())
    }

    @Test
    fun `카테고리 리스트로부터 트리 생성 테스트`() {
        // Given
        val categories = listOf(
            Category.create("의류", null, 0, 1).copy(id = 1L),
            Category.create("상의", 1L, 1, 1).copy(id = 2L),
            Category.create("하의", 1L, 1, 2).copy(id = 3L),
            Category.create("신발", null, 0, 2).copy(id = 4L)
        )

        // When
        val tree = CategoryTree.from(categories)

        // Then
        assertEquals(4, tree.getAllCategories().size)
        assertEquals(2, tree.getRootCategories().size)
        assertEquals(2, tree.getChildren(1L).size)
        assertEquals(0, tree.getChildren(2L).size)
    }

    @Test
    fun `카테고리 ID로 조회 테스트`() {
        // Given
        val categories = listOf(
            Category.create("의류", null, 0, 1).copy(id = 1L),
            Category.create("상의", 1L, 1, 1).copy(id = 2L)
        )
        val tree = CategoryTree.from(categories)

        // When
        val category = tree.findById(1L)
        val notFound = tree.findById(999L)

        // Then
        assertNotNull(category)
        assertEquals("의류", category?.name)
        assertNull(notFound)
    }

    @Test
    fun `하위 카테고리 조회 테스트`() {
        // Given
        val categories = listOf(
            Category.create("의류", null, 0, 1).copy(id = 1L),
            Category.create("상의", 1L, 1, 1).copy(id = 2L),
            Category.create("하의", 1L, 1, 2).copy(id = 3L),
            Category.create("티셔츠", 2L, 2, 1).copy(id = 4L)
        )
        val tree = CategoryTree.from(categories)

        // When
        val descendants = tree.getDescendants(1L)

        // Then
        assertEquals(3, descendants.size)
        val descendantNames = descendants.map { it.name }
        assertTrue(descendantNames.contains("상의"))
        assertTrue(descendantNames.contains("하의"))
        assertTrue(descendantNames.contains("티셔츠"))
    }

    @Test
    fun `상위 카테고리 조회 테스트`() {
        // Given
        val categories = listOf(
            Category.create("의류", null, 0, 1).copy(id = 1L),
            Category.create("상의", 1L, 1, 1).copy(id = 2L),
            Category.create("티셔츠", 2L, 2, 1).copy(id = 3L)
        )
        val tree = CategoryTree.from(categories)

        // When
        val ancestors = tree.getAncestors(3L)

        // Then
        assertEquals(2, ancestors.size)
        assertEquals("의류", ancestors[0].name)
        assertEquals("상의", ancestors[1].name)
    }

    @Test
    fun `카테고리 경로 조회 테스트`() {
        // Given
        val categories = listOf(
            Category.create("의류", null, 0, 1).copy(id = 1L),
            Category.create("상의", 1L, 1, 1).copy(id = 2L),
            Category.create("티셔츠", 2L, 2, 1).copy(id = 3L)
        )
        val tree = CategoryTree.from(categories)

        // When
        val path = tree.getCategoryPath(3L)

        // Then
        assertEquals("의류 > 상의 > 티셔츠", path)
    }

    @Test
    fun `카테고리 및 하위 카테고리 개수 조회 테스트`() {
        // Given
        val categories = listOf(
            Category.create("의류", null, 0, 1).copy(id = 1L),
            Category.create("상의", 1L, 1, 1).copy(id = 2L),
            Category.create("하의", 1L, 1, 2).copy(id = 3L),
            Category.create("티셔츠", 2L, 2, 1).copy(id = 4L)
        )
        val tree = CategoryTree.from(categories)

        // When
        val count = tree.countCategoryAndDescendants(1L)

        // Then
        assertEquals(4L, count) // 의류 + 상의 + 하의 + 티셔츠
    }

    @Test
    fun `하위 카테고리 여부 확인 테스트`() {
        // Given
        val categories = listOf(
            Category.create("의류", null, 0, 1).copy(id = 1L),
            Category.create("상의", 1L, 1, 1).copy(id = 2L),
            Category.create("티셔츠", 2L, 2, 1).copy(id = 3L),
            Category.create("신발", null, 0, 2).copy(id = 4L)
        )
        val tree = CategoryTree.from(categories)

        // When & Then
        assertTrue(tree.isDescendantOf(3L, 1L))  // 티셔츠는 의류의 하위
        assertTrue(tree.isDescendantOf(2L, 1L))  // 상의는 의류의 하위
        assertFalse(tree.isDescendantOf(1L, 3L)) // 의류는 티셔츠의 하위가 아님
        assertFalse(tree.isDescendantOf(4L, 1L)) // 신발은 의류의 하위가 아님
    }

    @Test
    fun `중복 이름 검증 테스트`() {
        // Given
        val categories = listOf(
            Category.create("의류", null, 0, 1).copy(id = 1L),
            Category.create("상의", 1L, 1, 1).copy(id = 2L),
            Category.create("하의", 1L, 1, 2).copy(id = 3L)
        )
        val tree = CategoryTree.from(categories)

        // When & Then
        assertTrue(tree.validateDuplicateName("상의", 1L)) // 같은 부모 하에 중복 이름
        assertFalse(tree.validateDuplicateName("바지", 1L)) // 중복되지 않는 이름
        assertFalse(tree.validateDuplicateName("상의", null)) // 다른 부모 하에서는 중복 아님
        assertFalse(tree.validateDuplicateName("상의", 1L, excludeId = 2L)) // 자기 자신 제외
    }

    @Test
    fun `통계 조회 테스트`() {
        // Given
        val categories = listOf(
            Category.create("의류", null, 0, 1).copy(id = 1L),
            Category.create("상의", 1L, 1, 1).copy(id = 2L),
            Category.create("하의", 1L, 1, 2).copy(id = 3L),
            Category.create("신발", null, 0, 2).copy(id = 4L)
        )
        val tree = CategoryTree.from(categories)

        // When
        val stats = tree.getStatistics()

        // Then
        assertEquals(4, stats["totalCategories"])
        assertEquals(2, stats["rootCategories"])
        assertEquals(1, stats["maxDepth"])
        assertEquals(4, stats["activeCategories"])
        assertEquals(0, stats["inactiveCategories"])
        
        val depthDistribution = stats["depthDistribution"] as Map<*, *>
        assertEquals(2, depthDistribution[0])
        assertEquals(2, depthDistribution[1])
    }

    @Test
    fun `트리 구조 검증 테스트 - 정상 케이스`() {
        // Given
        val categories = listOf(
            Category.create("의류", null, 0, 1).copy(id = 1L),
            Category.create("상의", 1L, 1, 1).copy(id = 2L),
            Category.create("하의", 1L, 1, 2).copy(id = 3L)
        )
        val tree = CategoryTree.from(categories)

        // When
        val errors = tree.validateTreeStructure()

        // Then
        assertTrue(errors.isEmpty())
    }

    @Test
    fun `트리 구조 검증 테스트 - 깊이 불일치`() {
        // Given
        val categories = listOf(
            Category.create("의류", null, 0, 1).copy(id = 1L),
            Category.create("상의", 1L, 2, 1).copy(id = 2L) // 잘못된 깊이: 2 (정상: 1)
        )
        val tree = CategoryTree.from(categories)

        // When
        val errors = tree.validateTreeStructure()

        // Then
        assertEquals(1, errors.size)
        assertTrue(errors[0].contains("깊이 불일치"))
        assertTrue(errors[0].contains("카테고리 ID 2"))
    }
} 