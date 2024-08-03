package io.github.darkkronicle.advancedchathud.util;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class ScissorUtil {

    private ScissorUtil() {}

    public static void applyScissorBox(int x, int y, int width, int height) {
        GlStateManager._enableScissorTest();
        GlStateManager._scissorBox(x, y, width, height);
    }

    public static void resetScissor() {
        GlStateManager._disableScissorTest();
    }
}
