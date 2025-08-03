package com.musinsa.category.dto

import com.fasterxml.jackson.annotation.JsonProperty
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.*

/**
 * 카테고리 생성 요청 DTO
 */
@Schema(description = "카테고리 생성 요청")
data class CategoryCreateRequest(
    @field:NotBlank(message = "카테고리명은 필수입니다")
    @field:Size(min = 1, max = 100, message = "카테고리명은 1-100자 이내여야 합니다")
    @field:Pattern(
        regexp = "^[a-zA-Z가-힣0-9\\s]+$",
        message = "카테고리명은 한글, 영문, 숫자, 공백만 허용됩니다"
    )
    @Schema(description = "카테고리명", example = "의류", required = true)
    val name: String,

    @field:Positive(message = "부모 카테고리 ID는 양수여야 합니다")
    @Schema(description = "부모 카테고리 ID", example = "1", required = false)
    @JsonProperty("parentId")
    val parentId: Long? = null,

    @field:Min(value = 0, message = "정렬 순서는 0 이상이어야 합니다")
    @Schema(description = "정렬 순서", example = "1", required = true, defaultValue = "0")
    @JsonProperty("sortOrder")
    val sortOrder: Int = 0
)

/**
 * 카테고리 수정 요청 DTO
 */
@Schema(description = "카테고리 수정 요청")
data class CategoryUpdateRequest(
    @field:NotBlank(message = "카테고리명은 필수입니다")
    @field:Size(min = 1, max = 100, message = "카테고리명은 1-100자 이내여야 합니다")
    @field:Pattern(
        regexp = "^[a-zA-Z가-힣0-9\\s]+$",
        message = "카테고리명은 한글, 영문, 숫자, 공백만 허용됩니다"
    )
    @Schema(description = "카테고리명", example = "상의", required = true)
    val name: String,

    @field:Positive(message = "부모 카테고리 ID는 양수여야 합니다")
    @Schema(description = "부모 카테고리 ID", example = "1", required = false)
    @JsonProperty("parentId")
    val parentId: Long? = null,

    @field:Min(value = 0, message = "정렬 순서는 0 이상이어야 합니다")
    @Schema(description = "정렬 순서", example = "2", required = true, defaultValue = "0")
    @JsonProperty("sortOrder")
    val sortOrder: Int = 0
) 