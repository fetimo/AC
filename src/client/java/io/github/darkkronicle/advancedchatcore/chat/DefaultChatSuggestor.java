/*
 * Copyright (C) 2021 DarkKronicle
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package io.github.darkkronicle.advancedchatcore.chat;

import io.github.darkkronicle.advancedchatcore.interfaces.AdvancedChatScreenSection;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ChatInputSuggestor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/** Handles the CommandSuggester for the chat */
@Environment(EnvType.CLIENT)
public class DefaultChatSuggestor extends AdvancedChatScreenSection {

    private static final Logger log = LogManager.getLogger(DefaultChatSuggestor.class);
    private ChatInputSuggestor commandSuggester;

    public DefaultChatSuggestor(AdvancedChatScreen screen) {
        super(screen);
    }

    @Override
    public void onChatFieldUpdate(String chatText, String text) {
        this.commandSuggester.setWindowActive(!text.equals(getScreen().getOriginalChatText()));
        this.commandSuggester.refresh();

        log.info("chatText: " + chatText);
        log.info("text: " + text);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        return this.commandSuggester.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float partialTicks) {
        this.commandSuggester.render(context, mouseX, mouseY);
    }

    @Override
    public void setChatFromHistory(String hist) {
        this.commandSuggester.setWindowActive(false);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        return this.commandSuggester.mouseScrolled(amount);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        return this.commandSuggester.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public void resize(int width, int height) {
        this.commandSuggester.refresh();
    }

    @Override
    public void initGui() {
        MinecraftClient client = MinecraftClient.getInstance();
        AdvancedChatScreen screen = getScreen();
        this.commandSuggester =
                new ChatInputSuggestor(
                        client,
                        screen,
                        screen.chatField,
                        client.textRenderer,
                        false,
                        false,
                        1,
                        10,
                        true,
                        -805306368);
        this.commandSuggester.refresh();
    }
}
