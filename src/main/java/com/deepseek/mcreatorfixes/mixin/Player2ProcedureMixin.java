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
 * polars_exploration_additions' Player2Procedure is registered on EntityTickEvent.Pre,
 * so it runs for every entity. Its non-End branch executes two /attribute commands
 * per entity per tick, which is the single largest consumer of server thread time
 * in the profile that motivated this mod.
 */
@Mixin(targets = "net.mcreator.polarsexplorationadditions.procedures.Player2Procedure", remap = false)
public class Player2ProcedureMixin {
    @Inject(method = "onEntityTick", at = @At("HEAD"), cancellable = true, require = 0)
    private static void mcreatorfixes$suppress(final EntityTickEvent.Pre event, final CallbackInfo ci) {
        if (!FixState.suppressPolarsGravity) {
            return;
        }
        if (FixState.playersOnly && event.getEntity() instanceof Player) {
            return;
        }
        if (FixState.LOGGED_POLARS.compareAndSet(false, true)) {
            MCreatorFixes.LOGGER.info("[MCreator Fixes] suppressing Player2Procedure.onEntityTick (polars_exploration_additions)");
        }
        ci.cancel();
    }
}
