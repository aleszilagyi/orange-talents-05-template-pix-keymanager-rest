package br.com.orangetalents.pix.controller.remove

import br.com.orangetalents.*
import br.com.orangetalents.pix.config.grpc.KeyManagerGRpcFactory
import br.com.orangetalents.pix.controller.registra.RegistraChaveControllerTest
import io.micronaut.context.annotation.Factory
import io.micronaut.context.annotation.Replaces
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpStatus
import io.micronaut.http.client.HttpClient
import io.micronaut.http.client.annotation.Client
import io.micronaut.http.client.exceptions.HttpClientResponseException
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@MicronautTest
internal class RemoveChaveControllerTest {

    companion object {
        val CLIENT_ID = UUID.randomUUID().toString()
        val PIX_ID = UUID.randomUUID().toString()
        val EMAIL_PIX = "teste@gmail.com"
    }

    @Inject
    lateinit var gRpcRegistra: KeyManagerRemovePixServiceGrpc.KeyManagerRemovePixServiceBlockingStub

    @Inject
    @field:Client("/")
    lateinit var client: HttpClient

    @BeforeEach
    internal fun setUp() {
        Mockito.reset(gRpcRegistra)
    }

    @Test
    internal fun `deve remover uma chave`() {
        Mockito.`when`(gRpcRegistra.remove(requestOkGrpc())).thenReturn(replyOkGrpc())

        val request = HttpRequest.DELETE<Any>("/api/v1/clientes/$CLIENT_ID/pix/$PIX_ID")
        val response = client.toBlocking().exchange(request, Pair::class.java)

        Assertions.assertEquals(HttpStatus.OK, response.status)
    }

    @Test
    internal fun `deve retornar erro NOT_FOUND quando o clienteId enviar `() {
        Mockito.`when`(gRpcRegistra.remove(requestErrorGrpc()))
            .thenThrow(
                io.grpc.Status.NOT_FOUND.withDescription("Chave PIX não encontrada ou não pertence ao cliente").asRuntimeException()
            )

        val request = HttpRequest.DELETE<Any>("/api/v1/clientes/fb7da232-62cd-49a3-92cf-a88a7022f9c0/pix/$PIX_ID")
        val httpThrow = assertThrows<HttpClientResponseException> {
            client.toBlocking().exchange(request, Pair::class.java)
        }

        with(httpThrow) {
            Assertions.assertEquals(HttpStatus.NOT_FOUND, status)
            Assertions.assertEquals("Chave PIX não encontrada ou não pertence ao cliente", localizedMessage)
        }
    }

    internal fun requestErrorGrpc(): RemoveChavePixRequest {
        return RemoveChavePixRequest.newBuilder()
            .setClienteId("fb7da232-62cd-49a3-92cf-a88a7022f9c0")
            .setPixId(PIX_ID)
            .build()
    }

    internal fun requestOkGrpc(): RemoveChavePixRequest {
        return RemoveChavePixRequest.newBuilder()
            .setClienteId(CLIENT_ID)
            .setPixId(PIX_ID)
            .build()
    }

    internal fun replyOkGrpc(): RemoveChavePixReply {
        return RemoveChavePixReply.newBuilder()
            .setClienteId(CLIENT_ID)
            .setPixId(PIX_ID)
            .build()
    }

    @Factory
    @Replaces(factory = KeyManagerGRpcFactory::class) // Precisa substituir a factory de client gRPC que tá no projeto para utilizar o contexto de testes
    internal class MockitoGRpcFactory {
        @Singleton
        fun registraGRpcMock() =
            Mockito.mock(KeyManagerRemovePixServiceGrpc.KeyManagerRemovePixServiceBlockingStub::class.java)
    }
}