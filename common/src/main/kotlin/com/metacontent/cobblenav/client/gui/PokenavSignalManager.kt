package com.metacontent.cobblenav.client.gui

import com.cobblemon.mod.common.util.math.fromEulerXYZDegrees
import com.metacontent.cobblenav.client.gui.util.Timer
import com.metacontent.cobblenav.item.Pokefinder
import com.metacontent.cobblenav.item.Pokenav
import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.world.item.ItemStack
import org.joml.Quaternionf
import org.joml.Vector3f
import kotlin.math.PI
import kotlin.math.sin

object PokenavSignalManager {
    const val SIGNAL_ITEM_SCALE = 1.025f
    const val SHAKE_AMPLITUDE = 4f
    const val BASE_SHAKE_FREQUENCY = 16f
    const val BASE_SHAKE_DURATION = 10f
    const val WAIT_DURATION = 10f

    val SPAWN_CATALOGUED_SIGNAL = Signal(1, 10f, 0f) {
        it.item is Pokenav
    }
    val POKEMON_APPEARED_SIGNAL = Signal(1, 5f, 0f) {
        it.item is Pokefinder
    }

    private val queue = ArrayDeque<Signal>()

    private var currentSignal: Signal? = null
    private val timer = Timer(0f)
    private val shakeTimer = Timer(0f)
    private val waitTimer = Timer(WAIT_DURATION)
    private var isFlickering = false

    @JvmStatic
    fun tick(delta: Float) {
        if (!waitTimer.isOver()) {
            waitTimer.tick(delta)
            return
        }

        if (currentSignal == null && queue.isNotEmpty()) {
            currentSignal = queue.removeFirst()
            isFlickering = true
            timer.reset(currentSignal!!.enabledStateDuration)
            shakeTimer.reset(currentSignal!!.duration)
            return
        }

        currentSignal ?: return

        shakeTimer.tick(delta)

        if (!timer.isOver()) {
            timer.tick(delta)
            return
        }

        val updatedTime = if (isFlickering) {
            currentSignal!!.disabledStateDuration.also { currentSignal!!.flickersLeft-- }
        } else {
            currentSignal!!.enabledStateDuration
        }
        timer.reset(updatedTime)
        isFlickering = !isFlickering

        if (currentSignal!!.flickersLeft == 0) {
            currentSignal = null
            waitTimer.reset()
        }
    }

    @JvmStatic
    fun add(signal: Signal) {
        queue.addLast(signal)
    }

    @JvmStatic
    fun isFlickering(stack: ItemStack) = hasSignal(stack) && isFlickering

    @JvmStatic
    fun hasSignal(stack: ItemStack) = currentSignal != null && currentSignal!!.itemSelector(stack)

    @JvmStatic
    fun getRotation() = Quaternionf().fromEulerXYZDegrees(
        Vector3f(
            0f,
            0f,
            SHAKE_AMPLITUDE * sin(shakeTimer.getProgress() * PI.toFloat() / 2 * (BASE_SHAKE_FREQUENCY * currentSignal!!.duration / BASE_SHAKE_DURATION))
        )
    )

    @JvmStatic
    fun shake(poseStack: PoseStack) {
        poseStack.scale(SIGNAL_ITEM_SCALE, SIGNAL_ITEM_SCALE, SIGNAL_ITEM_SCALE)
        poseStack.rotateAround(getRotation(), 0f, -0.25f, 0f)
    }

    data class Signal(
        val amount: Int,
        val enabledStateDuration: Float,
        val disabledStateDuration: Float,
        val itemSelector: (ItemStack) -> Boolean
    ) {
        val duration = amount * (enabledStateDuration + disabledStateDuration) - disabledStateDuration
        internal var flickersLeft = amount
    }
}