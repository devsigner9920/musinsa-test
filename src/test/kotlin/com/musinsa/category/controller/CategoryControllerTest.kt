package com.musinsa.category.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.musinsa.category.dto.CategoryCreateRequest
import com.musinsa.category.dto.CategoryResponse
import com.musinsa.category.service.CategoryService
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import java.time.LocalDateTime

@WebMvcTest(CategoryController::class)
@ActiveProfiles("test")
class CategoryControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @MockkBean
    private lateinit var categoryService: CategoryService

    @Test
    fun `카테고리 생성 API 테스트`() {
        // Given
        val request = CategoryCreateRequest(
            name = "의류",
            parentId = null,
            sortOrder = 1
        )

        val response = CategoryResponse(
            id = 1L,
            name = "의류",
            parentId = null,
            depth = 0,
            sortOrder = 1,
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now(),
            children = emptyList()
        )

        every { categoryService.createCategory(any()) } returns response

        // When & Then
        mockMvc.perform(
            post("/api/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.status").value("success"))
            .andExpect(jsonPath("$.data.name").value("의류"))
    }

    @Test
    fun `전체 카테고리 조회 API 테스트`() {
        // Given
        val response = listOf(
            CategoryResponse(
                id = 1L,
                name = "의류",
                parentId = null,
                depth = 0,
                sortOrder = 1,
                createdAt = LocalDateTime.now(),
                updatedAt = LocalDateTime.now(),
                children = emptyList()
            )
        )

        every { categoryService.getAllCategories() } returns response

        // When & Then
        mockMvc.perform(get("/api/categories"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.status").value("success"))
            .andExpect(jsonPath("$.data").isArray)
    }
} 