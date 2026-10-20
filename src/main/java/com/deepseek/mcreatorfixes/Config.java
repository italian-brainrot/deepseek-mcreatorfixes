package com.deepseek.mcreatorfixes;

import net.neoforged.neoforge.common.ModConfigSpec;

public final class Config {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.BooleanValue SUPPRESS_POLARS_GRAVITY = BUILDER
            .comment(
                    "Cancel the polars_exploration_additions Player2Procedure entity tick listener.",
                    "That listener is meant to adjust End gravity, but its else-branch runs",
                    "'/attribute @s minecraft:generic.gravity base set 0.08' and",
                    "'/attribute @s minecraft:generic.safe_fall_distance base set 3' on EVERY",
                    "entity in EVERY non-End dimension, every tick.")
            .define("suppressPolarsEndGravity", true);

    public static final ModConfigSpec.BooleanValue SUPPRESS_GAUNTLET = BUILDER
            .comment(
                    "Cancel the the_world_beneath GauntletNotInHandTickProcedure entity tick listener.",
                    "That listener runs '/attribute @s forge:block_reach base set 4' on EVERY",
                    "entity, every tick, in every dimension.")
            .define("suppressWorldBeneathGauntlet", true);

    public static final ModConfigSpec.BooleanValue PLAYERS_ONLY = BUILDER
            .comment(
                    "When true, the listeners above are only cancelled for non-player entities,",
                    "so their player-facing behaviour (End gravity, block reach) is preserved.",
                    "When false (default) the listeners are cancelled completely, which also",
                    "removes the attribute writes that can interfere with other mods.")
            .define("onlySuppressNonPlayerEntities", false);

    static final ModConfigSpec SPEC = BUILDER.build();

    static void apply() {
        FixState.suppressPolarsGravity = SUPPRESS_POLARS_GRAVITY.getAsBoolean();
        FixState.suppressGauntlet = SUPPRESS_GAUNTLET.getAsBoolean();
        FixState.playersOnly = PLAYERS_ONLY.getAsBoolean();
    }

    private Config() {
    }
}
