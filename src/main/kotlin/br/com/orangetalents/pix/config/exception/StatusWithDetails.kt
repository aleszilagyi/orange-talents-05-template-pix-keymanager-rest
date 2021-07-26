package br.com.orangetalents.pix.config.exception

import com.google.rpc.BadRequest
import io.micronaut.core.annotation.Introspected
import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus
import io.micronaut.http.hateoas.JsonError

@Introspected
data class StatusWithDetails(
    val statusDescription: String,
    val fieldErrors: List<FieldError>
) {
    companion object {
        fun resolveHttpResponse(
            httpStatus: HttpStatus,
            statusDescription: String,
            gStatus: com.google.rpc.Status
        ): HttpResponse<StatusWithDetails> {
            val fieldErrors =
                if (httpStatus == HttpStatus.BAD_REQUEST) gStatus.detailsList.map { error -> error.unpack(BadRequest::class.java) }
                    .flatMap { badRequest -> badRequest.fieldViolationsList.map { violation -> FieldError(
                        violation.field,
                        violation.description) } }
                else listOf()

            val statusDetails = StatusWithDetails(statusDescription = statusDescription, fieldErrors = fieldErrors)

            return HttpResponse.status<JsonError>(httpStatus).body(statusDetails)
        }
    }
}

data class FieldError(
    val field: String,
    val description: String
)