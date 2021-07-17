package br.com.orangetalents.pix.dto

import br.com.orangetalents.DetalhesChavePixReply
import br.com.orangetalents.TipoDeChave
import br.com.orangetalents.TipoDeConta
import io.micronaut.core.annotation.Introspected
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset

@Introspected
data class DetalheChavePixResponse(
    private val pixId: String,
    private val tipoDeChave: TipoDeChave,
    private val chave: String,
    private val criadoEm: LocalDateTime,
    private val conta: ContaDetalheChavePixResponse
) {
    companion object {
        fun of(detalhes: DetalhesChavePixReply): DetalheChavePixResponse {
            return DetalheChavePixResponse(
                pixId = detalhes.pixId,
                tipoDeChave = detalhes.chave.tipo,
                chave = detalhes.chave.chave,
                criadoEm = detalhes.chave.criadaEm.let {
                    return@let LocalDateTime.ofInstant(
                        Instant.ofEpochSecond(it.seconds, it.nanos.toLong()),
                        ZoneOffset.UTC
                    )
                },
                conta = ContaDetalheChavePixResponse.of(detalhes)
            )
        }
    }
}

data class ContaDetalheChavePixResponse(
    private val tipoDeConta: String,
    private val instituicao: String,
    private val nomeDoTitular: String,
    private val cpfDoTitular: String,
    private val agencia: String,
    private val numero: String
) {
    companion object {
        fun of(detalhes: DetalhesChavePixReply): ContaDetalheChavePixResponse {
            return ContaDetalheChavePixResponse(
                tipoDeConta = when (detalhes.chave.conta.tipo) {
                    TipoDeConta.CONTA_CORRENTE -> "CONTA_CORRENTE"
                    TipoDeConta.CONTA_POUPANCA -> "CONTA_POUPANCA"
                    else -> "NAO_RECONHECIDA"
                },
                instituicao = detalhes.chave.conta.instituicao,
                nomeDoTitular = detalhes.chave.conta.nomeDoTitular,
                cpfDoTitular = detalhes.chave.conta.cpfDoTitular,
                agencia = detalhes.chave.conta.agencia,
                numero = detalhes.chave.conta.numeroDaConta
            )
        }
    }
}
