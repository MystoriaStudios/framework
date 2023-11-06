package net.revive.framework.rabbitmq.consumer

import kotlin.reflect.KClass

interface IRabbitMQCacheConsumer<T : Any, ID : Any> {

    val typeClass: KClass<T>
    val idClass: KClass<ID>

    val queue: String

    var publish: (T) -> Unit

    fun consume(message: T)

    fun fetch(id: ID) : T?
}