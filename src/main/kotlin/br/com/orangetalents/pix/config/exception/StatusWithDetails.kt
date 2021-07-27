package br.com.orangetalents.pix.config.exception

import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus
import io.micronaut.http.hateoas.JsonError

data class StatusWithDetails(
    val statusDescription: String,
    val fieldErrors: List<FieldError>
) {
    companion object {
        fun resolveHttpResponse(
            httpStatus: HttpStatus,
            statusDescription: String,
            errorList: List<FieldError>
        ): HttpResponse<StatusWithDetails> {
            val statusDetails = StatusWithDetails(statusDescription = statusDescription, fieldErrors = errorList)

            return HttpResponse.status<JsonError>(httpStatus).body(statusDetails)
        }
    }
}

data class FieldError(
    val field: String,
    val description: String
)