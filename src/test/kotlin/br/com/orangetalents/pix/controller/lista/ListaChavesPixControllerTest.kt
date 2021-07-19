package br.com.orangetalents.pix.controller.lista

import br.com.orangetalents.*
import br.com.orangetalents.pix.config.grpc.KeyManagerGRpcFactory
import com.google.protobuf.Timestamp
import io.micronaut.context.annotation.Factory
import io.micronaut.context.annotation.Replaces
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpStatus
import io.micronaut.http.client.HttpClient
import io.micronaut.http.client.annotation.Client
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@MicronautTest
internal class ListaChavesPixControllerTest {
    companion object {
        val CLIENT_ID = UUID.randomUUID().toString()
        val PIX_ID = UUID.randomUUID().toString()
        val EMAIL_PIX = "teste@gmail.com"
    }

    @Inject
    lateinit var gRpcRegistra: KeyManagerListaPixServiceGrpc.KeyManagerListaPixServiceBlockingStub

    @Inject
    @field:Client("/")
    lateinit var client: HttpClient

    @BeforeEach
    internal fun setUp() {
        Mockito.reset(gRpcRegistra)
    }

    @Test
    internal fun `deve listar as chaves do cliente`() {
        Mockito.`when`(gRpcRegistra.lista(requestOkGrpc())).thenReturn(replyOkGrpc())

        val request = HttpRequest.GET<Any>("/api/v1/clientes/$CLIENT_ID/pix")
        val response = client.toBlocking().exchange(request, List::class.java)

        with(response) {
            Assertions.assertEquals(HttpStatus.OK, status)
            Assertions.assertNotNull(body())
            Assertions.assertEquals(body()!!.size, 2)
            Assertions.assertTrue(body()!!.toString().contains(EMAIL_PIX))
        }
    }

    internal fun requestOkGrpc(): ListaChavesPixRequest {
        return ListaChavesPixRequest.newBuilder()
            .setClienteId(CLIENT_ID)
            .build()
    }

    internal fun createChavePixEmail(): ListaChavesPixReply.ChavePix {
        return ListaChavesPixReply.ChavePix.newBuilder()
            .setPixId(UUID.randomUUID().toString())
            .setTipo(TipoDeChave.EMAIL)
            .setChave(EMAIL_PIX)
            .setTipoDeConta(TipoDeConta.CONTA_CORRENTE)
            .setCriadaEm(LocalDateTime.now().let {
                val createdAt = it.atZone(ZoneId.of("UTC")).toInstant()
                Timestamp.newBuilder()
                    .setSeconds(createdAt.epochSecond)
                    .setNanos(createdAt.nano)
                    .build()
            })
            .build()
    }

    internal fun createChavePixAleatoria(): ListaChavesPixReply.ChavePix {
        return ListaChavesPixReply.ChavePix.newBuilder()
            .setPixId(UUID.randomUUID().toString())
            .setTipo(TipoDeChave.ALEATORIA)
            .setChave(UUID.randomUUID().toString())
            .setTipoDeConta(TipoDeConta.CONTA_CORRENTE)
            .setCriadaEm(LocalDateTime.now().let {
                val createdAt = it.atZone(ZoneId.of("UTC")).toInstant()
                Timestamp.newBuilder()
                    .setSeconds(createdAt.epochSecond)
                    .setNanos(createdAt.nano)
                    .build()
            })
            .build()
    }

    internal fun replyOkGrpc(): ListaChavesPixReply {
        return ListaChavesPixReply.newBuilder()
            .setClienteId(CLIENT_ID)
            .addAllChaves(listOf(createChavePixEmail(), createChavePixAleatoria()))
            .build()
    }

    @Factory
    @Replaces(factory = KeyManagerGRpcFactory::class) // Precisa substituir a factory de client gRPC que tá no projeto para utilizar o contexto de testes
    internal class MockitoGRpcFactory {
        @Singleton
        fun registraGRpcMock() =
            Mockito.mock(KeyManagerListaPixServiceGrpc.KeyManagerListaPixServiceBlockingStub::class.java)
    }
}