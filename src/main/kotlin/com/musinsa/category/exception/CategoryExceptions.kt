package com.musinsa.category.exception

/**
 * 비즈니스 로직 예외 기본 클래스
 */
abstract class BusinessException(
    message: String,
    val errorCode: String,
    val details: String? = null,
    cause: Throwable? = null
) : RuntimeException(message, cause)

/**
 * 카테고리를 찾을 수 없을 때 발생하는 예외
 */
class CategoryNotFoundException(
    categoryId: Long,
    details: String? = null
) : BusinessException(
    message = "카테고리를 찾을 수 없습니다.",
    errorCode = "CATEGORY_NOT_FOUND",
    details = details ?: "ID: $categoryId 에 해당하는 카테고리가 존재하지 않습니다."
)

/**
 * 부모 카테고리를 찾을 수 없을 때 발생하는 예외
 */
class ParentCategoryNotFoundException(
    parentId: Long,
    details: String? = null
) : BusinessException(
    message = "부모 카테고리를 찾을 수 없습니다.",
    errorCode = "PARENT_CATEGORY_NOT_FOUND",
    details = details ?: "parentId: $parentId 에 해당하는 카테고리가 존재하지 않습니다."
)

/**
 * 순환 참조가 발생할 때 발생하는 예외
 */
class CircularReferenceException(
    categoryId: Long,
    parentId: Long,
    details: String? = null
) : BusinessException(
    message = "순환 참조가 발생합니다.",
    errorCode = "CIRCULAR_REFERENCE",
    details = details ?: "카테고리 ID: $categoryId 를 부모 ID: $parentId 로 설정하면 순환 참조가 발생합니다."
)

/**
 * 최대 깊이를 초과할 때 발생하는 예외
 */
class MaxDepthExceededException(
    currentDepth: Int,
    maxDepth: Int = 5,
    details: String? = null
) : BusinessException(
    message = "최대 깊이를 초과했습니다.",
    errorCode = "MAX_DEPTH_EXCEEDED",
    details = details ?: "현재 깊이: $currentDepth, 최대 허용 깊이: $maxDepth"
)

/**
 * 중복된 카테고리명일 때 발생하는 예외
 */
class DuplicateCategoryNameException(
    name: String,
    parentId: Long?,
    details: String? = null
) : BusinessException(
    message = "중복된 카테고리명입니다.",
    errorCode = "DUPLICATE_CATEGORY_NAME",
    details = details ?: "카테고리명: '$name' 이 부모 카테고리 ID: $parentId 하위에 이미 존재합니다."
)

/**
 * 유효하지 않은 카테고리명일 때 발생하는 예외
 */
class InvalidCategoryNameException(
    name: String,
    details: String? = null
) : BusinessException(
    message = "유효하지 않은 카테고리명입니다.",
    errorCode = "INVALID_CATEGORY_NAME",
    details = details ?: "카테고리명: '$name' 은(는) 유효하지 않은 형식입니다."
)

/**
 * 잘못된 부모 카테고리 지정일 때 발생하는 예외
 */
class InvalidParentAssignmentException(
    categoryId: Long,
    parentId: Long,
    details: String? = null
) : BusinessException(
    message = "잘못된 부모 카테고리 지정입니다.",
    errorCode = "INVALID_PARENT_ASSIGNMENT",
    details = details ?: "카테고리 ID: $categoryId 를 부모 ID: $parentId 로 설정할 수 없습니다."
)

/**
 * 카테고리가 비활성 상태일 때 발생하는 예외
 */
class InactiveCategoryException(
    categoryId: Long,
    details: String? = null
) : BusinessException(
    message = "비활성 상태인 카테고리입니다.",
    errorCode = "INACTIVE_CATEGORY",
    details = details ?: "카테고리 ID: $categoryId 는 비활성 상태입니다."
)

/**
 * 카테고리에 하위 카테고리가 존재할 때 발생하는 예외
 */
class CategoryHasChildrenException(
    categoryId: Long,
    childrenCount: Int,
    details: String? = null
) : BusinessException(
    message = "하위 카테고리가 존재하는 카테고리입니다.",
    errorCode = "CATEGORY_HAS_CHILDREN",
    details = details ?: "카테고리 ID: $categoryId 에 $childrenCount 개의 하위 카테고리가 존재합니다."
) 