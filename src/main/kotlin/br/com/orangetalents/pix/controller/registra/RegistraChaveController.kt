package br.com.orangetalents.pix.controller.registra

import br.com.orangetalents.KeyManagerRegistraPixServiceGrpc
import br.com.orangetalents.pix.config.validator.ValidUUID
import br.com.orangetalents.pix.dto.RegistraChavePixRequestDto
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.PathVariable
import io.micronaut.http.annotation.Post
import io.micronaut.validation.Validated
import org.slf4j.LoggerFactory
import javax.validation.Valid
import javax.validation.constraints.NotBlank

@Validated
@Controller("/api/v1/clientes/{clienteId}")
class RegistraChaveController(private val registraChavePixClient: KeyManagerRegistraPixServiceGrpc.KeyManagerRegistraPixServiceBlockingStub) {

    private val LOGGER = LoggerFactory.getLogger(this::class.java)

    @Post("/pix")
    fun registra(
        @PathVariable @NotBlank @ValidUUID clienteId: String,
        @Valid @Body request: RegistraChavePixRequestDto
    ): HttpResponse<Any> {
        LOGGER.info("[$clienteId] criando uma nova chave pix com $request")

        val grpcResponse = registraChavePixClient.registra(request.toProtoGrpc(clienteId))

        return HttpResponse.created(location(clienteId, grpcResponse.pixId))
    }
    private fun location(clienteId: String, pixId: String) = HttpResponse
        .uri("/api/v1/clientes/$clienteId/pix/${pixId}")
}