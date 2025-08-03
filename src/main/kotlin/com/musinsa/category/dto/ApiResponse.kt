package com.musinsa.category.dto

import com.fasterxml.jackson.annotation.JsonInclude
import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDateTime

/**
 * 표준 API 응답 래퍼
 */
@Schema(description = "표준 API 응답")
@JsonInclude(JsonInclude.Include.NON_NULL)
data class ApiResponse<T>(
    @Schema(description = "응답 상태", example = "success")
    val status: String,

    @Schema(description = "응답 데이터")
    val data: T? = null,

    @Schema(description = "응답 메시지", example = "요청이 성공적으로 처리되었습니다.")
    val message: String,

    @Schema(description = "에러 정보")
    val error: ErrorDetail? = null,

    @Schema(description = "응답 시간")
    val timestamp: LocalDateTime = LocalDateTime.now()
) {
    companion object {
        /**
         * 성공 응답 생성
         */
        fun <T> success(data: T, message: String = "요청이 성공적으로 처리되었습니다."): ApiResponse<T> {
            return ApiResponse(
                status = "success",
                data = data,
                message = message
            )
        }

        /**
         * 성공 응답 생성 (데이터 없음)
         */
        fun success(message: String = "요청이 성공적으로 처리되었습니다."): ApiResponse<Any> {
            return ApiResponse(
                status = "success",
                message = message
            )
        }

        /**
         * 에러 응답 생성
         */
        fun error(
            code: String,
            message: String,
            details: String? = null
        ): ApiResponse<Any> {
            return ApiResponse(
                status = "error",
                message = message,
                error = ErrorDetail(
                    code = code,
                    message = message,
                    details = details
                )
            )
        }
    }
}

/**
 * 에러 상세 정보
 */
@Schema(description = "에러 상세 정보")
data class ErrorDetail(
    @Schema(description = "에러 코드", example = "CATEGORY_NOT_FOUND")
    val code: String,

    @Schema(description = "에러 메시지", example = "카테고리를 찾을 수 없습니다.")
    val message: String,

    @Schema(description = "에러 상세 설명", example = "ID: 123에 해당하는 카테고리가 존재하지 않습니다.")
    val details: String? = null
) 