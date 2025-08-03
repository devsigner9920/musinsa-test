package com.musinsa.category.repository

import com.musinsa.category.entity.Category
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface CategoryRepository : JpaRepository<Category, Long> {

    /**
     * 부모 카테고리 ID로 하위 카테고리 목록 조회 (정렬 순서대로)
     */
    fun findByParentIdOrderBySortOrderAsc(parentId: Long?): List<Category>

    /**
     * 루트 카테고리 목록 조회 (정렬 순서대로)
     */
    fun findByParentIdIsNullOrderBySortOrderAsc(): List<Category>

    /**
     * 모든 카테고리 조회 (정렬 순서대로)
     */
    fun findAllByOrderByDepthAscSortOrderAsc(): List<Category>

    /**
     * 부모 카테고리 ID와 카테고리명으로 중복 확인
     */
    fun existsByParentIdAndName(parentId: Long?, name: String): Boolean



    /**
     * 특정 깊이의 카테고리들 조회
     */
    fun findByDepthOrderBySortOrderAsc(depth: Int): List<Category>

    /**
     * 최대 깊이 조회
     */
    @Query("SELECT COALESCE(MAX(c.depth), 0) FROM Category c")
    fun findMaxDepth(): Int



    /**
     * 동일 부모를 가진 카테고리들의 최대 정렬 순서 조회
     */
    @Query("SELECT COALESCE(MAX(c.sortOrder), 0) FROM Category c WHERE c.parentId = :parentId")
    fun findMaxSortOrderByParentId(@Param("parentId") parentId: Long?): Int
} 