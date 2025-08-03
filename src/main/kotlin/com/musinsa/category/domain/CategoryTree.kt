package com.musinsa.category.domain

import com.musinsa.category.entity.Category
import com.musinsa.category.exception.*

class CategoryTree private constructor(
    private val categories: MutableMap<Long, CategoryNode> = mutableMapOf(),
    private val rootNodes: MutableList<CategoryNode> = mutableListOf()
) {

    companion object {
        private const val MAX_DEPTH = 5
        
        fun from(categoryList: List<Category>): CategoryTree {
            val tree = CategoryTree()
            tree.buildTree(categoryList)
            return tree
        }
        
        fun empty(): CategoryTree = CategoryTree()
    }

    private fun buildTree(categoryList: List<Category>) {
        categories.clear()
        rootNodes.clear()
        
        // 1단계: 모든 카테고리를 노드로 변환하여 저장
        categoryList.forEach { category ->
            category.id?.let { id ->
                categories[id] = CategoryNode(category)
            }
        }
        
        // 2단계: 부모-자식 관계 설정
        categoryList.forEach { category ->
            category.id?.let { id ->
                val node = categories[id]!!
                
                if (category.parentId != null) {
                    val parentNode = categories[category.parentId]
                    if (parentNode != null) {
                        parentNode.addChild(node)
                        node.parent = parentNode
                    }
                } else {
                    rootNodes.add(node)
                }
            }
        }
        
        // 3단계: 정렬
        sortTree()
    }
    
    private fun sortTree() {
        rootNodes.sortBy { it.category.sortOrder }
        rootNodes.forEach { it.sortChildrenRecursively() }
    }

    fun getAllCategories(): List<Category> {
        return categories.values.map { it.category }
    }
    
    fun findById(id: Long): Category? {
        return categories[id]?.category
    }
    
    fun findNodeById(id: Long): CategoryNode? {
        return categories[id]
    }
    
    fun getRootCategories(): List<Category> {
        return rootNodes.map { it.category }
    }
    
    fun getChildren(parentId: Long?): List<Category> {
        return if (parentId == null) {
            getRootCategories()
        } else {
            categories[parentId]?.children?.map { it.category } ?: emptyList()
        }
    }
    
    fun getDescendants(categoryId: Long): List<Category> {
        val node = categories[categoryId] ?: return emptyList()
        val result = mutableListOf<Category>()
        
        fun collectDescendants(currentNode: CategoryNode) {
            currentNode.children.forEach { child ->
                result.add(child.category)
                collectDescendants(child)
            }
        }
        
        collectDescendants(node)
        return result
    }
    
    fun getAncestors(categoryId: Long): List<Category> {
        val node = categories[categoryId] ?: return emptyList()
        val ancestors = mutableListOf<Category>()
        
        var current = node.parent
        while (current != null) {
            ancestors.add(0, current.category)
            current = current.parent
        }
        
        return ancestors
    }
    
    fun getCategoryPath(categoryId: Long): String {
        val ancestors = getAncestors(categoryId)
        val category = findById(categoryId) ?: return ""
        
        val fullPath = ancestors + category
        return fullPath.joinToString(" > ") { it.name }
    }
    
    fun addCategory(category: Category): CategoryTree {
        // 유효성 검증
        validateCategoryForAdd(category)
        
        val newCategory = category.copy()
        newCategory.id?.let { id ->
            val newNode = CategoryNode(newCategory)
            categories[id] = newNode
            
            if (newCategory.parentId != null) {
                val parentNode = categories[newCategory.parentId]
                parentNode?.addChild(newNode)
                newNode.parent = parentNode
            } else {
                rootNodes.add(newNode)
            }
            
            sortTree()
        }
        
        return this
    }
    
    fun updateCategory(id: Long, updatedCategory: Category): CategoryTree {
        val existingNode = categories[id] ?: throw CategoryNotFoundException(id)
        val oldParentId = existingNode.category.parentId
        val newParentId = updatedCategory.parentId
        
        // 부모 변경 검증
        if (oldParentId != newParentId) {
            validateParentChange(id, newParentId)
        }
        
        // 카테고리 정보 업데이트
        existingNode.category.apply {
            name = updatedCategory.name
            parentId = updatedCategory.parentId
            depth = updatedCategory.depth
            sortOrder = updatedCategory.sortOrder
            isActive = updatedCategory.isActive
        }
        
        // 부모 관계 변경이 있는 경우
        if (oldParentId != newParentId) {
            // 기존 부모에서 제거
            existingNode.parent?.removeChild(existingNode)
            rootNodes.remove(existingNode)
            
            // 새 부모에 추가
            if (newParentId != null) {
                val newParentNode = categories[newParentId]
                newParentNode?.addChild(existingNode)
                existingNode.parent = newParentNode
            } else {
                rootNodes.add(existingNode)
                existingNode.parent = null
            }
            
            // 하위 카테고리들의 depth 업데이트
            updateChildrenDepth(existingNode)
        }
        
        sortTree()
        return this
    }
    
    fun removeCategory(id: Long): CategoryTree {
        val node = categories[id] ?: throw CategoryNotFoundException(id)
        
        // 하위 카테고리들을 부모의 부모로 이동 (또는 루트로)
        val newParentNode = node.parent
        node.children.forEach { child ->
            node.removeChild(child)
            if (newParentNode != null) {
                newParentNode.addChild(child)
                child.parent = newParentNode
            } else {
                rootNodes.add(child)
                child.parent = null
            }
            updateChildrenDepth(child)
        }
        
        // 현재 노드 제거
        node.parent?.removeChild(node)
        rootNodes.remove(node)
        categories.remove(id)
        
        sortTree()
        return this
    }
    
    fun deleteCategory(id: Long): CategoryTree {
        val node = categories[id] ?: throw CategoryNotFoundException(id)
        
        // 모든 하위 카테고리들도 함께 삭제
        val toDelete = mutableListOf<Long>()
        
        fun collectForDeletion(currentNode: CategoryNode) {
            currentNode.category.id?.let { toDelete.add(it) }
            currentNode.children.forEach { collectForDeletion(it) }
        }
        
        collectForDeletion(node)
        
        // 삭제 실행
        toDelete.forEach { categoryId ->
            val nodeToDelete = categories[categoryId]
            nodeToDelete?.let {
                it.parent?.removeChild(it)
                rootNodes.remove(it)
                categories.remove(categoryId)
            }
        }
        
        return this
    }
    
    private fun validateCategoryForAdd(category: Category) {
        category.id?.let { id ->
            if (categories.containsKey(id)) {
                throw IllegalArgumentException("카테고리 ID $id 가 이미 존재합니다.")
            }
        }
        
        category.parentId?.let { parentId ->
            val parentNode = categories[parentId] 
                ?: throw ParentCategoryNotFoundException(parentId)
            
            if (!parentNode.category.isActive) {
                throw InactiveCategoryException(parentId, "부모 카테고리가 비활성 상태입니다.")
            }
            
            val newDepth = parentNode.category.depth + 1
            if (newDepth > MAX_DEPTH) {
                throw MaxDepthExceededException(newDepth, MAX_DEPTH)
            }
        }
        
        // 카테고리 자체의 깊이도 검증
        if (category.depth > MAX_DEPTH) {
            throw MaxDepthExceededException(category.depth, MAX_DEPTH)
        }
    }
    
    private fun validateParentChange(categoryId: Long, newParentId: Long?) {
        if (newParentId == null) return
        
        if (categoryId == newParentId) {
            throw CircularReferenceException(categoryId, newParentId, "자기 자신을 부모로 설정할 수 없습니다.")
        }
        
        // 순환 참조 체크 - 새 부모가 현재 카테고리의 하위인지 확인
        val descendants = getDescendants(categoryId)
        if (descendants.any { it.id == newParentId }) {
            throw CircularReferenceException(categoryId, newParentId, "하위 카테고리를 부모로 설정할 수 없습니다.")
        }
        
        val newParentNode = categories[newParentId]
            ?: throw ParentCategoryNotFoundException(newParentId)
        
        if (!newParentNode.category.isActive) {
            throw InactiveCategoryException(newParentId, "부모 카테고리가 비활성 상태입니다.")
        }
        
        val newDepth = newParentNode.category.depth + 1
        if (newDepth > MAX_DEPTH) {
            throw MaxDepthExceededException(newDepth, MAX_DEPTH)
        }
    }
    
    private fun updateChildrenDepth(node: CategoryNode) {
        val parentDepth = node.parent?.category?.depth ?: -1
        val newDepth = parentDepth + 1
        
        node.category.depth = newDepth
        node.children.forEach { child ->
            updateChildrenDepth(child)
        }
    }
    
    fun validateDuplicateName(name: String, parentId: Long?, excludeId: Long? = null): Boolean {
        val siblings = getChildren(parentId)
        return siblings.any { sibling ->
            sibling.name == name && 
            sibling.isActive && 
            sibling.id != excludeId
        }
    }
    
    fun getStatistics(): Map<String, Any> {
        val allCategories = getAllCategories()
        val depthStats = allCategories.groupBy { it.depth }.mapValues { it.value.size }
        val maxDepth = allCategories.maxOfOrNull { it.depth } ?: 0
        
        return mapOf(
            "totalCategories" to allCategories.size,
            "rootCategories" to rootNodes.size,
            "maxDepth" to maxDepth,
            "depthDistribution" to depthStats,
            "activeCategories" to allCategories.count { it.isActive },
            "inactiveCategories" to allCategories.count { !it.isActive }
        )
    }
    
    fun validateTreeStructure(): List<String> {
        val errors = mutableListOf<String>()
        
        // 순환 참조 검사
        categories.values.forEach { node ->
            if (hasCircularReference(node)) {
                errors.add("순환 참조 발견: 카테고리 ID ${node.category.id}")
            }
        }
        
        // 깊이 일치성 검사
        categories.values.forEach { node ->
            val expectedDepth = getAncestors(node.category.id!!).size
            if (node.category.depth != expectedDepth) {
                errors.add("깊이 불일치: 카테고리 ID ${node.category.id} (저장된 깊이: ${node.category.depth}, 계산된 깊이: $expectedDepth)")
            }
        }
        
        // 부모 존재성 검사
        categories.values.forEach { node ->
            node.category.parentId?.let { parentId ->
                if (!categories.containsKey(parentId)) {
                    errors.add("존재하지 않는 부모: 카테고리 ID ${node.category.id}, 부모 ID $parentId")
                }
            }
        }
        
        return errors
    }
    
    private fun hasCircularReference(startNode: CategoryNode): Boolean {
        val visited = mutableSetOf<Long>()
        var current = startNode
        
        while (current.parent != null) {
            current.category.id?.let { id ->
                if (visited.contains(id)) {
                    return true
                }
                visited.add(id)
            }
            current = current.parent!!
        }
        
        return false
    }
    
    fun countCategoryAndDescendants(categoryId: Long): Long {
        return 1L + getDescendants(categoryId).size
    }
    
    fun isDescendantOf(descendantId: Long, ancestorId: Long): Boolean {
        return getAncestors(descendantId).any { it.id == ancestorId }
    }
} 