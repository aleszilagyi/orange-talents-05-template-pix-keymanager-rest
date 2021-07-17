package br.com.orangetalents.pix.controller.detalha

import br.com.orangetalents.DetalhesChavePixRequest
import br.com.orangetalents.FiltroPorPixId
import br.com.orangetalents.KeyManagerDetalhaPixServiceGrpc
import br.com.orangetalents.pix.dto.DetalheChavePixResponse
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.PathVariable
import io.micronaut.validation.Validated
import org.slf4j.LoggerFactory

@Validated
@Controller("/api/v1/clientes/{clienteId}")
class DetalhaChavePixController(
    private val detalhaChavesPixGRpc: KeyManagerDetalhaPixServiceGrpc.KeyManagerDetalhaPixServiceBlockingStub
) {
    private val LOGGER = LoggerFactory.getLogger(this::class.java)

    @Get("/pix/{pixId}")
    fun carrega(
        clienteId: String,
        @PathVariable pixId: String
    ): HttpResponse<Any> {
        LOGGER.info("[$clienteId] busca detalhes da chave PIX pelo id: $pixId")
        val chaveResponse = detalhaChavesPixGRpc.detalha(
            DetalhesChavePixRequest.newBuilder().setPixId(
                FiltroPorPixId.newBuilder()
                    .setClienteId(clienteId)
                    .setPixId(pixId)
                    .build()
            ).build()
        )

        return HttpResponse.ok(DetalheChavePixResponse.of(chaveResponse))
    }

}