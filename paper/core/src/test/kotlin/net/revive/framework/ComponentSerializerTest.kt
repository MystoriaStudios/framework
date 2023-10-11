package net.revive.framework

import net.kyori.adventure.text.Component
import net.revive.framework.adapters.ComponentAdapter
import net.revive.framework.constants.Tailwind
import net.revive.framework.serializer.impl.GsonSerializer
import net.revive.framework.utils.buildComponent
import kotlin.test.DefaultAsserter.assertEquals
import kotlin.test.Test

internal class ComponentSerializerTest {

    private val testConfig: TestConfig = TestConfig()

    @Test
    fun testMiniMessageSerializer() {
        GsonSerializer.useGsonBuilderThenRebuild {
            it.disableHtmlEscaping()
            it.registerTypeAdapter(Component::class.java, ComponentAdapter)
        }

        println(
            GsonSerializer.serialize(testConfig)
        )
    }
}

data class TestConfig(
    val component: Component = buildComponent {
        text("Testing 123", Tailwind.AMBER_100)
    }
)