# Shattered Pixel Dungeon Survival Prototype - Development Handoff

## Branch
survival-prototype

## Current Safe Baseline
Commit: 3d2481397
Message: Add collision mapper coordinate export
Repository was clean at this checkpoint before the mapper-mode changes below.

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
Manually verify HOUSE/PROP draft switching and mode-tagged exports with F10/F11.
See the latest Mapper Modes entry below; earlier entries record historical work.

## Temporary Collision Overlay - 2026-09-25

The initial read-only inspection is complete. A visual-only debugging change is
implemented but uncommitted; the baseline commit remains c2b30a008.

Changed files: GameScene.java, new SurvivalHouseDebugOverlay.java, and this handoff.
The overlay is enabled only for SurvivalTestLevel with district.devtools=true
(already set by desktop:debug). It draws the complete polygon in magenta, white
vertices numbered 0-7, the continuous movement center in yellow, and its 12x12
footprint in cyan. Right-side references are 0 (rear-right), 1 (utility side),
and 2 (porch start). Remove the overlay class and its GameScene hook/imports
after debugging. No collision coordinates, movement behavior, or artwork changed.

Validation: core:compileJava passed (Gradle cache access required running outside
the sandbox); git diff --check passed. desktop:debug was not run because it launches
interactive gameplay. Visual verification is still pending. No stash was applied
and no commit was made.

Next step: launch desktop:debug, approach the right-side polygon, and hold only W.
Compare the cyan footprint against magenta edges 0-1 and 1-2 and compare those
edges against the visible foundation. Observe whether separation grows from the
artwork, the polygon, or both, especially near vertex 1. Do not remap geometry
or change movement until the visual comparison has been reviewed.

## Proposed Polygon Preview - 2026-09-25

The user's in-game test confirmed that W movement follows active edge 1-0:
the cyan footprint stays aligned with collision, while the artwork diverges.
Movement/sliding is working correctly and must remain unchanged.

SurvivalHouseDebugOverlay now also draws the requested P0-P8 proposal in green,
with P0-P8 labels. Rear edges P7-P8 and P8-P0 are dashed orange, and P8 is
labelled provisional. The exact supplied coordinates live in a private preview
array used only by rendering. Active collision remains magenta; the yellow
center and cyan footprint remain visible. The existing developer-tools gate
is unchanged. Neither SurvivalHouse nor SurvivalMovement nor artwork was edited.

Files changed this step: SurvivalHouseDebugOverlay.java and this handoff.
Validation: core:compileJava passed; git diff --check passed. Preview appearance
still needs in-game review. No commit was made; baseline remains c2b30a008.
Next step: compare the two outlines in desktop:debug, especially the provisional
rear area. Do not activate the proposal or make collision adjustments yet.

## House-Local Position Label - 2026-09-25

SurvivalHouseDebugOverlay now displays Local X and Local Y to one decimal place
on a dark background beside the Survivor. Values use the continuous movement
center minus (HOUSE_X * CELL_SIZE, HOUSE_Y * CELL_SIZE), with no sprite offset.
Both polygons, provisional rear edges, center marker, footprint, and developer
gate are unchanged. This is visualization-only; no collision or movement edits.

Files changed this step: SurvivalHouseDebugOverlay.java and this handoff.
Validation: core:compileJava passed; git diff --check passed. In-game readability
still needs review. No commit made; baseline remains c2b30a008.
Next step: use the label to record house-local positions during visual remap
review. Do not activate or adjust either polygon yet.

## Developer Collision Toggle - 2026-09-25

The user authorized testing the proposal as actual collision. Press F8 in
desktop:debug to toggle CURRENT/PREVIEW; F8 is unused by existing survival/default
controls. The overlay displays Collision: CURRENT or Collision: PREVIEW beside
Local X/Y. Both outlines, orange provisional rear segments, yellow center, and
cyan 12x12 footprint remain visible.

New devtools/SurvivalHouseCollisionTest.java owns the single shared P0-P8 array
and an unsaved mode flag. Preview selection requires district.devtools=true and
SurvivalTestLevel. CURRENT is the process startup default, and overlay creation
and destruction also reset it. SurvivalMovement selects this geometry for both
the detailed-house corner tests and the existing sliding call. CURRENT returns
the original SurvivalHouse.COLLISION_POLYGON reference. Its coordinates, movement
speed, and the slideAlongPolygon implementation are unchanged.

Files changed this step: new SurvivalHouseCollisionTest.java,
SurvivalHouseDebugOverlay.java, SurvivalMovement.java, and this handoff.
GameScene.java retains its earlier uncommitted overlay hook without new edits.
Validation: core:compileJava passed after allowing Gradle cache access outside
the sandbox; git diff --check passed. In-game toggle/visual testing is pending.
No commit made; baseline remains c2b30a008. Protected stash untouched.
Next step: launch desktop:debug and compare wall contact in both modes with F8.
Toggle from a position outside both polygons to avoid starting inside the newly
selected footprint. Preview rear edges remain provisional.

## Preview Clearance Adjustment - 2026-09-25

In-game testing confirmed the preview shape. Added 3 world pixels of outward
clearance to edges P0-P1 through P6-P7 using parallel edge offsets and adjacent
line intersections, preserving edge angles rather than independently remapping
vertices. Provisional rear edge lines P7-P8 and P8-P0 remain fixed, with P8 still
(138, 60); P0 and P7 move along those lines to join the offset outline.

Exact preview coordinate changes (house-local world pixels):
- P0: (323, 132) -> (326.477540, 133.353421)
- P1: (193, 214) -> (194.605575, 216.534199)
- P2: (182, 221) -> (182.326361, 224.348244)
- P3: (118, 196) -> (118.215872, 199.305084)
- P4: (98, 207) -> (98.078604, 210.380582)
- P5: (43, 180) -> (39.215021, 181.483913)
- P6: (48, 167) -> (44.145560, 168.664510)
- P7: (4, 149) -> (0.979952, 151.005853)
- P8: (138, 60), unchanged.

Changed this step: SurvivalHouseCollisionTest.java and DEV_HANDOFF.md only.
CURRENT geometry, movement/sliding, speed, 12x12 footprint, F8, startup default,
and overlay code remain unchanged. Float-rounded geometry retains the intended
3-pixel offsets and parallel edges within 0.0001 world pixels.
Validation: core:compileJava passed; git diff --check passed. In-game clearance
review remains pending. No commit; baseline remains c2b30a008. Stash untouched.
Next step: compare CURRENT/PREVIEW in-game and confirm the subtle clearance.

## Preview Clearance with Locked Steps - 2026-09-25

The user confirmed the front-step spacing is correct. The front face is P4-P5,
corresponding to the right/left step corners. Both endpoints remain exactly
unchanged, as do P0/P7/P8 and the full provisional rear segments P7-P8 and P8-P0.

Moved only P1/P2/P3/P6 to intersections of their adjacent edge lines offset
outward another 3 world pixels. P1-P2 and P2-P3 retain parallel angles; P0-P1,
P3-P4, P5-P6, and P6-P7 taper to the locked endpoints. Those transition edges
necessarily change angle; keeping them parallel would require moving locked
endpoints or adding vertices. No vertices were added and no edges intersect.
Clearance is approximately 3 pixels away from the locked endpoints, tapering
to zero at those endpoints. P1's nearly straight join changes turn from -0.23
to +0.87 degrees; there is no sharp spike.

Exact changes this turn (house-local world pixels):
- P1: (194.605575, 216.534199) -> (196.211150, 219.068398)
- P2: (182.326361, 224.348244) -> (182.652722, 227.696488)
- P3: (118.215872, 199.305084) -> (118.431744, 202.610168)
- P6: (44.145560, 168.664510) -> (40.291120, 170.329020)

Changed this turn: SurvivalHouseCollisionTest.java and DEV_HANDOFF.md only.
CURRENT/SurvivalHouse, SurvivalMovement (including sliding/speed/footprint),
and the debug overlay are unchanged; F8 and Local X/Y remain available.
Validation: core:compileJava and git diff --check passed. Geometry checked for
self-intersections and exact preservation of locked float coordinates.
No commit made; protected stash untouched. Baseline remains c2b30a008.
Next step: in-game review of additional clearance and transitions at the steps.

## Visual-Only Right-Side Prop Proposals - 2026-09-25

Added devtools/SurvivalHousePropDebug.java with rendering-only house-local
footprints. SurvivalHouseDebugOverlay draws blue outlines, vertex numbers,
and separate named callouts marked DEBUG/PROPOSED - VISUAL ONLY. They display
in both CURRENT and PREVIEW under the existing developer overlay gate.
No collision integration, SAT, collider registry, or sliding changes were added.

Initial estimated vertices, in numbered order starting at 0:
- BARREL: (296,173), (293.5,176.5), (287,178), (280.5,176.5),
  (278,173), (280.5,169.5), (287,168), (293.5,169.5).
- AC: (295,153), (308,159), (317,152), (304,146).
- TANK: (336,133), (333.5,136.5), (327,138), (320.5,136.5),
  (318,133), (320.5,129.5), (327,128), (333.5,129.5).
- BOARDS: (267,178), (271,182), (281,178), (277,174).

These estimate bases in the house image at 0.25 scale, not full object heights.
Changed this turn: new SurvivalHousePropDebug.java, SurvivalHouseDebugOverlay.java,
and DEV_HANDOFF.md. Both house polygons and SurvivalMovement are unchanged;
F8 behavior, Local X/Y, and all earlier overlay elements remain intact.
Validation: core:compileJava passed; git diff --check passed; git status --short
run. In-game placement/readability review is pending. No commit; protected stash
untouched. Baseline remains c2b30a008.
Next step: inspect the blue numbered footprints and record local-coordinate
corrections before enabling any prop collision.

## Individual Prop Debug View - 2026-09-25

F9 (unused by existing controls) cycles ALL -> BARREL -> AC -> TANK -> BOARDS
-> ALL, once per press. Each new developer overlay defaults to ALL. Each prop's
outline, markers, vertex numbers, and compact nearby name belong to one group;
single selection hides the other three groups. Removed long-distance leaders
and repeated disclaimer callouts in all modes. The Survivor readout now includes
Prop Debug: <selection> (VISUAL ONLY), alongside unchanged Local X/Y and collision
mode text. F8 logic and every existing house/Survivor debug element are preserved.

Changed this turn: SurvivalHouseDebugOverlay.java and DEV_HANDOFF.md only.
Prop definition, house definition, house preview selector, and movement files
have identical before/after hashes. No geometry edits or prop collision added.
Validation: core:compileJava passed; git diff --check passed; git status --short
run. In-game readability/cycling review remains pending. No commit; stash untouched.
Next step: use F9 to isolate each prop and record coordinate corrections.

## Visual Barrel Expansion - 2026-09-25

Expanded only BARREL by intersecting adjacent edge lines offset outward 2 world
pixels. Center remains exactly (287,173); eight vertices, ordering, and edge
orientations are preserved. Float-rounded offsets are within 0.00002 pixels of 2.
Old -> new coordinates, by vertex:
- 0: (296,173) -> (298.457807,173)
- 1: (293.5,176.5) -> (294.687419,178.278544)
- 2: (287,178) -> (287,180.052564)
- 3: (280.5,176.5) -> (279.312581,178.278544)
- 4: (278,173) -> (275.542193,173)
- 5: (280.5,169.5) -> (279.312581,167.721456)
- 6: (287,168) -> (287,165.947436)
- 7: (293.5,169.5) -> (294.687419,167.721456)

Changed this turn: SurvivalHousePropDebug.java and DEV_HANDOFF.md only.
AC/TANK/BOARDS unchanged. All props remain visual-only. Movement, both house
polygons, and overlay code (including F8/F9/Local X/Y) are unchanged.
Validation: core:compileJava and git diff --check passed; git status --short run.
No commit; protected stash untouched. Next step: inspect expanded BARREL with F9.

## Visual AC Translation - 2026-09-25

Translated only AC by (-2.828427, -2.828427), approximately 4 world pixels
diagonally up-left in total. This is translation only: unchanged shape, size,
edge angles, and vertex order. BARREL is approved/frozen and was not edited.
Old -> new coordinates:
- 0: (295,153) -> (292.171573,150.171573)
- 1: (308,159) -> (305.171573,156.171573)
- 2: (317,152) -> (314.171573,149.171573)
- 3: (304,146) -> (301.171573,143.171573)

Changed this turn: SurvivalHousePropDebug.java and DEV_HANDOFF.md only.
TANK/BOARDS, both house polygons, SurvivalMovement, and overlay code are unchanged.
All props remain visual-only; no collision enabled. F8/F9/Local X/Y preserved.
Validation: core:compileJava and git diff --check passed; git status --short run.
No commit; protected stash untouched. Next step: inspect translated AC with F9.

## Visual BOARDS Translation - 2026-09-25

Translated only BOARDS by (-2.121320, -2.121320), approximately 3 world pixels
diagonally up-left. Shape, size, edge angles, and vertex ordering are unchanged.
Old -> new coordinates:
- 0: (267,178) -> (264.878680,175.878680)
- 1: (271,182) -> (268.878680,179.878680)
- 2: (281,178) -> (278.878680,175.878680)
- 3: (277,174) -> (274.878680,171.878680)

BARREL, AC, and TANK are approved/frozen and unchanged. All props remain
visual-only. Changed this turn: SurvivalHousePropDebug.java and DEV_HANDOFF.md
only. Movement, both house polygons, F8/F9, Local X/Y, speed, and the Survivor
footprint are unchanged. No commit; protected stash untouched.
Validation: core:compileJava and git diff --check passed; git status --short run.
Next step: inspect translated BOARDS with F9.

## PREVIEW-Only Physical Props - 2026-09-25

All four prop footprints are now approved/frozen. In developer PREVIEW mode,
SurvivalMovement checks the unchanged 12x12 rectangle against every prop using
SAT (rectangle axes plus convex-polygon edge normals). This catches containment
and edge crossings, including small props missed by four-corner-only checks.
Exact boundary contact blocks. CURRENT skips all added checks and prop slides.

Each blocked PREVIEW move compares the existing slideAlongPolygon result for
the house and each prop, selecting greatest dot-product progress; strict greater
comparison keeps house/definition order on ties. slideAlongPolygon itself is
unchanged. Its canOccupy validation checks the destination against all props,
the house, and existing blocked-terrain checks. PREVIEW additionally checks
house edges against the rectangle to close the corner-only overlap gap without
assuming the concave house is convex. CURRENT retains its original house test.
Normal speed, open-space movement, axis fallback, and footprint size are unchanged.

F8 controls physical mode; F9 only filters drawings, never collision. Prop status
shows ALL PHYSICAL in PREVIEW and VISUAL ONLY in CURRENT. All debug geometry,
Local X/Y, center, and footprint remain. Startup default remains CURRENT.

Changed: SurvivalMovement.java, SurvivalHouseDebugOverlay.java,
SurvivalHousePropDebug.java (class comment only), DEV_HANDOFF.md.
Created: tiles/SurvivalPolygonOverlap.java and
core/src/test/java/SurvivalPolygonOverlapTest.java.
The prop file matches its pre-change SHA256 when only the class comment is
restored in memory: all coordinate bytes are unchanged. Both house definition
files retain identical hashes; slideAlongPolygon matches the baseline method.

Validation: core:compileJava passed; 19 standalone geometry checks passed
(containment, edge-only crossing, separation, contact, winding, concave boundary,
and approved prop center/distant checks). Run the checks with:
  javac -d core/build/overlap-check core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/tiles/SurvivalPolygonOverlap.java core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/devtools/SurvivalHousePropDebug.java core/src/test/java/SurvivalPolygonOverlapTest.java
  java -cp core/build/overlap-check SurvivalPolygonOverlapTest
git diff --check passed; git status --short run. No game launched, no commit,
protected stash untouched. Existing endpoint-based movement is not swept collision;
large frame steps can still tunnel. Toggle outside all shapes during manual tests;
no new depenetration behavior was added. Next: manually test wall/prop joins,
small gaps, F8 baseline equivalence, and F9 independence from physical blockers.

## Mapper Modes - 2026-09-26

Starting checkpoint: 3d2481397 (Add collision mapper coordinate export), clean
survival-prototype branch. The user manually verified F10 activation, zoom-correct
mouse coordinates, click suppression, point editing, closure, and terminal export
at that checkpoint. This task supersedes the earlier proposed resolver work;
CURRENT and PREVIEW physical collision were not edited.

Added HOUSE/PROP modes inside SurvivalHouseDebugOverlay with an enum and an
EnumMap of independent drafts (points plus closed state). Default mode is HOUSE.
F11 cycles HOUSE -> PROP -> HOUSE while mapper is ON, once per key press. Switching
rebuilds only the selected draft drawing; switching back restores its points and
OPEN/CLOSED state. F10 off/on retains both drafts. Drafts are session-only and
are discarded with the overlay. Adding a future mode using the same coordinate
basis requires another enum entry, not changes to the point-editing logic.

Both modes retain the existing house-local coordinate conversion. Mapper drafts
are authoring data only and never change physical collision or approved geometry.
Status: Mapper: ON | HOUSE/PROP | OPEN/CLOSED | N points; OFF also shows the mode.
Exports retain the existing float formatting and now include Mode: HOUSE or PROP.

Controls:
- F10: mapper ON/OFF (existing master gate and gameplay click suppression).
- F11: cycle mapper mode while ON; no effect while OFF.
- Left click: add to active draft; right click: undo active draft.
- Delete: clear active draft only; C: toggle closure with at least three points.
- E: export active draft with mode, count, closed state, and house-local points.
- F8/F9: unchanged collision mode and prop debug filter controls.

Changed this turn: SurvivalHouseDebugOverlay.java and DEV_HANDOFF.md only.
CellSelector, camera conversion, movement, slideAlongPolygon, footprint size,
speed, both house polygons, and all approved prop geometry are unchanged.
Validation: core:compileJava passed; all 19 existing collision overlap checks
passed; git diff --check passed; git status --short run. No existing automated
mapper tests were available. No game launched, no commit, protected stash untouched.
Manual follow-up: create distinct drafts in both modes, switch repeatedly, verify
point order and closure restoration, undo/clear isolation, mode-tagged exports,
F10 off/on retention, and zoom/click suppression plus F8/F9 regression behavior.
