package br.com.orangetalents.pix.dto

import br.com.orangetalents.ListaChavesPixReply
import br.com.orangetalents.TipoDeChave
import br.com.orangetalents.TipoDeConta
import io.micronaut.core.annotation.Introspected
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset

@Introspected
data class ChavePixResponse(
    val id: String,
    val chave: String,
    val tipoDeChave: TipoDeChave,
    val tipoDeConta: TipoDeConta,
    val criadoEm: LocalDateTime
) {
    companion object {
        fun of(chave: ListaChavesPixReply.ChavePix): ChavePixResponse {
            return ChavePixResponse(
                id = chave.pixId,
                chave = chave.chave,
                tipoDeChave = chave.tipo,
                tipoDeConta = chave.tipoDeConta,
                criadoEm = chave.criadaEm.let {
                    return@let LocalDateTime.ofInstant(
                        Instant.ofEpochSecond(it.seconds, it.nanos.toLong()),
                        ZoneOffset.UTC
                    )
                }
            )
        }
    }
}
