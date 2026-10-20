package com.deepseek.mcreatorfixes;

import com.mojang.logging.LogUtils;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.config.ModConfigEvent;
import org.slf4j.Logger;

/**
 * Suppresses two broken MCreator "global entity tick" listeners that execute
 * /attribute commands on every loaded entity, every tick.
 */
@Mod(MCreatorFixes.MODID)
public final class MCreatorFixes {
    public static final String MODID = "mcreatorfixes";
    public static final Logger LOGGER = LogUtils.getLogger();

    private static final String POLARS_TARGET =
            "net.mcreator.polarsexplorationadditions.procedures.Player2Procedure";
    private static final String GAUNTLET_TARGET =
            "net.mcreator.theworldbeneath.procedures.GauntletNotInHandTickProcedure";

    public MCreatorFixes(IEventBus modEventBus, ModContainer modContainer) {
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
        modEventBus.addListener(this::onConfigLoading);
        modEventBus.addListener(this::onConfigReloading);
    }

    private void onConfigLoading(ModConfigEvent.Loading event) {
        if (!event.getConfig().getModId().equals(MODID)) {
            return;
        }
        Config.apply();
        logState("loaded");
    }

    private void onConfigReloading(ModConfigEvent.Reloading event) {
        if (!event.getConfig().getModId().equals(MODID)) {
            return;
        }
        Config.apply();
        logState("reloaded");
    }

    private static void logState(String phase) {
        LOGGER.info(
                "[MCreator Fixes] config {}: suppressPolarsEndGravity={}, suppressWorldBeneathGauntlet={}, onlySuppressNonPlayerEntities={}",
                phase,
                FixState.suppressPolarsGravity,
                FixState.suppressGauntlet,
                FixState.playersOnly);

        boolean polars = classPresent(POLARS_TARGET);
        boolean gauntlet = classPresent(GAUNTLET_TARGET);
        LOGGER.info("[MCreator Fixes] target classes found: Player2Procedure={}, GauntletNotInHandTickProcedure={}", polars, gauntlet);
        if (!polars && FixState.suppressPolarsGravity) {
            LOGGER.info("[MCreator Fixes] Player2Procedure is absent; that fix is idle.");
        }
        if (!gauntlet && FixState.suppressGauntlet) {
            LOGGER.info("[MCreator Fixes] GauntletNotInHandTickProcedure is absent; that fix is idle.");
        }
    }

    private static boolean classPresent(String className) {
        try {
            Class.forName(className, false, MCreatorFixes.class.getClassLoader());
            return true;
        } catch (Throwable ignored) {
            return false;
        }
    }
}
