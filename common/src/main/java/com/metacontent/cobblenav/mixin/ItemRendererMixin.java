package com.metacontent.cobblenav.mixin;

import com.metacontent.cobblenav.client.gui.PokenavSignalManager;
import com.metacontent.cobblenav.item.Pokenav;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemRenderer.class)
public abstract class ItemRendererMixin {
    @Shadow
    public abstract void render(ItemStack arg, ItemDisplayContext arg2, boolean bl, PoseStack arg3, MultiBufferSource arg4, int i, int j, BakedModel arg5);

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/PoseStack;translate(FFF)V"))
    private void inject(ItemStack itemStack, ItemDisplayContext itemDisplayContext, boolean bl, PoseStack poseStack, MultiBufferSource multiBufferSource, int i, int j, BakedModel bakedModel, CallbackInfo ci) {
        if (!PokenavSignalManager.INSTANCE.getINVENTORY_DISPLAY_CONTEXTS().contains(itemDisplayContext)) return;

        if (itemStack.getItem() instanceof Pokenav) {
            if (PokenavSignalManager.isFlickering()) {
                PokenavSignalManager.flicker(poseStack);
            }
        }
    }
}
