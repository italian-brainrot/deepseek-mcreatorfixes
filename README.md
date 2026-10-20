# MCreator Global Tick Fixes

A small NeoForge 1.21.1 utility mod that disables two MCreator-generated
`EntityTickEvent.Pre` listeners which execute commands on **every loaded entity,
every tick**.

## The problem

Both listeners are registered globally, so they run once per entity per tick
(dropped items included), not once per tick:

| Mod | Class | What it spams |
|---|---|---|
| `polars_exploration_additions` | `Player2Procedure` | `attribute @s minecraft:generic.gravity base set 0.08` and `attribute @s minecraft:generic.safe_fall_distance base set 3` for every entity in every non-End dimension |
| `the_world_beneath` | `GauntletNotInHandTickProcedure` | `attribute @s forge:block_reach base set 4` for every entity that is not holding the Builder Gauntlet (i.e. all of them) |

In a profile taken in the overworld with 1,726 loaded entities, these two
listeners were **~23% of the entire server thread**, and the whole
`EntityTickEvent` dispatch (including other MCreator mods) was ~43%.
Because the commands fire for items too, a pile of a thousand dropped items
turns into thousands of command dispatches per tick.

## What this mod does

Two mixins cancel the `onEntityTick` method of those classes at `HEAD`. The
listeners never run, so no commands are executed and no attributes are written
to your entities. Other mods' tick listeners are untouched.

Both targets are optional: if one of the mods is not installed (or a future
version renames the class), Mixin simply never applies that mixin - the game
still starts. Injection uses `require = 0` so a changed method signature
degrades to a no-op with a log warning instead of crashing.

## Config

`config/mcreatorfixes-common.toml`:

```toml
suppressPolarsEndGravity = true
suppressWorldBeneathGauntlet = true
onlySuppressNonPlayerEntities = false
```

- `onlySuppressNonPlayerEntities = true` keeps the listeners running for
  players (so the mods' original player behaviour - End gravity, block reach -
  still works) and suppresses them only for mobs, items and everything else.
- Set both `suppress*` options to `false` to restore vanilla-mod behaviour.

Config changes are picked up on `/reload`-style config reload without a restart.

## Build

```bash
./gradlew build
```

The jar lands in `build/libs/mcreatorfixes-1.0.0.jar`.

## Notes

- The mixins target the classes by name, not by inheritance, so they also cover
  the duplicate `Player2Procedure` bundled inside `polars_end_additions`.
- This does not touch `EntityAnimationFactory` (the `instanceof` animation
  dispatcher used by Koopa's Critters, Luminous, Barnyard Buddies, etc.).
  That is a separate, smaller cost and cancelling it would break those mods'
  animations.
