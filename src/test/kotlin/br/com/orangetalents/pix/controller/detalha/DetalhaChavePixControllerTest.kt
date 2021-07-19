package br.com.orangetalents.pix.controller.detalha

import br.com.orangetalents.*
import br.com.orangetalents.pix.config.grpc.KeyManagerGRpcFactory
import br.com.orangetalents.pix.dto.ContaDetalheChavePixResponse
import br.com.orangetalents.pix.dto.DetalheChavePixResponse
import com.google.protobuf.Timestamp
import io.micronaut.context.annotation.Factory
import io.micronaut.context.annotation.Replaces
import io.micronaut.core.annotation.Introspected
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpStatus
import io.micronaut.http.client.HttpClient
import io.micronaut.http.client.annotation.Client
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.slf4j.LoggerFactory
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@MicronautTest
internal class DetalhaChavePixControllerTest {
    companion object {
        val CLIENT_ID = UUID.randomUUID().toString()
        val PIX_ID = UUID.randomUUID().toString()
        val EMAIL_PIX = "teste@gmail.com"
    }

    @Inject
    lateinit var gRpcRegistra: KeyManagerDetalhaPixServiceGrpc.KeyManagerDetalhaPixServiceBlockingStub

    @Inject
    @field:Client("/")
    lateinit var client: HttpClient

    private val LOGGER = LoggerFactory.getLogger(this::class.java)

    @BeforeEach
    internal fun setUp() {
        Mockito.reset(gRpcRegistra)
    }

    @Test
    internal fun `deve detalhar a chave PIX do cliente`() {
        Mockito.`when`(gRpcRegistra.detalha(requestOkGrpc())).thenReturn(replyOkGrpc())

        val request = HttpRequest.GET<Any>("/api/v1/clientes/$CLIENT_ID/pix/$PIX_ID")
        val response = client.toBlocking().exchange(request, Any::class.java)

        LOGGER.info(response.body()!!.toString())

        with(response) {
            Assertions.assertEquals(HttpStatus.OK, status)
            Assertions.assertNotNull(body())
        }
    }

    internal fun requestOkGrpc(): DetalhesChavePixRequest {
        return DetalhesChavePixRequest.newBuilder()
            .setPixId(FiltroPorPixId.newBuilder().setClienteId(CLIENT_ID).setPixId(PIX_ID).build())
            .build()
    }

    internal fun replyOkGrpc(): DetalhesChavePixReply {
        return DetalhesChavePixReply.newBuilder()
            .setClienteId(CLIENT_ID)
            .setPixId(PIX_ID)
            .setChave(
                ChavePix
                    .newBuilder()
                    .setTipo(TipoDeChave.EMAIL)
                    .setChave(EMAIL_PIX)
                    .setConta(
                        ContaInfo.newBuilder()
                            .setTipo(TipoDeConta.CONTA_CORRENTE)
                            .setInstituicao("ITAÚ UNIBANCO S.A")
                            .setNomeDoTitular("Yuri Matheus")
                            .setCpfDoTitular("86135457004")
                            .setAgencia("0001")
                            .setNumeroDaConta("291900")
                            .build()
                    )
                    .setCriadaEm(LocalDateTime.now().let {
                        val createdAt = it.atZone(ZoneId.of("UTC")).toInstant()
                        Timestamp.newBuilder()
                            .setSeconds(createdAt.epochSecond)
                            .setNanos(createdAt.nano)
                            .build()
                    })
            ).build()
    }

    @Factory
    @Replaces(factory = KeyManagerGRpcFactory::class) // Precisa substituir a factory de client gRPC que tá no projeto para utilizar o contexto de testes
    internal class MockitoGRpcFactory {
        @Singleton
        fun registraGRpcMock() =
            Mockito.mock(KeyManagerDetalhaPixServiceGrpc.KeyManagerDetalhaPixServiceBlockingStub::class.java)
    }
}