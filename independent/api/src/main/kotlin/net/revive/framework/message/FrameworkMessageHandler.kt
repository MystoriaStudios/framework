package net.revive.framework.message

import io.lettuce.core.RedisClient
import net.revive.framework.Framework
import net.revive.framework.message.listener.FrameworkMessageListener
import net.revive.framework.serializer.impl.GsonSerializer

class FrameworkMessageHandler {

    val MESSAGE_CHANNEL = "Framework:Global"
    lateinit var connection: RedisClient

    fun configure() {
        connection = net.revive.framework.Framework.useWithReturn {
            it.constructNewRedisConnection().createNewConnection()
        }

        connection.connect()
        connection.connectPubSub().addListener(FrameworkMessageListener())
    }

    fun send(message: IMessage) {
        connection.connect().async().publish(MESSAGE_CHANNEL, GsonSerializer.serialize(message))
    }
}