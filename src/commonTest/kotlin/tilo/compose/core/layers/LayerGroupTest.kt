package tilo.compose.core.layers

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class LayerGroupTest {
    @Test
    fun childrenAreSnapshottedAtConstruction() {
        val children = mutableListOf<Layer>(testLayer("first"))
        val group = LayerGroup(id = "group", children = children)

        children += testLayer("second")

        assertEquals(listOf("first"), group.children.map(Layer::id))
    }

    @Test
    fun invalidZoomRangeIsRejected() {
        assertFailsWith<IllegalArgumentException> {
            LayerGroup(
                id = "group",
                children = emptyList(),
                minZoom = 12.0,
                maxZoom = 11.0,
            )
        }
    }

    private fun testLayer(id: String): Layer =
        object : Layer {
            override val id = id
        }
}
