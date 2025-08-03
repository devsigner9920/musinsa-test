package com.musinsa.category.exception

import com.musinsa.category.dto.ApiResponse
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.BindException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException
import jakarta.validation.ConstraintViolationException

@RestControllerAdvice
class GlobalExceptionHandler {

    private val logger = LoggerFactory.getLogger(GlobalExceptionHandler::class.java)

    /**
     * 비즈니스 로직 예외 처리
     */
    @ExceptionHandler(BusinessException::class)
    fun handleBusinessException(ex: BusinessException): ResponseEntity<ApiResponse<Any>> {
        logger.warn("Business exception occurred: {}", ex.message, ex)
        
        val httpStatus = when (ex.errorCode) {
            "CATEGORY_NOT_FOUND", "PARENT_CATEGORY_NOT_FOUND" -> HttpStatus.NOT_FOUND
            "DUPLICATE_CATEGORY_NAME" -> HttpStatus.CONFLICT
            else -> HttpStatus.BAD_REQUEST
        }

        val response = ApiResponse.error(
            code = ex.errorCode,
            message = ex.message ?: "비즈니스 로직 오류가 발생했습니다.",
            details = ex.details
        )

        return ResponseEntity(response, httpStatus)
    }

    /**
     * Bean Validation 예외 처리 (@Valid, @Validated)
     */
    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationException(ex: MethodArgumentNotValidException): ResponseEntity<ApiResponse<Any>> {
        logger.warn("Validation exception occurred: {}", ex.message)

        val errors = ex.bindingResult.fieldErrors.map { fieldError ->
            "${fieldError.field}: ${fieldError.defaultMessage}"
        }.joinToString(", ")

        val response = ApiResponse.error(
            code = "VALIDATION_ERROR",
            message = "입력값 검증에 실패했습니다.",
            details = errors
        )

        return ResponseEntity(response, HttpStatus.BAD_REQUEST)
    }

    /**
     * Bean Validation 예외 처리 (BindException)
     */
    @ExceptionHandler(BindException::class)
    fun handleBindException(ex: BindException): ResponseEntity<ApiResponse<Any>> {
        logger.warn("Bind exception occurred: {}", ex.message)

        val errors = ex.bindingResult.fieldErrors.map { fieldError ->
            "${fieldError.field}: ${fieldError.defaultMessage}"
        }.joinToString(", ")

        val response = ApiResponse.error(
            code = "VALIDATION_ERROR",
            message = "입력값 바인딩에 실패했습니다.",
            details = errors
        )

        return ResponseEntity(response, HttpStatus.BAD_REQUEST)
    }

    /**
     * Constraint Violation 예외 처리
     */
    @ExceptionHandler(ConstraintViolationException::class)
    fun handleConstraintViolationException(ex: ConstraintViolationException): ResponseEntity<ApiResponse<Any>> {
        logger.warn("Constraint violation exception occurred: {}", ex.message)

        val errors = ex.constraintViolations.map { violation ->
            "${violation.propertyPath}: ${violation.message}"
        }.joinToString(", ")

        val response = ApiResponse.error(
            code = "CONSTRAINT_VIOLATION",
            message = "제약 조건 위반이 발생했습니다.",
            details = errors
        )

        return ResponseEntity(response, HttpStatus.BAD_REQUEST)
    }

    /**
     * 타입 변환 예외 처리
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException::class)
    fun handleTypeMismatchException(ex: MethodArgumentTypeMismatchException): ResponseEntity<ApiResponse<Any>> {
        logger.warn("Type mismatch exception occurred: {}", ex.message)

        val response = ApiResponse.error(
            code = "TYPE_MISMATCH",
            message = "잘못된 타입의 파라미터입니다.",
            details = "파라미터 '${ex.name}'는 ${ex.requiredType?.simpleName} 타입이어야 합니다."
        )

        return ResponseEntity(response, HttpStatus.BAD_REQUEST)
    }

    /**
     * IllegalArgumentException 예외 처리
     */
    @ExceptionHandler(IllegalArgumentException::class)
    fun handleIllegalArgumentException(ex: IllegalArgumentException): ResponseEntity<ApiResponse<Any>> {
        logger.warn("Illegal argument exception occurred: {}", ex.message)

        val response = ApiResponse.error(
            code = "ILLEGAL_ARGUMENT",
            message = "잘못된 인수가 전달되었습니다.",
            details = ex.message
        )

        return ResponseEntity(response, HttpStatus.BAD_REQUEST)
    }

    /**
     * 일반적인 예외 처리
     */
    @ExceptionHandler(Exception::class)
    fun handleGeneralException(ex: Exception): ResponseEntity<ApiResponse<Any>> {
        logger.error("Unexpected exception occurred", ex)

        val response = ApiResponse.error(
            code = "INTERNAL_SERVER_ERROR",
            message = "서버 내부 오류가 발생했습니다.",
            details = "예상치 못한 오류가 발생했습니다. 관리자에게 문의해주세요."
        )

        return ResponseEntity(response, HttpStatus.INTERNAL_SERVER_ERROR)
    }

    /**
     * RuntimeException 예외 처리
     */
    @ExceptionHandler(RuntimeException::class)
    fun handleRuntimeException(ex: RuntimeException): ResponseEntity<ApiResponse<Any>> {
        logger.error("Runtime exception occurred: {}", ex.message, ex)

        val response = ApiResponse.error(
            code = "RUNTIME_ERROR",
            message = "런타임 오류가 발생했습니다.",
            details = ex.message ?: "런타임 중 예상치 못한 오류가 발생했습니다."
        )

        return ResponseEntity(response, HttpStatus.INTERNAL_SERVER_ERROR)
    }
} 