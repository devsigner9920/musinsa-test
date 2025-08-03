package com.musinsa.category.util

import com.musinsa.category.dto.CategoryResponse
import com.musinsa.category.entity.Category

object CategoryMapper {

    fun toResponse(category: Category): CategoryResponse {
        return CategoryResponse(
            id = category.id!!,
            name = category.name,
            parentId = category.parentId,
            depth = category.depth,
            sortOrder = category.sortOrder,
            createdAt = category.createdAt!!,
            updatedAt = category.updatedAt!!,
            children = emptyList()
        )
    }

    fun toResponseList(categories: List<Category>): List<CategoryResponse> {
        return categories.map { toResponse(it) }
    }

    fun toTreeStructure(categories: List<Category>): List<CategoryResponse> {
        val categoryMap = categories.associate { it.id!! to toResponse(it) }.toMutableMap()
        val rootCategories = mutableListOf<CategoryResponse>()

        categories.forEach { category ->
            val categoryResponse = categoryMap[category.id!!]!!
            
            if (category.parentId == null) {
                rootCategories.add(categoryResponse)
            } else {
                val parentId = category.parentId
                if (parentId != null) {
                    val parentCategory = categoryMap[parentId]
                    if (parentCategory != null) {
                        categoryMap[parentId] = parentCategory.copy(
                            children = parentCategory.children + categoryResponse
                        )
                    }
                }
            }
        }

        return rootCategories
            .map { categoryMap[it.id]!! }  // 업데이트된 카테고리를 가져오기
            .sortedBy { it.sortOrder }
            .map { sortChildren(it) }
    }

    fun toTreeStructureWithRoot(rootCategory: Category, allCategories: List<Category>): CategoryResponse {
        val categoryMap = allCategories.associate { it.id!! to toResponse(it) }.toMutableMap()
        val rootResponse = toResponse(rootCategory)
        categoryMap[rootCategory.id!!] = rootResponse

        buildTreeRecursively(rootCategory.id!!, categoryMap, allCategories)

        return categoryMap[rootCategory.id!!]!!
    }

    private fun buildTreeRecursively(
        parentId: Long,
        categoryMap: MutableMap<Long, CategoryResponse>,
        allCategories: List<Category>
    ) {
        val children = allCategories
            .filter { it.parentId == parentId }
            .sortedBy { it.sortOrder }

        children.forEach { child ->
            val childResponse = toResponse(child)
            categoryMap[child.id!!] = childResponse
            
            buildTreeRecursively(child.id!!, categoryMap, allCategories)
            
            val parent = categoryMap[parentId]!!
            categoryMap[parentId] = parent.copy(
                children = parent.children + categoryMap[child.id!!]!!
            )
        }
    }

    private fun sortChildren(category: CategoryResponse): CategoryResponse {
        val sortedChildren = category.children
            .sortedBy { it.sortOrder }
            .map { sortChildren(it) }
        
        return category.copy(children = sortedChildren)
    }
} 