package com.metacontent.cobblenav.client.gui

import com.cobblemon.mod.common.util.math.fromEulerXYZDegrees
import com.metacontent.cobblenav.client.gui.util.RGB
import com.metacontent.cobblenav.client.gui.util.Timer
import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.world.item.ItemDisplayContext
import org.joml.Quaternionf
import org.joml.Vector3f
import kotlin.math.sin

object PokenavSignalManager {
    const val SIGNAL_ITEM_SCALE = 1.05f
    const val ROTATION_AMPLITUDE = 8f
    const val ROTATION_FREQUENCY = 16f
    val INVENTORY_DISPLAY_CONTEXTS = listOf(
        ItemDisplayContext.NONE,
        ItemDisplayContext.GUI
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
    fun isFlickering() = currentSignal != null && isFlickering

    @JvmStatic
    fun getRotation() = Quaternionf().fromEulerXYZDegrees(
        Vector3f(
            0f,
            0f,
            ROTATION_AMPLITUDE * sin(timer.getProgress() * ROTATION_FREQUENCY)
        )
    )

    @JvmStatic
    fun flicker(poseStack: PoseStack) {
        poseStack.scale(SIGNAL_ITEM_SCALE, SIGNAL_ITEM_SCALE, SIGNAL_ITEM_SCALE)
        poseStack.rotateAround(getRotation(), 0f, -0.25f, 0f)
    }

    data class Signal(
        val amount: Int,
        val color: RGB,
        val flickerDuration: Float,
        val idleDuration: Float
    ) {
        internal var flickersLeft = amount

        fun copy() = Signal(amount, color, flickerDuration, idleDuration)
    }
}