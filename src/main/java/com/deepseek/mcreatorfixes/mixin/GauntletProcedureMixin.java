package com.deepseek.mcreatorfixes.mixin;

import com.deepseek.mcreatorfixes.FixState;
import com.deepseek.mcreatorfixes.MCreatorFixes;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * the_world_beneath's GauntletNotInHandTickProcedure is registered on EntityTickEvent.Pre
 * and executes '/attribute @s forge:block_reach base set 4' for every entity that is not
 * holding the Builder Gauntlet - i.e. for every entity in the world, every tick.
 */
@Mixin(targets = "net.mcreator.theworldbeneath.procedures.GauntletNotInHandTickProcedure", remap = false)
public class GauntletProcedureMixin {
    @Inject(method = "onEntityTick", at = @At("HEAD"), cancellable = true, require = 0)
    private static void mcreatorfixes$suppress(final EntityTickEvent.Pre event, final CallbackInfo ci) {
        if (!FixState.suppressGauntlet) {
            return;
        }
        if (FixState.playersOnly && event.getEntity() instanceof Player) {
            return;
        }
        if (FixState.LOGGED_GAUNTLET.compareAndSet(false, true)) {
            MCreatorFixes.LOGGER.info("[MCreator Fixes] suppressing GauntletNotInHandTickProcedure.onEntityTick (the_world_beneath)");
        }
        ci.cancel();
    }
}
