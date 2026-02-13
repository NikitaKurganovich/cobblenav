package com.metacontent.cobblenav.client

import com.cobblemon.mod.common.util.math.fromEulerXYZDegrees
import com.metacontent.cobblenav.client.gui.util.Timer
import net.minecraft.world.item.ItemDisplayContext
import org.joml.Quaternionf
import org.joml.Vector3f
import kotlin.math.sin

object PokenavSignalManager {
    val NON_INVENTORY_DISPLAY_CONTEXTS = listOf(
        ItemDisplayContext.HEAD,
        ItemDisplayContext.NONE,
        ItemDisplayContext.GROUND
    )

    private val queue = ArrayDeque<Signal>()

    private var currentSignal: Signal? = null
    val timer = Timer(0f)
    private var isFlickering = false

    @JvmStatic
    fun tick(delta: Float) {
        if (currentSignal == null && queue.isNotEmpty()) {
            currentSignal = queue.removeFirst()
            isFlickering = true
            timer.reset(currentSignal!!.flickerDuration)
        }

        currentSignal ?: return

        if (!timer.isOver()) {
            timer.tick(delta)
            return
        }

        val updatedTime = if (isFlickering) {
            currentSignal!!.idleDuration.also { currentSignal!!.flickersLeft-- }
        } else {
            currentSignal!!.flickerDuration
        }
        timer.reset(updatedTime)
        isFlickering = !isFlickering

        if (currentSignal!!.flickersLeft == 0) {
            currentSignal = null
        }
    }

    @JvmStatic
    fun add(signal: Signal) {
        queue.addLast(signal)
    }

    @JvmStatic
    fun isFlickering() = isFlickering

    @JvmStatic
    fun getRotation() = Quaternionf().fromEulerXYZDegrees(Vector3f(0f, 0f, if (isFlickering) 8f * sin(timer.getProgress() * 16f) else 0f))

    data class Signal(
        val amount: Int,
        val color: Int,
        val flickerDuration: Float,
        val idleDuration: Float
    ) {
        internal var flickersLeft = amount

        fun copy() = Signal(amount, color, flickerDuration, idleDuration)
    }
}