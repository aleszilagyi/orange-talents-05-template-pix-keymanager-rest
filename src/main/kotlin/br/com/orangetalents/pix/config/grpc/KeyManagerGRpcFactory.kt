package br.com.orangetalents.pix.config.grpc

import br.com.orangetalents.KeyManagerRegistraPixServiceGrpc
import io.grpc.ManagedChannel
import io.micronaut.context.annotation.Factory
import io.micronaut.grpc.annotation.GrpcChannel
import javax.inject.Singleton

@Factory
class KeyManagerGRpcFactory(@GrpcChannel("keyManager") val channel: ManagedChannel) {
    @Singleton
    fun registraChave() = KeyManagerRegistraPixServiceGrpc.newBlockingStub(channel)
}