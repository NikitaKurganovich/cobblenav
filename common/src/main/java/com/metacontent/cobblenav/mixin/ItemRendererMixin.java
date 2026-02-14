package com.metacontent.cobblenav.mixin;

import com.metacontent.cobblenav.client.gui.PokenavSignalManager;
import com.metacontent.cobblenav.item.Pokenav;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.ItemModelShaper;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemRenderer.class)
public abstract class ItemRendererMixin {
    @Shadow
    @Final
    private ItemModelShaper itemModelShaper;

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/PoseStack;translate(FFF)V"))
    private void shake(ItemStack itemStack, ItemDisplayContext itemDisplayContext, boolean bl, PoseStack poseStack, MultiBufferSource multiBufferSource, int i, int j, BakedModel bakedModel, CallbackInfo ci) {
        if (!PokenavSignalManager.INSTANCE.getINVENTORY_DISPLAY_CONTEXTS().contains(itemDisplayContext)) return;

        if (itemStack.getItem() instanceof Pokenav) {
            if (PokenavSignalManager.hasSignal()) {
                PokenavSignalManager.shake(poseStack);
            }
        }
    }

    @ModifyVariable(method = "render", at = @At("HEAD"), argsOnly = true)
    public BakedModel flicker(BakedModel bakedModel, ItemStack stack, ItemDisplayContext renderMode) {
        if (!PokenavSignalManager.INSTANCE.getINVENTORY_DISPLAY_CONTEXTS().contains(renderMode)) return bakedModel;

        if (stack.getItem() instanceof Pokenav pokenav) {
            if (PokenavSignalManager.isFlickering()) {
                return itemModelShaper.getModelManager().getModel(ModelResourceLocation.inventory(pokenav.getFlickeringModel()));
            }
        }

        return bakedModel;
    }
}
