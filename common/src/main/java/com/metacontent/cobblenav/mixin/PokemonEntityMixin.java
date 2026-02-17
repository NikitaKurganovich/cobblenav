package com.metacontent.cobblenav.mixin;

import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.metacontent.cobblenav.util.RenderAwareEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(PokemonEntity.class)
public abstract class PokemonEntityMixin implements RenderAwareEntity {
    @Unique
    private transient boolean cobblenav$isRendered = false;

    @Override
    public boolean cobblenav$isRendered() {
        return cobblenav$isRendered;
    }

    @Override
    public void cobblenav$setRendered(boolean isRendered) {
        this.cobblenav$isRendered = isRendered;
    }
}
