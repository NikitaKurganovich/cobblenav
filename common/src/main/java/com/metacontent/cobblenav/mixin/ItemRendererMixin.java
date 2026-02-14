package com.metacontent.cobblenav.mixin;

import com.cobblemon.mod.common.block.PokeSnackBlock;
import com.metacontent.cobblenav.client.gui.PokenavSignalManager;
import com.metacontent.cobblenav.item.ConditionalModelItem;
import com.metacontent.cobblenav.item.Pokenav;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.ItemModelShaper;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.BlockHitResult;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.cobblemon.mod.common.util.math.QuaternionUtilsKt.fromEulerXYZDegrees;

@Mixin(ItemRenderer.class)
public abstract class ItemRendererMixin {
    @Shadow
    @Final
    private ItemModelShaper itemModelShaper;

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/PoseStack;translate(FFF)V"))
    private void shake(ItemStack itemStack, ItemDisplayContext itemDisplayContext, boolean bl, PoseStack poseStack, MultiBufferSource multiBufferSource, int i, int j, BakedModel bakedModel, CallbackInfo ci) {
        if (itemStack.getItem() instanceof Pokenav) {
            if (!itemDisplayContext.firstPerson() && itemDisplayContext != ItemDisplayContext.GUI) return;
            if (PokenavSignalManager.hasSignal()) {
                PokenavSignalManager.shake(poseStack);
            }

            if (!itemDisplayContext.firstPerson()) return;
            if (Minecraft.getInstance().hitResult instanceof BlockHitResult hitResult) {
                ClientLevel level = Minecraft.getInstance().level;
                if (level == null) return;

                BlockPos pos = hitResult.getBlockPos();
                Block block = level.getBlockState(pos).getBlock();
                if (block instanceof PokeSnackBlock) {
                    Quaternionf rotation = fromEulerXYZDegrees(new Quaternionf(), new Vector3f(0f, 0f, 30f));
                    poseStack.rotateAround(rotation, 0, -0.5f, 0);
                }
            }
        }
    }

    @ModifyVariable(method = "render", at = @At("HEAD"), argsOnly = true)
    public BakedModel flicker(BakedModel bakedModel, ItemStack stack, ItemDisplayContext renderMode) {
        if (stack.getItem() instanceof ConditionalModelItem item) {
            ResourceLocation modelId = item.getModel(stack, renderMode);
            if (modelId == null) return bakedModel;
            return itemModelShaper.getModelManager().getModel(ModelResourceLocation.inventory(modelId));
        }
        return bakedModel;
    }
}
