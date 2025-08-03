package com.musinsa.category.entity

import jakarta.persistence.*
import jakarta.validation.constraints.*
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.LocalDateTime

@Entity
@Table(
    name = "category",
    indexes = [
        Index(name = "idx_parent_id", columnList = "parent_id"),
        Index(name = "idx_depth", columnList = "depth"),
        Index(name = "idx_sort_order", columnList = "sort_order"),
        Index(name = "idx_active_parent", columnList = "is_active, parent_id")
    ],
    uniqueConstraints = [
        UniqueConstraint(name = "uk_category_name_parent", columnNames = ["name", "parent_id"])
    ]
)
class Category(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    var id: Long? = null,

    @Column(name = "name", nullable = false, length = 100)
    @field:NotBlank(message = "카테고리명은 필수입니다")
    @field:Size(min = 1, max = 100, message = "카테고리명은 1-100자 이내여야 합니다")
    var name: String,

    @Column(name = "parent_id")
    var parentId: Long? = null,

    @Column(name = "depth", nullable = false)
    @field:Min(value = 0, message = "깊이는 0 이상이어야 합니다")
    @field:Max(value = 5, message = "최대 깊이는 5입니다")
    var depth: Int = 0,

    @Column(name = "sort_order", nullable = false)
    @field:Min(value = 0, message = "정렬 순서는 0 이상이어야 합니다")
    var sortOrder: Int = 0,

    @Column(name = "is_active", nullable = false)
    var isActive: Boolean = true,

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    var createdAt: LocalDateTime? = null,

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    var updatedAt: LocalDateTime? = null
) {
    constructor() : this(
        name = "",
        parentId = null,
        depth = 0,
        sortOrder = 0,
        isActive = true
    )

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Category

        return id != null && id == other.id
    }

    override fun hashCode(): Int {
        return id?.hashCode() ?: 0
    }

    override fun toString(): String {
        return "Category(id=$id, name='$name', parentId=$parentId, depth=$depth, sortOrder=$sortOrder, isActive=$isActive)"
    }

    companion object {
        fun create(
            name: String,
            parentId: Long? = null,
            depth: Int = 0,
            sortOrder: Int = 0
        ): Category {
            return Category(
                name = name,
                parentId = parentId,
                depth = depth,
                sortOrder = sortOrder,
                isActive = true
            )
        }
    }

    fun update(
        name: String? = null,
        parentId: Long? = null,
        depth: Int? = null,
        sortOrder: Int? = null
    ) {
        name?.let { this.name = it }
        parentId.let { this.parentId = it }
        depth?.let { this.depth = it }
        sortOrder?.let { this.sortOrder = it }
    }

    fun isRoot(): Boolean = parentId == null

    fun isChild(): Boolean = parentId != null
    
    fun copy(
        id: Long? = this.id,
        name: String = this.name,
        parentId: Long? = this.parentId,
        depth: Int = this.depth,
        sortOrder: Int = this.sortOrder,
        isActive: Boolean = this.isActive,
        createdAt: LocalDateTime? = this.createdAt,
        updatedAt: LocalDateTime? = this.updatedAt
    ): Category {
        return Category(
            id = id,
            name = name,
            parentId = parentId,
            depth = depth,
            sortOrder = sortOrder,
            isActive = isActive,
            createdAt = createdAt,
            updatedAt = updatedAt
        )
    }
} 