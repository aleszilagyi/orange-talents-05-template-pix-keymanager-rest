package br.com.orangetalents.pix.config.exception

import com.google.rpc.BadRequest
import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus
import io.micronaut.http.server.exceptions.ExceptionHandler
import org.slf4j.LoggerFactory
import javax.inject.Singleton

@Singleton
class GrpcExceptionHandler : ExceptionHandler<StatusRuntimeException, HttpResponse<StatusWithDetails>> {

    private val LOGGER = LoggerFactory.getLogger(this::class.java)

    override fun handle(request: HttpRequest<*>, exception: StatusRuntimeException): HttpResponse<StatusWithDetails> {
        val statusCode = exception.status.code
        val gStatus = io.grpc.protobuf.StatusProto.fromThrowable(exception) as com.google.rpc.Status
        val statusDescription = exception.status.description ?: "Desculpe, erro interno"
        return when (statusCode) {
            Status.NOT_FOUND.code -> StatusWithDetails.resolveHttpResponse(
                HttpStatus.NOT_FOUND,
                statusDescription,
                obterListaDeErros(gStatus)
            )
            Status.INVALID_ARGUMENT.code -> StatusWithDetails.resolveHttpResponse(
                HttpStatus.BAD_REQUEST,
                statusDescription,
                obterListaDeErros(gStatus)
            )
            Status.ALREADY_EXISTS.code -> StatusWithDetails.resolveHttpResponse(
                HttpStatus.UNPROCESSABLE_ENTITY,
                statusDescription,
                obterListaDeErros(gStatus)
            )
            Status.FAILED_PRECONDITION.code -> StatusWithDetails.resolveHttpResponse(
                HttpStatus.UNPROCESSABLE_ENTITY,
                statusDescription,
                obterListaDeErros(gStatus)
            )
            else -> {
                LOGGER.error("Erro inesperado '${exception.javaClass.name}' ao processar requisição", exception)
                StatusWithDetails.resolveHttpResponse(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Nao foi possivel completar a requisição devido ao erro: ${statusDescription} (${statusCode})",
                    listOf()
                )
            }
        }
    }

    private fun obterListaDeErros(gStatus: com.google.rpc.Status): List<FieldError> {
        return gStatus.detailsList?.map { error -> error.unpack(BadRequest::class.java) }
            ?.flatMap { badRequest ->
                badRequest.fieldViolationsList.map { violation ->
                    FieldError(
                        violation.field,
                        violation.description
                    )
                }
            } ?: listOf()
    }
}