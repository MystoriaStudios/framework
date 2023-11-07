package net.revive.framework.rabbitmq

import com.rabbitmq.client.*
import net.revive.framework.Framework
import net.revive.framework.FrameworkApp
import net.revive.framework.flavor.service.Configure
import net.revive.framework.flavor.service.Service
import net.revive.framework.rabbitmq.consumer.IRabbitMQCacheConsumer
import net.revive.framework.utils.objectInstance
import java.nio.charset.Charset

//@Service
object RabbitMQService {

    lateinit var connection: Connection
    val consumers: MutableMap<String, IRabbitMQCacheConsumer<*, *>> = mutableMapOf()

    @Configure
    fun configure() {
        connection = ConnectionFactory().apply {
            host = "localhost"
        }.newConnection()

        Framework.use { framework ->
            framework.flavor.reflections
                .getSubTypes<IRabbitMQCacheConsumer<Any, Any>>()
                .mapNotNull { it.objectInstance() }
                .filterIsInstance<IRabbitMQCacheConsumer<Any, Any>>()
                .forEach { consumer ->
                    consumers[consumer.queue] = consumer

                    val channel = connection.createChannel()
                    channel.queueDeclare(consumer.queue, false, false, false, null)

                    consumer.publish = {
                        channel.basicPublish("", consumer.queue, null, framework.serializer.serialize(it).toByteArray())
                    }

                    val rabbitConsumer = object : DefaultConsumer(channel) {
                        override fun handleDelivery(
                            consumerTag: String,
                            envelope: Envelope,
                            properties: AMQP.BasicProperties,
                            body: ByteArray
                        ) {
                            consumer.consume(
                                framework.serializer.deserialize(
                                    consumer.typeClass,
                                    String(body, Charset.forName("UTF-8"))
                                )
                            )
                        }
                    }

                    channel.basicConsume(consumer.queue, true, rabbitConsumer)
                }
        }
    }
}