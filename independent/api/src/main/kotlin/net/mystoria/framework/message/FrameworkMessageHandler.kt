package net.mystoria.framework.message

import io.lettuce.core.RedisClient
import net.mystoria.framework.Framework
import net.mystoria.framework.message.listener.FrameworkMessageListener
import net.mystoria.framework.serializer.impl.GsonSerializer

class FrameworkMessageHandler {

    val MESSAGE_CHANNEL = "Framework:Global"
    lateinit var connection: RedisClient

    fun configure() {
        connection = Framework.useWithReturn {
            it.constructNewRedisConnection().createNewConnection()
        }

        connection.connect()
        connection.connectPubSub().addListener(FrameworkMessageListener())
    }

    fun send(message: IMessage) {
        connection.connect().async().publish(MESSAGE_CHANNEL, GsonSerializer.serialize(message))
    }
}