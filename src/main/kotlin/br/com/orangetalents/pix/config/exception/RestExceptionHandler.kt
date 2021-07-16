package br.com.orangetalents.pix.config.exception

import io.grpc.StatusRuntimeException
import io.micronaut.http.HttpRequest
import io.micronaut.http.server.exceptions.ExceptionHandler
import java.net.http.HttpResponse

class RestExceptionHandler : ExceptionHandler<StatusRuntimeException, HttpResponse<Any>> {
    override fun handle(request: HttpRequest<*>?, exception: StatusRuntimeException?): HttpResponse<Any> {
        TODO("Not yet implemented")
    }
}