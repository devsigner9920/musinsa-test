package com.musinsa.category.domain

import com.musinsa.category.entity.Category

class CategoryNode(
    val category: Category
) {
    var parent: CategoryNode? = null
    private val _children: MutableList<CategoryNode> = mutableListOf()
    
    val children: List<CategoryNode>
        get() = _children.toList()
    
    fun addChild(child: CategoryNode) {
        if (!_children.contains(child)) {
            _children.add(child)
            child.parent = this
            sortChildren()
        }
    }
    
    fun removeChild(child: CategoryNode) {
        if (_children.remove(child)) {
            child.parent = null
        }
    }
    
    fun sortChildren() {
        _children.sortBy { it.category.sortOrder }
    }
    
    fun sortChildrenRecursively() {
        sortChildren()
        _children.forEach { it.sortChildrenRecursively() }
    }
    
    fun isRoot(): Boolean = parent == null
    
    fun isLeaf(): Boolean = _children.isEmpty()
    
    fun getDepth(): Int {
        var depth = 0
        var current = parent
        while (current != null) {
            depth++
            current = current.parent
        }
        return depth
    }
    
    fun getSiblings(): List<CategoryNode> {
        return parent?.children?.filter { it != this } ?: emptyList()
    }
    
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        
        other as CategoryNode
        return category.id == other.category.id
    }
    
    override fun hashCode(): Int {
        return category.id?.hashCode() ?: 0
    }
    
    override fun toString(): String {
        return "CategoryNode(category=${category.name}, children=${_children.size})"
    }
} 