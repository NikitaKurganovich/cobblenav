package com.metacontent.cobblenav.item

import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemDisplayContext
import net.minecraft.world.item.ItemStack

abstract class ConditionalModelItem(properties: Properties) : Item(properties) {
    abstract fun getModel(stack: ItemStack, displayContext: ItemDisplayContext): ResourceLocation?
}