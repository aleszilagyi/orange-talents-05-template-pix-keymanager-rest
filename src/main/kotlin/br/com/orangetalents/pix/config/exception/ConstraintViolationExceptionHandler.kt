package br.com.orangetalents.pix.config.exception

import io.micronaut.context.annotation.Replaces
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus
import io.micronaut.http.server.exceptions.ExceptionHandler
import io.micronaut.validation.exceptions.ConstraintExceptionHandler
import javax.inject.Singleton
import javax.validation.ConstraintViolationException

@Singleton
@Replaces(ConstraintExceptionHandler::class)
class ConstraintViolationExceptionHandler :
    ExceptionHandler<ConstraintViolationException, HttpResponse<StatusWithDetails>> {
    override fun handle(
        request: HttpRequest<*>?,
        exception: ConstraintViolationException
    ): HttpResponse<StatusWithDetails> {
        val fieldErrors = exception.constraintViolations.map { violation ->
            FieldError(
                violation.propertyPath.last().name ?: "chavePix", violation.message
            )
        }

        return StatusWithDetails.resolveHttpResponse(
            httpStatus = HttpStatus.BAD_REQUEST,
            statusDescription = "Dados inválidos",
            errorList = fieldErrors
        )
    }
}