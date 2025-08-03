package com.musinsa.category.controller

import com.musinsa.category.dto.*
import com.musinsa.category.service.CategoryService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/categories")
@Tag(name = "Category API", description = "카테고리 관리 API")
class CategoryController(
    private val categoryService: CategoryService
) {

    private val logger = LoggerFactory.getLogger(CategoryController::class.java)

    @PostMapping
    @Operation(summary = "카테고리 생성")
    fun createCategory(
        @Valid @RequestBody request: CategoryCreateRequest
    ): ResponseEntity<ApiResponse<CategoryResponse>> {
        logger.info("POST /api/categories - Creating category: {}", request.name)

        val categoryResponse = categoryService.createCategory(request)
        val response = ApiResponse.success(
            data = categoryResponse,
            message = "카테고리가 성공적으로 생성되었습니다."
        )

        return ResponseEntity(response, HttpStatus.CREATED)
    }

    @PutMapping("/{id}")
    @Operation(summary = "카테고리 수정")
    fun updateCategory(
        @Parameter(description = "카테고리 ID") @PathVariable id: Long,
        @Valid @RequestBody request: CategoryUpdateRequest
    ): ResponseEntity<ApiResponse<CategoryResponse>> {
        logger.info("PUT /api/categories/{} - Updating category: {}", id, request.name)

        val categoryResponse = categoryService.updateCategory(id, request)
        val response = ApiResponse.success(
            data = categoryResponse,
            message = "카테고리가 성공적으로 수정되었습니다."
        )

        return ResponseEntity.ok(response)
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "카테고리 삭제", description = "하위 카테고리도 함께 삭제됩니다")
    fun deleteCategory(
        @Parameter(description = "카테고리 ID") @PathVariable id: Long
    ): ResponseEntity<ApiResponse<CategoryDeleteResponse>> {
        logger.info("DELETE /api/categories/{} - Deleting category", id)

        val deleteResponse = categoryService.deleteCategory(id)
        val response = ApiResponse.success(
            data = deleteResponse,
            message = "카테고리와 하위 카테고리 ${deleteResponse.deletedChildrenCount}개가 성공적으로 삭제되었습니다."
        )

        return ResponseEntity.ok(response)
    }

    @GetMapping("/{id}")
    @Operation(summary = "개별 카테고리 조회")
    fun getCategoryById(
        @Parameter(description = "카테고리 ID") @PathVariable id: Long
    ): ResponseEntity<ApiResponse<CategoryResponse>> {
        logger.info("GET /api/categories/{} - Getting category by id", id)

        val categoryResponse = categoryService.getCategoryById(id)
        val response = ApiResponse.success(
            data = categoryResponse,
            message = "카테고리가 성공적으로 조회되었습니다."
        )

        return ResponseEntity.ok(response)
    }

    @GetMapping
    @Operation(summary = "전체 카테고리 조회")
    fun getAllCategories(): ResponseEntity<ApiResponse<List<CategoryResponse>>> {
        logger.info("GET /api/categories - Getting all categories")

        val categories = categoryService.getAllCategories()
        val response = ApiResponse.success(
            data = categories,
            message = "전체 카테고리가 성공적으로 조회되었습니다."
        )

        return ResponseEntity.ok(response)
    }


} 