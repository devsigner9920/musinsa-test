package com.musinsa.category.dto

import com.fasterxml.jackson.annotation.JsonProperty
import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDateTime

/**
 * 카테고리 응답 DTO
 */
@Schema(description = "카테고리 응답")
data class CategoryResponse(
    @Schema(description = "카테고리 ID", example = "1")
    val id: Long,

    @Schema(description = "카테고리명", example = "의류")
    val name: String,

    @Schema(description = "부모 카테고리 ID", example = "null")
    @JsonProperty("parentId")
    val parentId: Long?,

    @Schema(description = "깊이 레벨", example = "0")
    val depth: Int,

    @Schema(description = "정렬 순서", example = "1")
    @JsonProperty("sortOrder")
    val sortOrder: Int,

    @Schema(description = "생성일시", example = "2024-01-01T10:00:00")
    @JsonProperty("createdAt")
    val createdAt: LocalDateTime,

    @Schema(description = "수정일시", example = "2024-01-01T10:00:00")
    @JsonProperty("updatedAt")
    val updatedAt: LocalDateTime,

    @Schema(description = "하위 카테고리 목록")
    val children: List<CategoryResponse> = emptyList()
)

/**
 * 카테고리 삭제 응답 DTO
 */
@Schema(description = "카테고리 삭제 응답")
data class CategoryDeleteResponse(
    @Schema(description = "삭제된 카테고리 ID", example = "2")
    @JsonProperty("deletedCategoryId")
    val deletedCategoryId: Long,

    @Schema(description = "삭제된 하위 카테고리 개수", example = "3")
    @JsonProperty("deletedChildrenCount")
    val deletedChildrenCount: Int
) 