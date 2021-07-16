package br.com.orangetalents.pix.dto

import br.com.orangetalents.RegistraChavePixRequest
import br.com.orangetalents.TipoDeChave
import br.com.orangetalents.TipoDeConta
import br.com.orangetalents.dto.TipoDeChaveDto
import br.com.orangetalents.dto.TipoDeContaDto
import br.com.orangetalents.pix.config.validator.ValidTipoChave
import io.micronaut.core.annotation.Introspected
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

@Introspected
@ValidTipoChave
data class RegistraChavePixRequestDto(
    @field:NotNull val tipoDeChave: TipoDeChaveDto?,
    @field:Size(max = 77) val chavePix: String?,
    @field:NotNull val tipoDeConta: TipoDeContaDto?
) {

    fun toProtoGrpc(clienteId: String): RegistraChavePixRequest {
        return RegistraChavePixRequest.newBuilder()
            .setClienteId(clienteId.toString())
            .setTipoDeConta(
                TipoDeConta.valueOf(tipoDeConta?.name ?: TipoDeConta.UNKNOWN_CONTA.toString())
            )
            .setTipoDeChave(
                TipoDeChave.valueOf(tipoDeChave?.name ?: TipoDeChave.UNKNOWN_CHAVE.toString())
            )
            .setChavePix(chavePix ?: "")
            .build()
    }
}
