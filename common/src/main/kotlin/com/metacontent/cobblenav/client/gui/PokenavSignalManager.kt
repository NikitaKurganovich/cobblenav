package com.metacontent.cobblenav.client.gui

import com.cobblemon.mod.common.util.math.fromEulerXYZDegrees
import com.metacontent.cobblenav.client.gui.util.Timer
import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.world.item.ItemDisplayContext
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
    val FITTING_DISPLAY_CONTEXTS = listOf(
        ItemDisplayContext.FIRST_PERSON_LEFT_HAND,
        ItemDisplayContext.FIRST_PERSON_RIGHT_HAND,
        ItemDisplayContext.GUI
    )

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
    fun isFlickering() = hasSignal() && isFlickering

    @JvmStatic
    fun hasSignal() = currentSignal != null

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

    @JvmStatic
    fun isFittingContext(displayContext: ItemDisplayContext) = FITTING_DISPLAY_CONTEXTS.contains(displayContext)

    data class Signal(
        val amount: Int,
        val enabledStateDuration: Float,
        val disabledStateDuration: Float
    ) {
        val duration = amount * (enabledStateDuration + disabledStateDuration) - disabledStateDuration
        internal var flickersLeft = amount
    }
}