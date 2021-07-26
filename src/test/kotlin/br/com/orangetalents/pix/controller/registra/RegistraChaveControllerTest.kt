package br.com.orangetalents.pix.controller.registra

import br.com.orangetalents.*
import br.com.orangetalents.dto.TipoDeChaveDto
import br.com.orangetalents.dto.TipoDeContaDto
import br.com.orangetalents.pix.config.exception.StatusWithDetails
import br.com.orangetalents.pix.config.grpc.KeyManagerGRpcFactory
import br.com.orangetalents.pix.dto.RegistraChavePixRequestDto
import io.micronaut.context.annotation.Factory
import io.micronaut.context.annotation.Replaces
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpStatus
import io.micronaut.http.client.HttpClient
import io.micronaut.http.client.annotation.Client
import io.micronaut.http.client.exceptions.HttpClientResponseException
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@MicronautTest
internal class RegistraChaveControllerTest {
    companion object {
        val CLIENT_ID = UUID.randomUUID().toString()
        val PIX_ID = UUID.randomUUID().toString()
        val EMAIL_PIX = "teste@gmail.com"
    }

    @Inject
    lateinit var gRpcRegistra: KeyManagerRegistraPixServiceGrpc.KeyManagerRegistraPixServiceBlockingStub

    @Inject
    @field:Client("/")
    lateinit var client: HttpClient

    @BeforeEach
    internal fun setUp() {
        Mockito.reset(gRpcRegistra)
    }

    @Test
    internal fun `deve registrar uma nova chave`() {
        `when`(gRpcRegistra.registra(requestCreatedGrpc())).thenReturn(replyCreatedGrpc())

        val request = HttpRequest.POST("/api/v1/clientes/$CLIENT_ID/pix", novaChavePix())
        val response = client.toBlocking().exchange(request, Any::class.java)

        with(response) {
            assertEquals(HttpStatus.CREATED, status)
            assertTrue(headers.contains("Location"))
            assertTrue(header("Location")!!.contains(PIX_ID))
        }
    }

    @Test
    internal fun `deve retornar erro BAD_REQUEST quando enviar dados invalidos`() {
        `when`(gRpcRegistra.registra(requestErrorGrpc("")))
            .thenThrow(
                io.grpc.Status.INVALID_ARGUMENT.withDescription("Dados inválidos").asRuntimeException()
            )

        val request =
            HttpRequest.POST("/api/v1/clientes/fb7da232-62cd-49a3-92cf-a88a7022f9c0/pix", novaChavePixErrorRequest(""))
        val httpThrow = assertThrows<HttpClientResponseException> {
            client.toBlocking().exchange(request, Any::class.java)
        }

        with(httpThrow.response) {
            assertEquals(HttpStatus.BAD_REQUEST, status)
        }
    }

    @Test
    internal fun `deve retornar erro HTTP_UNPROCESSABLE_ENTITY quando o clienteId nao estiver cadastrado no ITAU`() {
        `when`(gRpcRegistra.registra(requestErrorGrpc(EMAIL_PIX)))
            .thenThrow(
                io.grpc.Status.ALREADY_EXISTS.withDescription("Cliente não encontrado no Itaú").asRuntimeException()
            )

        val request = HttpRequest.POST("/api/v1/clientes/fb7da232-62cd-49a3-92cf-a88a7022f9c0/pix", novaChavePix())
        val httpThrow = assertThrows<HttpClientResponseException> {
            client.toBlocking().exchange(request, Any::class.java)
        }

        with(httpThrow) {
            assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, status)
        }
    }

    internal fun requestErrorGrpc(chavePix: String): RegistraChavePixRequest {
        return RegistraChavePixRequest.newBuilder()
            .setClienteId("fb7da232-62cd-49a3-92cf-a88a7022f9c0")
            .setChavePix(chavePix)
            .setTipoDeChave(TipoDeChave.EMAIL)
            .setTipoDeConta(TipoDeConta.CONTA_CORRENTE)
            .build()
    }

    internal fun requestCreatedGrpc(): RegistraChavePixRequest {
        return RegistraChavePixRequest.newBuilder()
            .setClienteId(CLIENT_ID)
            .setChavePix(EMAIL_PIX)
            .setTipoDeChave(TipoDeChave.EMAIL)
            .setTipoDeConta(TipoDeConta.CONTA_CORRENTE)
            .build()
    }

    internal fun replyCreatedGrpc(): RegistraChavePixReply {
        return RegistraChavePixReply.newBuilder()
            .setClienteId(CLIENT_ID)
            .setPixId(PIX_ID)
            .build()
    }

    internal fun novaChavePix(): RegistraChavePixRequestDto {
        return RegistraChavePixRequestDto(
            tipoDeConta = TipoDeContaDto.CONTA_CORRENTE,
            chavePix = EMAIL_PIX,
            tipoDeChave = TipoDeChaveDto.EMAIL
        )
    }

    internal fun novaChavePixErrorRequest(chavePix: String): RegistraChavePixRequestDto {
        return RegistraChavePixRequestDto(
            tipoDeConta = TipoDeContaDto.CONTA_CORRENTE,
            chavePix = chavePix,
            tipoDeChave = TipoDeChaveDto.EMAIL
        )
    }

    @Factory
    @Replaces(factory = KeyManagerGRpcFactory::class) // Precisa substituir a factory de client gRPC que tá no projeto para utilizar o contexto de testes
    internal class MockitoGRpcFactory {
        @Singleton
        fun registraGRpcMock() =
            mock(KeyManagerRegistraPixServiceGrpc.KeyManagerRegistraPixServiceBlockingStub::class.java)
    }
}