package com.deepseek.mcreatorfixes;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Live flags read by the mixins. Defaults are applied before the config loads,
 * so the fixes are active even if an entity tick happens during startup.
 */
public final class FixState {
    public static volatile boolean suppressPolarsGravity = true;
    public static volatile boolean suppressGauntlet = true;
    public static volatile boolean playersOnly = false;

    public static final AtomicBoolean LOGGED_POLARS = new AtomicBoolean();
    public static final AtomicBoolean LOGGED_GAUNTLET = new AtomicBoolean();

    private FixState() {
    }
}
