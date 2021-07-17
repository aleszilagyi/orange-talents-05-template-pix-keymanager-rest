package br.com.orangetalents.pix.controller.remove

import br.com.orangetalents.KeyManagerRemovePixServiceGrpc
import br.com.orangetalents.RemoveChavePixRequest
import br.com.orangetalents.pix.config.validator.ValidUUID
import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Delete
import io.micronaut.validation.Validated
import org.slf4j.LoggerFactory
import javax.validation.constraints.NotBlank

@Validated
@Controller("/api/v1/clientes/{clienteId}")
class RemoveChaveController(private val removeChavePixClient: KeyManagerRemovePixServiceGrpc.KeyManagerRemovePixServiceBlockingStub) {

    private val LOGGER = LoggerFactory.getLogger(this::class.java)

    @Delete("/pix/{pixId}")
    fun delete(
        @NotBlank @ValidUUID clienteId: String,
        @NotBlank @ValidUUID pixId: String
    ): HttpResponse<Any> {

        LOGGER.info("[$clienteId] está removendo uma chave PIX com $pixId")

        val chaveRemovida = removeChavePixClient.remove(
            RemoveChavePixRequest.newBuilder()
                .setClienteId(clienteId.toString())
                .setPixId(pixId.toString())
                .build()
        )
        return HttpResponse.ok<Pair<String, String>>()
            .body(Pair("result", "Chave PIX [${chaveRemovida.pixId}] foi removida"))
    }
}