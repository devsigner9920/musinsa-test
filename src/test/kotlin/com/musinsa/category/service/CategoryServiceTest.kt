package com.musinsa.category.service

import com.musinsa.category.dto.CategoryCreateRequest
import com.musinsa.category.entity.Category
import com.musinsa.category.exception.CategoryNotFoundException
import com.musinsa.category.repository.CategoryRepository
import io.mockk.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.data.repository.findByIdOrNull
import java.time.LocalDateTime
import org.junit.jupiter.api.Assertions.*

class CategoryServiceTest {

    private val categoryRepository = mockk<CategoryRepository>()
    private val categoryService = CategoryServiceImpl(categoryRepository)

    @BeforeEach
    fun setUp() {
        clearAllMocks()
    }

    @Test
    fun `카테고리 생성 성공 테스트`() {
        // Given
        val request = CategoryCreateRequest(
            name = "의류",
            parentId = null,
            sortOrder = 1
        )

        every { categoryRepository.existsByParentIdAndName(null, "의류") } returns false
        every { categoryRepository.save(any<Category>()) } returns Category.create("의류", null, 0, 1).copy(
            id = 1L,
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )

        // When
        val result = categoryService.createCategory(request)

        // Then
        assertNotNull(result)
        assertEquals("의류", result.name)
        assertEquals(null, result.parentId)
        assertEquals(0, result.depth)
        assertEquals(1, result.sortOrder)


        verify { categoryRepository.save(any<Category>()) }
    }

    @Test
    fun `부모 카테고리가 있는 하위 카테고리 생성 성공 테스트`() {
        // Given
        val parentCategory = Category.create("의류", null, 0, 1).copy(id = 1L)
        val request = CategoryCreateRequest(
            name = "상의",
            parentId = 1L,
            sortOrder = 1
        )

        every { categoryRepository.findByIdOrNull(1L) } returns parentCategory
        every { categoryRepository.existsByParentIdAndName(1L, "상의") } returns false
        every { categoryRepository.save(any<Category>()) } returns Category.create("상의", 1L, 1, 1).copy(
            id = 2L,
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )

        // When
        val result = categoryService.createCategory(request)

        // Then
        assertNotNull(result)
        assertEquals("상의", result.name)
        assertEquals(1L, result.parentId)
        assertEquals(1, result.depth)
        assertEquals(1, result.sortOrder)


        verify { categoryRepository.save(any<Category>()) }
    }

    @Test
    fun `카테고리 조회 성공 테스트`() {
        // Given
        val category = Category.create("의류", null, 0, 1).copy(
            id = 1L,
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )

        every { categoryRepository.findByIdOrNull(1L) } returns category
        every { categoryRepository.findByParentIdOrderBySortOrderAsc(1L) } returns emptyList()

        // When
        val result = categoryService.getCategoryById(1L)

        // Then
        assertNotNull(result)
        assertEquals(1L, result.id)
        assertEquals("의류", result.name)
    }

    @Test
    fun `존재하지 않는 카테고리 조회 실패 테스트`() {
        // Given
        every { categoryRepository.findByIdOrNull(999L) } returns null

        // When & Then
        assertThrows<CategoryNotFoundException> {
            categoryService.getCategoryById(999L)
        }
    }

    @Test
    fun `전체 카테고리 조회 테스트`() {
        // Given
        val categories = listOf(
            Category.create("의류", null, 0, 1).copy(id = 1L, createdAt = LocalDateTime.now(), updatedAt = LocalDateTime.now()),
            Category.create("신발", null, 0, 2).copy(id = 2L, createdAt = LocalDateTime.now(), updatedAt = LocalDateTime.now())
        )

        every { categoryRepository.findAllByOrderByDepthAscSortOrderAsc() } returns categories

        // When
        val result = categoryService.getAllCategories()

        // Then
        assertNotNull(result)
        assertEquals(2, result.size)
        assertEquals("의류", result[0].name)
        assertEquals("신발", result[1].name)
    }


} 