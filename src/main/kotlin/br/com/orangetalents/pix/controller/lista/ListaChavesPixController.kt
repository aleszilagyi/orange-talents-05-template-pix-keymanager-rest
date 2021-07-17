package br.com.orangetalents.pix.controller.lista

import br.com.orangetalents.KeyManagerListaPixServiceGrpc
import br.com.orangetalents.ListaChavesPixRequest
import br.com.orangetalents.pix.dto.ChavePixResponse
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.validation.Validated
import org.slf4j.LoggerFactory

@Validated
@Controller("/api/v1/clientes/{clienteId}")
class ListaChavesPixController(private val listaPixServiceGrpc: KeyManagerListaPixServiceGrpc.KeyManagerListaPixServiceBlockingStub) {
    private val LOGGER = LoggerFactory.getLogger(this::class.java)


    @Get("/pix/")
    fun lista(clienteId: String) : HttpResponse<Any> {

        LOGGER.info("[$clienteId] listando chaves pix")
        val pix = listaPixServiceGrpc.lista(
            ListaChavesPixRequest.newBuilder()
            .setClienteId(clienteId)
            .build())

        val chaves = pix.chavesList.map { chave -> ChavePixResponse.of(chave) }
        return HttpResponse.ok(chaves)
    }
}