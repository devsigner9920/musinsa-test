package com.musinsa.category.service

import com.musinsa.category.dto.*
import com.musinsa.category.entity.Category

/**
 * 카테고리 서비스 인터페이스
 */
interface CategoryService {

    /**
     * 카테고리 생성
     */
    fun createCategory(request: CategoryCreateRequest): CategoryResponse

    /**
     * 카테고리 수정
     */
    fun updateCategory(id: Long, request: CategoryUpdateRequest): CategoryResponse

    /**
     * 카테고리 삭제 (CASCADE)
     */
    fun deleteCategory(id: Long): CategoryDeleteResponse

    /**
     * 개별 카테고리 조회 (하위 카테고리 포함)
     */
    fun getCategoryById(id: Long): CategoryResponse

    /**
     * 전체 카테고리 조회 (트리 구조)
     */
    fun getAllCategories(): List<CategoryResponse>
} 