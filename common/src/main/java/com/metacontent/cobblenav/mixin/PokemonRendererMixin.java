package com.metacontent.cobblenav.mixin;

import com.cobblemon.mod.common.client.render.pokemon.PokemonRenderer;
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.metacontent.cobblenav.event.CobblenavEvents;
import com.metacontent.cobblenav.util.RenderAwareEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PokemonRenderer.class)
public class PokemonRendererMixin {
    @Inject(
            method = "render(Lcom/cobblemon/mod/common/entity/pokemon/PokemonEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
            at = @At("HEAD")
    )
    private void fireEvent(PokemonEntity entity, float entityYaw, float partialTicks, PoseStack poseMatrix, MultiBufferSource buffer, int packedLight, CallbackInfo ci) {
        if (entity instanceof RenderAwareEntity awareEntity && !awareEntity.cobblenav$isRendered()) {
            CobblenavEvents.INSTANCE.getPOKEMON_APPEARED_ON_CLIENT().emit(entity);
            awareEntity.cobblenav$setRendered(true);
        }
    }
}
