package br.com.orangetalents.pix.controller

import br.com.orangetalents.KeyManagerRegistraPixServiceGrpc
import br.com.orangetalents.RegistraChavePixReply
import br.com.orangetalents.dto.TipoDeChaveDto
import br.com.orangetalents.dto.TipoDeContaDto
import br.com.orangetalents.pix.config.grpc.KeyManagerGRpcFactory
import br.com.orangetalents.pix.dto.RegistraChavePixRequestDto
import io.micronaut.context.annotation.Bean
import io.micronaut.context.annotation.Factory
import io.micronaut.context.annotation.Replaces
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpStatus
import io.micronaut.http.client.HttpClient
import io.micronaut.http.client.annotation.Client
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.given
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.*
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@MicronautTest
internal class RegistraChaveControllerTest(
) {
    companion object {
        val CLIENT_ID = UUID.randomUUID().toString()
        val PIX_ID = UUID.randomUUID().toString()
    }

    @Inject
    lateinit var gRpcRegistra: KeyManagerRegistraPixServiceGrpc.KeyManagerRegistraPixServiceBlockingStub

    @Inject
    @field:Client("/")
    lateinit var client: HttpClient

    @BeforeEach
    internal fun setUp(){
        Mockito.reset(gRpcRegistra)
    }

    @Test
    internal fun `deve registrar uma nova chave`() {
        given(gRpcRegistra.registra(any())).willReturn(respostaGrpc()) //sempre retorna isso aqui, independente do que for enviado, pouco seguro, mas serve para lembrar do uso

        val request = HttpRequest.POST("/api/v1/clientes/$CLIENT_ID/pix", novaChavePix())
        val response = client.toBlocking().exchange(request, novaChavePix().javaClass)

        assertEquals(HttpStatus.CREATED, response.status)
        assertTrue(response.headers.contains("Location"))
        assertTrue(response.header("Location")!!.contains(PIX_ID))
    }

    @Test
    internal fun `deve retornar erro 400 quando o clienteId nao for um UUID valido`() {
        val request = HttpRequest.POST("/api/v1/clientes/banana123/pix", novaChavePix())
        val response = client.toBlocking().exchange(request, novaChavePix().javaClass)

        assertEquals(HttpStatus.BAD_REQUEST, response.status)
    }

    internal fun respostaGrpc(): RegistraChavePixReply {
        return RegistraChavePixReply.newBuilder()
            .setClienteId(CLIENT_ID)
            .setPixId(PIX_ID)
            .build()
    }

    internal fun novaChavePix(): RegistraChavePixRequestDto {
        return RegistraChavePixRequestDto(
            tipoDeConta = TipoDeContaDto.CONTA_CORRENTE,
            chavePix = "teste@gmail.com",
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