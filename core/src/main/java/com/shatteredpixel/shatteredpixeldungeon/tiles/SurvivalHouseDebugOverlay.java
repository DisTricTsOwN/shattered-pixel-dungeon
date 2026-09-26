package com.shatteredpixel.shatteredpixeldungeon.tiles;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.watabou.noosa.Game;
import com.shatteredpixel.shatteredpixeldungeon.devtools.SurvivalHouseCollisionTest;
import com.shatteredpixel.shatteredpixeldungeon.devtools.SurvivalHousePropDebug;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.watabou.noosa.BitmapText;
import com.watabou.noosa.ColorBlock;
import com.watabou.noosa.Group;
import com.watabou.input.PointerEvent;
import com.watabou.utils.PointF;
import java.util.Locale;
import java.util.ArrayList;
import java.util.List;
import java.util.EnumMap;

import static com.shatteredpixel.shatteredpixeldungeon.devtools.SurvivalHouseCollisionTest.PREVIEW_POLYGON;

/** Temporary debugger. F8 switches collision; F9 cycles visual-only prop inspection. */
public class SurvivalHouseDebugOverlay extends Group {
    private static SurvivalHouseDebugOverlay activeMapper;
    

    private static final int POLYGON_COLOR = 0xFFFF40FF;
    private static final int FOOTPRINT_COLOR = 0xFF00FFFF;
    private static final int PREVIEW_COLOR = 0xFF60FF40;
    private static final int PROVISIONAL_COLOR = 0xFFFFA040;
    private static final int PROP_DEBUG_COLOR = 0xFF4080FF;
    // Mirrors SurvivalMovement's current half-size; does not configure collision.
    private static final float HALF_SIZE = 6f;

    private final ColorBlock[] footprint = new ColorBlock[4];
    private final ColorBlock center;
    private final BitmapText localXLabel;
    private final BitmapText localYLabel;
    private final BitmapText collisionLabel;
    private final BitmapText propStatusLabel;
    private final Group[] propGroups = new Group[SurvivalHousePropDebug.FOOTPRINTS.length];
    // 0 = ALL; 1..4 follow the BARREL, AC, TANK, BOARDS definition order.
    private int propSelection = 0;
    private boolean propCycleHeld;
    private boolean toggleHeld;
    private final ColorBlock positionBackground;
// Interactive collision mapper. F10 toggles it; F11 cycles independent drafts.
// Both initial modes use house-local coordinates. Drafts never affect collision.
private enum MapperMode { HOUSE, PROP }
private static final class MapperDraft {
    final List<PointF> points = new ArrayList<>();
    boolean closed;
}
private final EnumMap<MapperMode, MapperDraft> mapperDrafts = new EnumMap<>(MapperMode.class);
private MapperMode mapperMode = MapperMode.HOUSE;
private boolean mapperModeHeld;
private MapperDraft mapperDraft() {
    MapperDraft draft = mapperDrafts.get(mapperMode);
    if (draft == null) {
        draft = new MapperDraft();
        mapperDrafts.put(mapperMode, draft);
    }
    return draft;
}
private boolean mapperEnabled;
private boolean mapperToggleHeld;
private boolean mapperClearHeld;
private boolean mapperCloseHeld;
private boolean mapperExportHeld;
private final ColorBlock mapperCrosshairHorizontal;
private final ColorBlock mapperCrosshairVertical;
private final BitmapText mapperStatusLabel;
private Group mapperDrawing = new Group();
private final ColorBlock mapperNextEdge = new ColorBlock(1, 1, 0xFF80FF80);
private int selectedVertex = -1;
private PointF editOriginal;
private PointerEvent editGesture;
private PointerEvent cancelEditClick;
private boolean rawEditPressed;
private boolean cancelEditRequested;
private boolean cancelRightCaptured;
// PointerEvent coalesces buttons sharing a pointer ID. Consume edit cancellation
// before that layer can mistake RIGHT up for the active LEFT gesture's release.
private final InputAdapter editCancelInput = new InputAdapter() {
    @Override
    public boolean touchDown(int x, int y, int pointer, int button) {
        if (mapperEnabled && button == Input.Buttons.LEFT
                && (Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)
                || Gdx.input.isKeyPressed(Input.Keys.SHIFT_RIGHT))) rawEditPressed = true;
        if (mapperEnabled && button == Input.Buttons.RIGHT
                && (rawEditPressed || editGesture != null)) {
            cancelRightCaptured = true;
            cancelEditRequested = true;
            return true;
        }
        return false;
    }

    @Override
    public boolean touchUp(int x, int y, int pointer, int button) {
        if (button == Input.Buttons.LEFT) rawEditPressed = false;
        if (button == Input.Buttons.RIGHT && cancelRightCaptured) {
            cancelRightCaptured = false;
            return true;
        }
        return false;
    }

    @Override
    public boolean touchCancelled(int x, int y, int pointer, int button) {
        return touchUp(x, y, pointer, button);
    }
};
private final Group helpPanel = new Group();
private final ColorBlock helpBackground = new ColorBlock(1, 1, 0xEE000000);
private final List<BitmapText> helpLines = new ArrayList<>();
private boolean helpHeld;

    public SurvivalHouseDebugOverlay() {
        activeMapper = this;
        Game.inputHandler.addInputProcessor(editCancelInput);
        SurvivalHouseCollisionTest.reset();
        toggleHeld = Gdx.input.isKeyPressed(Input.Keys.F8);
        propCycleHeld = Gdx.input.isKeyPressed(Input.Keys.F9);
        float originX = SurvivalHouse.HOUSE_X * SurvivalVisuals.CELL_SIZE;
        float originY = SurvivalHouse.HOUSE_Y * SurvivalVisuals.CELL_SIZE;
        float[][] polygon = SurvivalHouse.COLLISION_POLYGON;

        for (int i = 0; i < polygon.length; i++) {
            float[] a = polygon[i];
            float[] b = polygon[(i + 1) % polygon.length];
            ColorBlock edge = new ColorBlock(1, 1, POLYGON_COLOR);
            positionLine(edge, originX + a[0], originY + a[1],
                    originX + b[0], originY + b[1]);
            add(edge);
        }

        for (int i = 0; i < polygon.length; i++) {
            float x = originX + polygon[i][0];
            float y = originY + polygon[i][1];
            ColorBlock marker = new ColorBlock(3, 3, 0xFFFFFFFF);
            marker.x = x - 1.5f;
            marker.y = y - 1.5f;
            add(marker);

            BitmapText label = new BitmapText(Integer.toString(i), PixelScene.pixelFont);
            label.measure();
            label.x = x + 4;
            label.y = y - label.height() - 2;
            ColorBlock background = new ColorBlock(label.width() + 2, label.height() + 2,
                    0xCC000000);
            background.x = label.x - 1;
            background.y = label.y - 1;
            add(background);
            add(label);
        }

        addPreview(originX, originY);
        addPropProposals(originX, originY);

        for (int i = 0; i < footprint.length; i++) {
            footprint[i] = new ColorBlock(1, 1, FOOTPRINT_COLOR);
            add(footprint[i]);
        }
        center = new ColorBlock(3, 3, 0xFFFFFF00);
        add(center);

        positionBackground = new ColorBlock(1, 1, 0xDD000000);
        add(positionBackground);
        localXLabel = new BitmapText(PixelScene.pixelFont);
        localYLabel = new BitmapText(PixelScene.pixelFont);
        add(localXLabel);
        add(localYLabel);
        collisionLabel = new BitmapText(PixelScene.pixelFont);
        add(collisionLabel);
        propStatusLabel = new BitmapText(PixelScene.pixelFont);
        propStatusLabel.hardlight(PROP_DEBUG_COLOR & 0xFFFFFF);
        add(propStatusLabel);
        mapperToggleHeld = Gdx.input.isKeyPressed(Input.Keys.F10);
        mapperModeHeld = Gdx.input.isKeyPressed(Input.Keys.F11);

mapperCrosshairHorizontal = new ColorBlock(17, 1, 0xFFFFFF00);
mapperCrosshairVertical = new ColorBlock(1, 17, 0xFFFFFF00);
mapperCrosshairHorizontal.visible = false;
mapperCrosshairVertical.visible = false;
add(mapperCrosshairHorizontal);
add(mapperCrosshairVertical);

mapperStatusLabel = new BitmapText(PixelScene.pixelFont);
mapperStatusLabel.hardlight(0xFFFF00);
mapperStatusLabel.text("Mapper: OFF");
mapperStatusLabel.measure();
add(mapperStatusLabel);
add(mapperDrawing);
mapperNextEdge.visible = false;
mapperNextEdge.alpha(0.55f);
add(mapperNextEdge);
helpHeld = Gdx.input.isKeyPressed(Input.Keys.F7);
buildHelpPanel();
    }

    private void buildHelpPanel() {
        helpPanel.camera = PixelScene.uiCamera;
        helpPanel.visible = false;
        helpPanel.add(helpBackground);
        String[] lines = {
                "COLLISION DEBUG  [F7 hide/show]",
                "F8  Current / Preview", "F9  Prop Debug Filter",
                "F10 Mapper On / Off", "F11 Mode: House / Prop",
                "MAPPER", "Left Click: Add Point", "Right Click: Undo Last Point",
                "Shift + Left Drag: Move Vertex", "Release: Commit Move",
                "Right Click during edit: Cancel", "Delete: Clear Active Draft",
                "C: Open / Close Polygon", "E: Export Coordinates",
                "HOUSE / PROP: separate drafts", "Coordinates: house-local"
        };
        for (String text : lines) {
            BitmapText label = new BitmapText(text, PixelScene.pixelFont);
            label.measure();
            helpLines.add(label);
            helpPanel.add(label);
        }
        add(helpPanel);
    }

    private void layoutHelpPanel() {
        float width = 0, height = 0;
        for (BitmapText line : helpLines) {
            width = Math.max(width, line.width() / line.scale.x);
            height += line.height() / line.scale.y + 2;
        }
        float scale = Math.min(1f, Math.min((PixelScene.uiCamera.width - 16f) / (width + 8),
                (PixelScene.uiCamera.height - 16f) / (height + 8)));
        com.watabou.utils.Point playerScreen = camera().cameraToScreen(SurvivalMovement.x(), SurvivalMovement.y());
        PointF playerUI = PixelScene.uiCamera.screenToCamera(playerScreen.x, playerScreen.y);
        float panelX = playerUI.x < PixelScene.uiCamera.width / 2f
                ? Math.max(4, PixelScene.uiCamera.width - width * scale - 12) : 4;
        float y = 8;
        for (BitmapText line : helpLines) {
            line.scale.set(scale);
            line.x = panelX + 4;
            line.y = y;
            y += line.height() + 2 * scale;
        }
        helpBackground.x = panelX;
        helpBackground.y = 4;
        helpBackground.size(width * scale + 8, y - 4 + 4);
    }

    private void addPropProposals(float originX, float originY) {
        for (int propIndex = 0; propIndex < propGroups.length; propIndex++) {
            SurvivalHousePropDebug.Footprint prop = SurvivalHousePropDebug.FOOTPRINTS[propIndex];
            Group group = propGroups[propIndex] = new Group();
            add(group);
            float cx = 0, cy = 0;
            for (float[] vertex : prop.vertices) {
                cx += vertex[0] / prop.vertices.length;
                cy += vertex[1] / prop.vertices.length;
            }
            for (int i = 0; i < prop.vertices.length; i++) {
                float[] a = prop.vertices[i];
                float[] b = prop.vertices[(i + 1) % prop.vertices.length];
                ColorBlock edge = new ColorBlock(1, 1, PROP_DEBUG_COLOR);
                positionLine(edge, originX + a[0], originY + a[1],
                        originX + b[0], originY + b[1]);
                group.add(edge);
                ColorBlock vertex = new ColorBlock(2, 2, PROP_DEBUG_COLOR);
                vertex.x = originX + a[0] - 1;
                vertex.y = originY + a[1] - 1;
                group.add(vertex);
                // Push numbers radially outside these small ground footprints.
                float dx = a[0] - cx, dy = a[1] - cy;
                float length = (float) Math.sqrt(dx * dx + dy * dy);
                addPropLabel(group, Integer.toString(i), originX + a[0] + dx / length * 6 - 2,
                        originY + a[1] + dy / length * 6 - 3);
            }
            // Compact local name; no distant leaders or repeated disclaimer text.
            addPropLabel(group, prop.name, originX + cx + 16, originY + cy + 7);
        }
    }

    private void addPropLabel(Group group, String text, float x, float y) {
        BitmapText label = new BitmapText(text, PixelScene.pixelFont);
        label.hardlight(PROP_DEBUG_COLOR & 0xFFFFFF);
        label.measure();
        label.x = x;
        label.y = y;
        ColorBlock background = new ColorBlock(label.width() + 2, label.height() + 2,
                0xDD000000);
        background.x = x - 1;
        background.y = y - 1;
        group.add(background);
        group.add(label);
    }

    private void addPreview(float originX, float originY) {
        for (int i = 0; i < PREVIEW_POLYGON.length; i++) {
            float[] a = PREVIEW_POLYGON[i];
            float[] b = PREVIEW_POLYGON[(i + 1) % PREVIEW_POLYGON.length];
            float ax = originX + a[0];
            float ay = originY + a[1];
            float dx = b[0] - a[0];
            float dy = b[1] - a[1];
            float length = (float) Math.sqrt(dx * dx + dy * dy);
            boolean provisional = i == 7 || i == 8;
            int color = provisional ? PROVISIONAL_COLOR : PREVIEW_COLOR;
            // Rear P7-P8-P0 edges use four-pixel dashes with four-pixel gaps.
            float step = provisional ? 8f : length;
            for (float distance = 0; distance < length; distance += step) {
                float end = Math.min(distance + (provisional ? 4f : length), length);
                ColorBlock edge = new ColorBlock(1, 1, color);
                positionLine(edge, ax + dx * distance / length, ay + dy * distance / length,
                        ax + dx * end / length, ay + dy * end / length);
                add(edge);
            }
        }

        for (int i = 0; i < PREVIEW_POLYGON.length; i++) {
            float x = originX + PREVIEW_POLYGON[i][0];
            float y = originY + PREVIEW_POLYGON[i][1];
            int color = i == 8 ? PROVISIONAL_COLOR : PREVIEW_COLOR;
            ColorBlock marker = new ColorBlock(3, 3, color);
            marker.x = x - 1.5f;
            marker.y = y - 1.5f;
            add(marker);

            BitmapText label = new BitmapText(i == 8 ? "P8 provisional" : "P" + i,
                    PixelScene.pixelFont);
            label.hardlight(color & 0xFFFFFF);
            label.measure();
            // Below vertices, to distinguish from the current polygon's labels above.
            label.x = x + 4;
            label.y = y + 4;
            ColorBlock background = new ColorBlock(label.width() + 2, label.height() + 2,
                    0xCC000000);
            background.x = label.x - 1;
            background.y = label.y - 1;
            add(background);
            add(label);
        }
    }

    @Override
    public void update() {
        super.update();
        applyRequestedEditCancel();
        boolean helpPressed = Gdx.input.isKeyPressed(Input.Keys.F7);
        if (helpPressed && !helpHeld) helpPanel.visible = !helpPanel.visible;
        helpHeld = helpPressed;
        // Rising edge only: holding F8 must not repeatedly switch collision.
        boolean held = Gdx.input.isKeyPressed(Input.Keys.F8);
        if (held && !toggleHeld) {
            SurvivalHouseCollisionTest.toggle();
        }
        toggleHeld = held;
        boolean propHeld = Gdx.input.isKeyPressed(Input.Keys.F9);
        if (propHeld && !propCycleHeld) {
            propSelection = (propSelection + 1) % (propGroups.length + 1);
            for (int i = 0; i < propGroups.length; i++) {
                propGroups[i].visible = propSelection == 0 || propSelection == i + 1;
            }
        }
        propCycleHeld = propHeld;
        boolean mapperHeld = Gdx.input.isKeyPressed(Input.Keys.F10);
if (mapperHeld && !mapperToggleHeld) {
    cancelMapperEdit();
    mapperEnabled = !mapperEnabled;

    mapperCrosshairHorizontal.visible = mapperEnabled;
    mapperCrosshairVertical.visible = mapperEnabled;

    mapperStatusLabel.text(mapperEnabled ? "Mapper: ON" : "Mapper: OFF | " + mapperMode);
    mapperStatusLabel.measure();
}
mapperToggleHeld = mapperHeld;
boolean modeHeld = Gdx.input.isKeyPressed(Input.Keys.F11);
if (mapperEnabled && modeHeld && !mapperModeHeld) {
    cancelMapperEdit();
    MapperMode[] modes = MapperMode.values();
    mapperMode = modes[(mapperMode.ordinal() + 1) % modes.length];
    rebuildMapperDrawing();
}
mapperModeHeld = modeHeld;
boolean clearHeld = Gdx.input.isKeyPressed(Input.Keys.FORWARD_DEL);
if (mapperEnabled && clearHeld && !mapperClearHeld) {
    cancelMapperEdit();
    mapperDraft().points.clear();
    mapperDraft().closed = false;
    rebuildMapperDrawing();

    mapperStatusLabel.text("Mapper: ON | No points");
    mapperStatusLabel.measure();
}
mapperClearHeld = clearHeld;
boolean closeHeld = Gdx.input.isKeyPressed(Input.Keys.C);
if (mapperEnabled && closeHeld && !mapperCloseHeld) {
    cancelMapperEdit();
    if (mapperDraft().points.size() >= 3) {
        mapperDraft().closed = !mapperDraft().closed;
        rebuildMapperDrawing();

        mapperStatusLabel.text(
                mapperDraft().closed
                        ? "Mapper: ON | CLOSED"
                        : "Mapper: ON | OPEN"
        );
        mapperStatusLabel.measure();
    }
}
mapperCloseHeld = closeHeld;
boolean exportHeld = Gdx.input.isKeyPressed(Input.Keys.E);
if (mapperEnabled && exportHeld && !mapperExportHeld) {
    cancelMapperEdit();
    exportMapperPoints();
}
mapperExportHeld = exportHeld;
    }
public static boolean mapperConsumesClicks() {
    return activeMapper != null && activeMapper.mapperEnabled;
}
public static void mapperPointerDown(PointerEvent event) {
    if (!mapperConsumesClicks()) return;
    SurvivalHouseDebugOverlay mapper = activeMapper;
    if (mapper.editGesture != null) {
        if (event.button == PointerEvent.RIGHT) {
            mapper.cancelEditClick = event;
            mapper.cancelMapperEdit();
        }
        return;
    }
    if (event.button != PointerEvent.LEFT || !(Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)
            || Gdx.input.isKeyPressed(Input.Keys.SHIFT_RIGHT))) return;
    mapper.editGesture = event; // Even a missed Shift-click must never add a point.
    PointF local = mapper.mapperLocal(event.current);
    float radius = 10f / mapper.camera().zoom;
    float best = radius * radius;
    for (int i = 0; i < mapper.mapperDraft().points.size(); i++) {
        PointF p = mapper.mapperDraft().points.get(i);
        float dx = p.x - local.x, dy = p.y - local.y;
        float distance = dx * dx + dy * dy;
        if (distance <= best) {
            best = distance;
            mapper.selectedVertex = i;
        }
    }
    if (mapper.selectedVertex >= 0) {
        mapper.editOriginal = new PointF(mapper.mapperDraft().points.get(mapper.selectedVertex));
        mapper.rebuildMapperDrawing();
    }
}

private PointF mapperLocal(PointF screen) {
    PointF world = camera().screenToCamera((int) screen.x, (int) screen.y);
    return new PointF(world.x - SurvivalHouse.HOUSE_X * SurvivalVisuals.CELL_SIZE,
            world.y - SurvivalHouse.HOUSE_Y * SurvivalVisuals.CELL_SIZE);
}

public static void mapperPointerDrag(PointerEvent event) {
    if (activeMapper != null) activeMapper.applyRequestedEditCancel();
    if (!mapperConsumesClicks() || activeMapper.editGesture != event
            || activeMapper.selectedVertex < 0) return;
    PointF local = activeMapper.mapperLocal(event.current);
    activeMapper.mapperDraft().points.get(activeMapper.selectedVertex).set(local);
    activeMapper.rebuildMapperDrawing();
}

public static void mapperPointerUp(PointerEvent event) {
    if (activeMapper == null) return;
    SurvivalHouseDebugOverlay mapper = activeMapper;
    mapper.applyRequestedEditCancel();
    if (event == mapper.cancelEditClick) {
        mapper.cancelEditClick = null;
        return;
    }
    if (mapper.editGesture != null) {
        if (mapper.editGesture != event) return;
        if (event.type == PointerEvent.Type.CANCEL) mapper.cancelMapperEdit();
        else mapperPointerDrag(event);
        mapper.selectedVertex = -1;
        mapper.editOriginal = null;
        mapper.editGesture = null;
        mapper.rebuildMapperDrawing();
    } else if (event.type != PointerEvent.Type.CANCEL) {
        handleMapperClick(event);
    }
}

private void applyRequestedEditCancel() {
    if (cancelEditRequested) {
        cancelMapperEdit();
        cancelEditRequested = false;
    }
}

private void cancelMapperEdit() {
    if (selectedVertex >= 0 && editOriginal != null) {
        mapperDraft().points.get(selectedVertex).set(editOriginal);
    }
    selectedVertex = -1;
    editOriginal = null;
    // Keep the gesture until release, suppressing accidental placement after cancellation.
    rebuildMapperDrawing();
}
public static boolean handleMapperClick(PointerEvent event) {
    if (activeMapper == null || !activeMapper.mapperEnabled) {
        return false;
    }
    activeMapper.applyRequestedEditCancel();
    if (activeMapper.editGesture != null) {
        if (event.button == PointerEvent.RIGHT) activeMapper.cancelMapperEdit();
        return true;
    }

    if (event.button == PointerEvent.RIGHT) {
    activeMapper.undoMapperPoint();
    return true;
}

if (event.button != PointerEvent.LEFT) {
    return true;
}

    PointF worldPos = activeMapper.camera().screenToCamera(
            (int) event.current.x,
            (int) event.current.y
    );

    float originX = SurvivalHouse.HOUSE_X * SurvivalVisuals.CELL_SIZE;
    float originY = SurvivalHouse.HOUSE_Y * SurvivalVisuals.CELL_SIZE;

    float localX = worldPos.x - originX;
    float localY = worldPos.y - originY;

    activeMapper.addMapperPoint(localX, localY);
    return true;
}

private void addMapperPoint(float localX, float localY) {
    if (mapperDraft().closed) {
        mapperDraft().closed = false;
        rebuildMapperDrawing();
    }
    mapperDraft().closed = false;

    PointF point = new PointF(localX, localY);
    mapperDraft().points.add(point);

    float originX = SurvivalHouse.HOUSE_X * SurvivalVisuals.CELL_SIZE;
    float originY = SurvivalHouse.HOUSE_Y * SurvivalVisuals.CELL_SIZE;

    float worldX = originX + localX;
    float worldY = originY + localY;

    ColorBlock marker = new ColorBlock(5, 5, 0xFFFFFF00);
    marker.x = worldX - 2.5f;
    marker.y = worldY - 2.5f;
    mapperDrawing.add(marker);

    int index = mapperDraft().points.size() - 1;

    BitmapText label = new BitmapText("P" + index, PixelScene.pixelFont);
    label.hardlight(0xFFFF00);
    label.measure();
    label.x = worldX + 5;
    label.y = worldY + 5;
    mapperDrawing.add(label);

    if (mapperDraft().points.size() > 1) {
        PointF previous = mapperDraft().points.get(mapperDraft().points.size() - 2);

        ColorBlock edge = new ColorBlock(1, 1, 0xFFFFFF00);
        positionLine(
                edge,
                originX + previous.x,
                originY + previous.y,
                worldX,
                worldY
        );
        mapperDrawing.add(edge);
    }

    mapperStatusLabel.text(String.format(
            Locale.ROOT,
            "Mapper: ON | P%d (%.1f, %.1f)",
            index,
            localX,
            localY
    ));
    mapperStatusLabel.measure();
}
private void exportMapperPoints() {
    System.out.println();
    System.out.println("=== COLLISION MAPPER EXPORT ===");
    System.out.println("Mode: " + mapperMode);
    System.out.println("Points: " + mapperDraft().points.size());
    System.out.println("Closed: " + mapperDraft().closed);
    System.out.println("{");

    for (PointF point : mapperDraft().points) {
        System.out.println(String.format(
                Locale.ROOT,
                "    {%.1ff, %.1ff},",
                point.x,
                point.y
        ));
    }

    System.out.println("}");
    System.out.println("=== END EXPORT ===");
    System.out.println();

    mapperStatusLabel.text(
            "Mapper: ON | EXPORTED | " + mapperDraft().points.size() + " points"
    );
    mapperStatusLabel.measure();
}

private void undoMapperPoint() {
    if (mapperDraft().points.isEmpty()) {
        return;
    }

    mapperDraft().points.remove(mapperDraft().points.size() - 1);
    rebuildMapperDrawing();

    if (mapperDraft().points.isEmpty()) {
        mapperStatusLabel.text("Mapper: ON | No points");
    } else {
        PointF last = mapperDraft().points.get(mapperDraft().points.size() - 1);
        mapperStatusLabel.text(String.format(
                Locale.ROOT,
                "Mapper: ON | P%d (%.1f, %.1f)",
                mapperDraft().points.size() - 1,
                last.x,
                last.y
        ));
    }

    mapperStatusLabel.measure();
}
private void rebuildMapperDrawing() {
    Group previousDrawing = mapperDrawing;
    mapperDrawing = new Group();
    replace(previousDrawing, mapperDrawing);
    previousDrawing.destroy();

    float originX = SurvivalHouse.HOUSE_X * SurvivalVisuals.CELL_SIZE;
    float originY = SurvivalHouse.HOUSE_Y * SurvivalVisuals.CELL_SIZE;

    for (int i = 0; i < mapperDraft().points.size(); i++) {
        PointF point = mapperDraft().points.get(i);

        float worldX = originX + point.x;
        float worldY = originY + point.y;

        boolean selected = i == selectedVertex;
        ColorBlock marker = new ColorBlock(selected ? 7 : 5, selected ? 7 : 5,
                selected ? 0xFF00FFFF : 0xFFFFFF00);
        marker.x = worldX - (selected ? 3.5f : 2.5f);
        marker.y = worldY - (selected ? 3.5f : 2.5f);
        mapperDrawing.add(marker);

        BitmapText label = new BitmapText("P" + i, PixelScene.pixelFont);
        label.hardlight(0xFFFF00);
        label.measure();
        label.x = worldX + 5;
        label.y = worldY + 5;
        mapperDrawing.add(label);

        if (i > 0) {
            PointF previous = mapperDraft().points.get(i - 1);

            ColorBlock edge = new ColorBlock(1, 1, 0xFFFFFF00);
            positionLine(
                    edge,
                    originX + previous.x,
                    originY + previous.y,
                    worldX,
                    worldY
            );
            mapperDrawing.add(edge);
        }
    }
    if (mapperDraft().closed && mapperDraft().points.size() >= 3) {
    PointF first = mapperDraft().points.get(0);
    PointF last = mapperDraft().points.get(mapperDraft().points.size() - 1);

    ColorBlock closingEdge = new ColorBlock(1, 1, 0xFFFFFF00);
    positionLine(
            closingEdge,
            originX + last.x,
            originY + last.y,
            originX + first.x,
            originY + first.y
    );
    mapperDrawing.add(closingEdge);
}
}
    @Override
public void destroy() {
    Game.inputHandler.removeInputProcessor(editCancelInput);
    if (activeMapper == this) {
        activeMapper = null;
    }
    SurvivalHouseCollisionTest.reset();
    super.destroy();
}

    @Override
    public void draw() {
        mapperNextEdge.visible = false;
        if (helpPanel.visible) layoutHelpPanel();
        if (mapperEnabled) {
    PointF mousePos = PointerEvent.currentHoverPos();

    if (mousePos != null) {
        PointF worldPos = camera().screenToCamera(
                (int) mousePos.x,
                (int) mousePos.y
        );

        mapperCrosshairHorizontal.x = worldPos.x - 8;
        mapperCrosshairHorizontal.y = worldPos.y;
        mapperCrosshairVertical.x = worldPos.x;
        mapperCrosshairVertical.y = worldPos.y - 8;
        if (!mapperDraft().closed && !mapperDraft().points.isEmpty() && editGesture == null) {
            PointF last = mapperDraft().points.get(mapperDraft().points.size() - 1);
            positionLine(mapperNextEdge,
                    SurvivalHouse.HOUSE_X * SurvivalVisuals.CELL_SIZE + last.x,
                    SurvivalHouse.HOUSE_Y * SurvivalVisuals.CELL_SIZE + last.y,
                    worldPos.x, worldPos.y);
            mapperNextEdge.visible = true;
        }
    }
}
        // Read after scene updates so the outline uses this frame's resolved position.
        float x = SurvivalMovement.x();
        float y = SurvivalMovement.y();
        float left = x - HALF_SIZE;
        float right = x + HALF_SIZE;
        float top = y - HALF_SIZE;
        float bottom = y + HALF_SIZE;
        positionLine(footprint[0], left, top, right, top);
        positionLine(footprint[1], right, top, right, bottom);
        positionLine(footprint[2], right, bottom, left, bottom);
        positionLine(footprint[3], left, bottom, left, top);
        center.x = x - 1.5f;
        center.y = y - 1.5f;
        float localX = x - SurvivalHouse.HOUSE_X * SurvivalVisuals.CELL_SIZE;
        float localY = y - SurvivalHouse.HOUSE_Y * SurvivalVisuals.CELL_SIZE;
        updatePositionLabel(localXLabel, String.format(Locale.ROOT, "Local X: %.1f", localX));
        updatePositionLabel(localYLabel, String.format(Locale.ROOT, "Local Y: %.1f", localY));
        updatePositionLabel(collisionLabel, SurvivalHouseCollisionTest.isPreview()
                ? "Collision: PREVIEW" : "Collision: CURRENT");
        String propName = propSelection == 0 ? "ALL"
                : SurvivalHousePropDebug.FOOTPRINTS[propSelection - 1].name;
        updatePositionLabel(propStatusLabel, "Prop Debug: " + propName
                + (SurvivalHouseCollisionTest.isPreview() ? " (ALL PHYSICAL)" : " (VISUAL ONLY)"));
                updatePositionLabel(
        mapperStatusLabel,
        mapperEnabled
                ? "Mapper: ON | " + mapperMode + " | " + (mapperDraft().closed ? "CLOSED" : "OPEN")
                    + " | " + mapperDraft().points.size() + " points"
                : "Mapper: OFF | " + mapperMode + " | F7 Help"
);
        // Offset beyond the footprint so the center and contact outline remain visible.
        localXLabel.x = localYLabel.x = right + 6;
        localXLabel.y = bottom + 6;
        localYLabel.y = localXLabel.y + localXLabel.height() + 2;
        collisionLabel.x = localXLabel.x;
        collisionLabel.y = localYLabel.y + localYLabel.height() + 2;
        propStatusLabel.x = localXLabel.x;
        propStatusLabel.y = collisionLabel.y + collisionLabel.height() + 2;
        mapperStatusLabel.x = localXLabel.x;
        mapperStatusLabel.y = propStatusLabel.y + propStatusLabel.height() + 2;
        positionBackground.x = localXLabel.x - 2;
        positionBackground.y = localXLabel.y - 2;
      positionBackground.size(
        Math.max(
                Math.max(Math.max(collisionLabel.width(), propStatusLabel.width()),
                        Math.max(localXLabel.width(), localYLabel.width())),
                mapperStatusLabel.width()
        ) + 4,
        localXLabel.height()
                + localYLabel.height()
                + collisionLabel.height()
                + propStatusLabel.height()
                + mapperStatusLabel.height()
                + 12
);
        super.draw();
    }

    private static void updatePositionLabel(BitmapText label, String text) {
        if (!text.equals(label.text())) {
            label.text(text);
            label.measure();
        }
    }

    private static void positionLine(ColorBlock line, float ax, float ay, float bx, float by) {
        float dx = bx - ax;
        float dy = by - ay;
        float length = (float) Math.sqrt(dx * dx + dy * dy);
        line.size(length, 1);
        line.angle = (float) Math.toDegrees(Math.atan2(dy, dx));
        // Center the one-pixel stroke on the actual geometric segment.
        line.x = ax + (length == 0 ? 0 : dy / length * 0.5f);
        line.y = ay - (length == 0 ? 0 : dx / length * 0.5f);
    }
}
