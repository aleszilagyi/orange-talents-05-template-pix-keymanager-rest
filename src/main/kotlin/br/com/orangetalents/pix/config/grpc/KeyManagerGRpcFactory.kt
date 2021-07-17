package br.com.orangetalents.pix.config.grpc

import br.com.orangetalents.KeyManagerDetalhaPixServiceGrpc
import br.com.orangetalents.KeyManagerListaPixServiceGrpc
import br.com.orangetalents.KeyManagerRegistraPixServiceGrpc
import br.com.orangetalents.KeyManagerRemovePixServiceGrpc
import io.grpc.ManagedChannel
import io.micronaut.context.annotation.Factory
import io.micronaut.grpc.annotation.GrpcChannel
import javax.inject.Singleton

@Factory
class KeyManagerGRpcFactory(@GrpcChannel("keyManager") val channel: ManagedChannel) {
    @Singleton
    fun registraChave() = KeyManagerRegistraPixServiceGrpc.newBlockingStub(channel)

    @Singleton
    fun removeChave() = KeyManagerRemovePixServiceGrpc.newBlockingStub(channel)

    @Singleton
    fun listaChaves() = KeyManagerListaPixServiceGrpc.newBlockingStub(channel)

    @Singleton
    fun detalhaChave() = KeyManagerDetalhaPixServiceGrpc.newBlockingStub(channel)
}