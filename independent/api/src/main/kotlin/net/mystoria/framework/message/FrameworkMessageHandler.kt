package net.mystoria.framework.message

import net.mystoria.framework.Framework
import net.mystoria.framework.flavor.service.Configure
import net.mystoria.framework.flavor.service.Service
import net.mystoria.framework.message.listener.FrameworkMessageListener
import net.mystoria.framework.serializer.impl.GsonSerializer

@Service
class FrameworkMessageHandler {

    val MESSAGE_CHANNEL = "Framework:Global"

    val connection = Framework.useWithReturn {
        it.constructNewRedisConnection().getConnection()
    }

    @Configure
    fun configure() {
        connection.connect()
        connection.connectPubSub().addListener(FrameworkMessageListener())
    }

    fun send(message: IMessage) {
        connection.connect().async().publish(MESSAGE_CHANNEL, GsonSerializer.serialize(message))
    }
}