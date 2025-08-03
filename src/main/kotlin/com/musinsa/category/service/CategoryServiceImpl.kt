package com.musinsa.category.service

import com.musinsa.category.dto.*
import com.musinsa.category.entity.Category
import com.musinsa.category.exception.*
import com.musinsa.category.repository.CategoryRepository
import com.musinsa.category.util.CategoryMapper
import org.slf4j.LoggerFactory
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.Cacheable
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class CategoryServiceImpl(
    private val categoryRepository: CategoryRepository
) : CategoryService {

    private val logger = LoggerFactory.getLogger(CategoryServiceImpl::class.java)

    companion object {
        private const val CACHE_CATEGORY_TREE = "categoryTree"
    }

    @Transactional
    @CacheEvict(value = [CACHE_CATEGORY_TREE], allEntries = true)
    override fun createCategory(request: CategoryCreateRequest): CategoryResponse {
        logger.info("Creating category with name: {}, parentId: {}", request.name, request.parentId)

        // 기본 유효성 검증
        validateParentCategory(request.parentId)
        val depth = calculateDepth(request.parentId)
        
        // 최대 깊이 검증
        if (depth > 5) {
            throw MaxDepthExceededException(depth, 5)
        }
        
        // 중복 이름 검증
        if (categoryRepository.existsByParentIdAndName(request.parentId, request.name)) {
            throw DuplicateCategoryNameException(request.name, request.parentId)
        }

        val category = Category.create(
            name = request.name,
            parentId = request.parentId,
            depth = depth,
            sortOrder = request.sortOrder
        )

        val savedCategory = categoryRepository.save(category)
        logger.info("Category created successfully with id: {}", savedCategory.id)

        return CategoryMapper.toResponse(savedCategory)
    }

    @Transactional
    @CacheEvict(value = [CACHE_CATEGORY_TREE], allEntries = true)
    override fun updateCategory(id: Long, request: CategoryUpdateRequest): CategoryResponse {
        logger.info("Updating category id: {} with name: {}, parentId: {}", id, request.name, request.parentId)

        val category = findCategoryById(id)

        // 기본 유효성 검증
        if (request.parentId != category.parentId) {
            validateParentChangeRequest(id, request.parentId)
        }

        // 중복 이름 검증 (자기 자신 제외)
        if (request.name != category.name) {
            val duplicateExists = categoryRepository.findByParentIdOrderBySortOrderAsc(request.parentId)
                .any { it.name == request.name && it.id != id }
            if (duplicateExists) {
                throw DuplicateCategoryNameException(request.name, request.parentId)
            }
        }

        validateParentCategory(request.parentId)
        val newDepth = calculateDepth(request.parentId)

        category.update(
            name = request.name,
            parentId = request.parentId,
            depth = newDepth,
            sortOrder = request.sortOrder
        )

        if (request.parentId != category.parentId) {
            updateChildrenDepthRecursively(id, newDepth)
        }

        val savedCategory = categoryRepository.save(category)
        logger.info("Category updated successfully with id: {}", savedCategory.id)

        return CategoryMapper.toResponse(savedCategory)
    }

    @Transactional
    @CacheEvict(value = [CACHE_CATEGORY_TREE], allEntries = true)
    override fun deleteCategory(id: Long): CategoryDeleteResponse {
        logger.info("Deleting category id: {}", id)

        findCategoryById(id)

        // 삭제 전 하위 카테고리 개수 계산
        val childrenCount = countDescendants(id)

        categoryRepository.deleteById(id)
        logger.info("Category and {} children deleted successfully", childrenCount)

        return CategoryDeleteResponse(
            deletedCategoryId = id,
            deletedChildrenCount = childrenCount.toInt()
        )
    }

    @Cacheable(value = [CACHE_CATEGORY_TREE], key = "'single:' + #id")
    override fun getCategoryById(id: Long): CategoryResponse {
        logger.info("Getting category by id: {}", id)

        val category = findCategoryById(id)
        val descendants = getAllDescendants(id)

        val categoriesToProcess = listOf(category) + descendants
        val treeStructure = CategoryMapper.toTreeStructure(categoriesToProcess)
        return treeStructure.find { it.id == id } ?: CategoryMapper.toResponse(category)
    }

    @Cacheable(value = [CACHE_CATEGORY_TREE], key = "'all'")
    override fun getAllCategories(): List<CategoryResponse> {
        logger.info("Getting all categories")

        val categories = categoryRepository.findAllByOrderByDepthAscSortOrderAsc()
        return CategoryMapper.toTreeStructure(categories)
    }



    private fun findCategoryById(id: Long): Category {
        return categoryRepository.findByIdOrNull(id)
            ?: throw CategoryNotFoundException(id)
    }

    private fun validateParentCategory(parentId: Long?) {
        if (parentId == null) return

        categoryRepository.findByIdOrNull(parentId)
            ?: throw ParentCategoryNotFoundException(parentId)
    }

    private fun validateParentChangeRequest(categoryId: Long, newParentId: Long?) {
        if (newParentId == null) return

        if (categoryId == newParentId) {
            throw CircularReferenceException(categoryId, newParentId, "자기 자신을 부모로 설정할 수 없습니다.")
        }

        // 순환 참조 검사: 하위 카테고리를 부모로 설정하는지 확인
        if (isDescendantOf(newParentId, categoryId)) {
            throw CircularReferenceException(categoryId, newParentId, "하위 카테고리를 부모로 설정할 수 없습니다.")
        }
    }

    private fun calculateDepth(parentId: Long?): Int {
        return if (parentId == null) {
            0
        } else {
            val parentCategory = categoryRepository.findByIdOrNull(parentId)
            parentCategory?.depth?.plus(1) ?: 0
        }
    }

    private fun countDescendants(categoryId: Long): Int {
        var count = 0
        val children = categoryRepository.findByParentIdOrderBySortOrderAsc(categoryId)
        count += children.size
        children.forEach { child ->
            count += countDescendants(child.id!!)
        }
        return count
    }

    private fun getAllDescendants(categoryId: Long): List<Category> {
        val descendants = mutableListOf<Category>()
        val children = categoryRepository.findByParentIdOrderBySortOrderAsc(categoryId)
        descendants.addAll(children)
        children.forEach { child ->
            descendants.addAll(getAllDescendants(child.id!!))
        }
        return descendants
    }

    private fun isDescendantOf(potentialAncestorId: Long, categoryId: Long): Boolean {
        val descendants = getAllDescendants(categoryId)
        return descendants.any { it.id == potentialAncestorId }
    }

    private fun updateChildrenDepthRecursively(parentId: Long, newParentDepth: Int) {
        val children = categoryRepository.findByParentIdOrderBySortOrderAsc(parentId)
        
        children.forEach { child ->
            val newDepth = newParentDepth + 1
            child.depth = newDepth
            categoryRepository.save(child)
            
            updateChildrenDepthRecursively(child.id!!, newDepth)
        }
    }


} 