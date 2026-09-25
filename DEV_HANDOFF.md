# Shattered Pixel Dungeon Survival Prototype - Development Handoff

## Branch
survival-prototype

## Current Safe Baseline
Commit: c2b30a008
Message: Remove obsolete house tile visuals

## Protected Experimental Work
A Git stash was created before beginning the Codex workflow.

Stash description:
pre-codex house movement and depth experiments

Do not apply or delete this stash unless specifically instructed.

## Project Direction
This fork is being developed into a high-angle 2D zombie looter-survival game.

Important design rules:

- Keep the hidden 32px logical grid for world generation, AI, navigation, and placement.
- Player movement should feel smooth and continuous.
- WASD movement should support smooth wall/surface sliding.
- Mouse/right-stick aiming will eventually be independent from movement.
- Buildings use artwork independent from their collision footprint.
- Exterior collision should follow the physical ground-contact footprint, not the roof silhouette.
- Buildings will eventually become reusable procedural prefabs.
- Entering a building will eventually transition to a separate interior level/view.
- Do not globally change DungeonTilemap.SIZE.
- Do not put SurvivalTerrain IDs into Level.map.
- Do not mass-delete original SPD systems. Disconnect/refactor safely.

## Current House
The detailed survival house is rendered by SurvivalHouse.java.

Current origin:
HOUSE_X = 4
HOUSE_Y = 3

Collision uses COLLISION_POLYGON in SurvivalHouse.java.

SurvivalMovement.java handles continuous movement and polygon surface sliding.

## Current Bug / First Codex Task
The right-side exterior wall of the detailed house needs investigation.

Expected behavior:
When the Survivor approaches the angled right exterior wall and holds W while in contact with it, the Survivor should smoothly follow the wall upward in a straight path parallel to the wall.

Current observed behavior:
The Survivor gradually pulls away from the right wall while holding only W instead of remaining close and parallel to the wall.

Important:
Do not immediately modify the movement algorithm.

First inspect:
- SurvivalMovement.java
- SurvivalHouse.java
- relevant SurvivalTestLevel.java collision/terrain setup
- relevant GameScene.java survival rendering/player setup

Determine whether the right-side collision geometry accurately matches the visible exterior wall and whether remapping that wall is preferable to changing universal movement behavior.

Normal open-space WASD movement must not be degraded.

## Codex Workflow Rules
1. Inspect before editing.
2. First task is READ-ONLY investigation.
3. Explain findings before making changes.
4. Keep edits narrowly scoped.
5. Run appropriate Gradle compile/tests after edits.
6. Review git diff before committing.
7. Make small logical commits.
8. Update this DEV_HANDOFF.md after meaningful milestones.
9. Record commit hash, files changed, tests/build results, unfinished work, and recommended next step.
10. Never wait until usage limits are nearly exhausted before updating the handoff.

## Build / Test
Repository:
C:\Users\lj112\shattered-pixel-dungeon

Core compile:
.\gradlew.bat core:compileJava

Desktop debug:
.\gradlew.bat desktop:debug

Gradle compile is the source of truth. Existing IDE warnings do not necessarily indicate build failure.

## Next Action
Connect Codex to this repository and perform a read-only inspection of the right-side exterior house wall movement/collision issue.

Do not edit files during the initial inspection.