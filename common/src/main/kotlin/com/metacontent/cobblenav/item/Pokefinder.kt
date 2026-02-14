package com.metacontent.cobblenav.item

import com.metacontent.cobblenav.client.gui.screen.pokefinder.PokefinderScreen
import com.metacontent.cobblenav.client.isGui
import com.metacontent.cobblenav.util.cobblenavResource
import net.minecraft.client.Minecraft
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResultHolder
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemDisplayContext
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level

class Pokefinder(color: String) : ConditionalModelItem(Properties().stacksTo(1)) {
    companion object {
        const val BASE_REGISTRY_KEY: String = "pokefinder_item_"
        const val TRANSLATION_KEY = "item.cobblenav.pokefinder_item"
    }

    val baseModel = cobblenavResource("$BASE_REGISTRY_KEY$color")
    val inHandModel = cobblenavResource("model/$BASE_REGISTRY_KEY$color")
    val openedInHandModel = cobblenavResource("model/open/$BASE_REGISTRY_KEY$color")

    override fun use(
        level: Level,
        player: Player,
        interactionHand: InteractionHand
    ): InteractionResultHolder<ItemStack> {
        if (level.isClientSide()) {
            Minecraft.getInstance().setScreen(PokefinderScreen())
        }
        return InteractionResultHolder.sidedSuccess(player.getItemInHand(interactionHand), false)
    }

    override fun getDescriptionId(): String {
        return TRANSLATION_KEY
    }

    override fun getModel(stack: ItemStack, displayContext: ItemDisplayContext): ResourceLocation {
        return if (displayContext.isGui()) {
            baseModel
        } else {
            if (Minecraft.getInstance().screen is PokefinderScreen && displayContext.firstPerson()) {
                return openedInHandModel
            }
            inHandModel
        }
    }
}