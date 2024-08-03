/*
 * Copyright (C) 2021-2022 DarkKronicle
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package io.github.darkkronicle.advancedchatfilters.filters.processors;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.text2speech.Narrator;
import fi.dy.masa.malilib.config.IConfigBase;
import fi.dy.masa.malilib.config.options.ConfigString;
import fi.dy.masa.malilib.gui.GuiBase;
import fi.dy.masa.malilib.gui.GuiTextFieldGeneric;
import fi.dy.masa.malilib.gui.button.ButtonBase;
import fi.dy.masa.malilib.gui.button.ButtonGeneric;
import fi.dy.masa.malilib.gui.button.IButtonActionListener;
import fi.dy.masa.malilib.util.StringUtils;
import io.github.darkkronicle.Konstruct.functions.Function;
import io.github.darkkronicle.Konstruct.functions.NamedFunction;
import io.github.darkkronicle.Konstruct.nodes.Node;
import io.github.darkkronicle.Konstruct.parser.IntRange;
import io.github.darkkronicle.Konstruct.parser.ParseContext;
import io.github.darkkronicle.Konstruct.type.NullObject;
import io.github.darkkronicle.advancedchatcore.config.SaveableConfig;
import io.github.darkkronicle.advancedchatcore.config.gui.widgets.WidgetLabelHoverable;
import io.github.darkkronicle.advancedchatcore.interfaces.IJsonApplier;
import io.github.darkkronicle.advancedchatcore.interfaces.IMatchProcessor;
import io.github.darkkronicle.advancedchatcore.interfaces.IScreenSupplier;
import io.github.darkkronicle.advancedchatcore.util.Colors;
import io.github.darkkronicle.advancedchatcore.util.SearchResult;

import java.util.List;
import java.util.function.Supplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
public class NarratorProcessor implements IMatchProcessor, IScreenSupplier, IJsonApplier {

    public static class NarratorFunction implements NamedFunction {

        @Override
        public String getName() {
            return "toNarrator";
        }

        @Override
        public io.github.darkkronicle.Konstruct.parser.Result parse(ParseContext context, List<Node> input) {
            io.github.darkkronicle.Konstruct.parser.Result r1 = Function.parseArgument(context, input, 0);
            Narrator.getNarrator().say(r1.getContent().getString(), false);
            return io.github.darkkronicle.Konstruct.parser.Result.success(new NullObject());
        }

        @Override
        public IntRange getArgumentCount() {
            return IntRange.of(1);
        }
    }

    private static String translate(String key) {
        return "advancedchatfilters.config.processor.narrator." + key;
    }

    private final SaveableConfig<ConfigString> message =
            SaveableConfig.fromConfig(
                    "message",
                    new ConfigString(translate("message"), "$1", translate("info.message")));

    @Override
    public Result processMatches(Text text, Text unfiltered, SearchResult search) {
        String content = search.getGroupReplacements(message.config.getStringValue(), 0);
        Narrator.getNarrator().say(content, false);
        return Result.getFromBool(true);
    }

    @Override
    public JsonObject save() {
        JsonObject obj = new JsonObject();
        obj.add(message.key, message.config.getAsJsonElement());
        return obj;
    }

    @Override
    public void load(JsonElement element) {
        if (element == null || !element.isJsonObject()) {
            return;
        }
        JsonObject obj = element.getAsJsonObject();
        message.config.setValueFromJsonElement(obj.get(message.key));
    }

    @Override
    public Supplier<Screen> getScreen(Screen parent) {
        return () -> new SenderScreen(parent);
    }

    public class SenderScreen extends GuiBase {

        private GuiTextFieldGeneric textField;

        @Override
        public void close() {
            save();
            super.close();
        }

        public SenderScreen(Screen parent) {
            this.setParent(parent);
            this.setTitle(StringUtils.translate("advancedchatfilters.screen.narrator"));
        }

        @Override
        protected void closeGui(boolean showParent) {
            save();
            super.closeGui(showParent);
        }

        public void save() {
            message.config.setValueFromString(textField.getText());
        }

        private int getWidth() {
            return 300;
        }

        @Override
        public void initGui() {
            super.initGui();
            int x = 10;
            int y = 26;

            String name = SoundProcessor.ButtonListener.Type.BACK.getDisplayName();
            int nameW = StringUtils.getStringWidth(name) + 10;
            ButtonGeneric button = new ButtonGeneric(x, y, nameW, 20, name);
            this.addButton(button, new ButtonListener(ButtonListener.Type.BACK, this));
            y += 30;
            y += this.addLabel(x, y, message.config) + 1;
            textField =
                    new GuiTextFieldGeneric(
                            x, y, getWidth(), 20, MinecraftClient.getInstance().textRenderer);
            textField.setMaxLength(64000);
            textField.setText(message.config.getStringValue());
            this.addTextField(textField, null);
        }

        private int addLabel(int x, int y, IConfigBase config) {
            int width = StringUtils.getStringWidth(config.getConfigGuiDisplayName());
            WidgetLabelHoverable label =
                    new WidgetLabelHoverable(
                            x,
                            y,
                            width,
                            8,
                            Colors.getInstance().getColorOrWhite("white").color(),
                            config.getConfigGuiDisplayName());
            label.setHoverLines(StringUtils.translate(config.getComment()));
            this.addWidget(label);
            return 8;
        }

        public void back() {
            this.closeGui(true);
        }
    }

    public static class ButtonListener implements IButtonActionListener {

        private final SenderScreen parent;
        private final Type type;

        public ButtonListener(Type type, SenderScreen parent) {
            this.type = type;
            this.parent = parent;
        }

        @Override
        public void actionPerformedWithButton(ButtonBase button, int mouseButton) {
            if (this.type == Type.BACK) {
                parent.back();
            }
        }

        public enum Type {
            BACK("back");

            private final String translation;

            private static String translate(String key) {
                return "advancedchatfilters.gui.button." + key;
            }

            Type(String key) {
                this.translation = translate(key);
            }

            public String getDisplayName() {
                return StringUtils.translate(translation);
            }
        }
    }
}
