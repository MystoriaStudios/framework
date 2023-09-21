package net.mystoria.framework.message.listener

import io.lettuce.core.pubsub.RedisPubSubListener
import net.mystoria.framework.Framework
import net.mystoria.framework.message.IMessage
import net.mystoria.framework.serializer.impl.GsonSerializer

class FrameworkMessageListener : RedisPubSubListener<String, String> {

    /**
     * Message received from a channel subscription.
     *
     * @param channel Channel.
     * @param message Message.
     */
    override fun message(channel: String, message: String) {
        Framework.use {
            val split = message.indexOf("||")
            val packetClassStr = message.substring(0, split)
            val messageJson = message.substring(split + 2)

            val clazz = try {
                Class.forName(packetClassStr)
            } catch (ignoRED: ClassNotFoundException) {
                return@use
            }

            (GsonSerializer.deserialize(clazz.kotlin, messageJson) as IMessage).onReceive()
        }
    }

    override fun message(pattern: String?, channel: String?, message: String?) {}
    override fun subscribed(channel: String?, count: Long) {}
    override fun psubscribed(pattern: String?, count: Long) {}
    override fun unsubscribed(channel: String?, count: Long) {}
    override fun punsubscribed(pattern: String?, count: Long) {}
}