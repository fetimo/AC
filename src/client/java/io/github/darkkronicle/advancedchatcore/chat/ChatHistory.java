/*
 * Copyright (C) 2021 DarkKronicle
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package io.github.darkkronicle.advancedchatcore.chat;

import io.github.darkkronicle.advancedchatcore.config.ConfigStorage;
import io.github.darkkronicle.advancedchatcore.interfaces.IChatMessageProcessor;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.Setter;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/** A utility class to maintain the storage of the chat. */
@Getter
@Environment(EnvType.CLIENT)
public class ChatHistory {

    private static final ChatHistory INSTANCE = new ChatHistory();

    /** Stored lines */
    private final List<ChatMessage> messages = new ArrayList<>();

    /** Maximum lines for storage */
    @Setter private int maxLines = 500;

    /** Runnables to run when chat history is cleared */
    private final List<Runnable> onClear = new ArrayList<>();

    /** {@link IChatMessageProcessor} for when history is updated. */
    private final List<IChatMessageProcessor> onUpdate = new ArrayList<>();

    public static ChatHistory getInstance() {
        return INSTANCE;
    }

    private ChatHistory() {}

    /**
     * Adds a runnable that will trigger when all chat messages should be cleared.
     *
     * @param runnable Runnable to run
     */
    public void addOnClear(Runnable runnable) {
        onClear.add(runnable);
    }

    /**
     * Adds a {@link IChatMessageProcessor} that gets called on new messages, added messages,
     * stacked messages, or removed messages.
     *
     * @param processor Processor ot add
     */
    public void addOnUpdate(IChatMessageProcessor processor) {
        onUpdate.add(processor);
    }

    /** Goes through and clears all message data from everywhere. */
    public void clearAll() {
        this.messages.clear();
        for (Runnable r : onClear) {
            r.run();
        }
    }

    /** Clears all the chat messages from the history */
    public void clear() {
        messages.clear();
    }

    private void sendUpdate(ChatMessage message, IChatMessageProcessor.UpdateType type) {
        for (IChatMessageProcessor consumer : onUpdate) {
            consumer.onMessageUpdate(message, type);
        }
    }

    /**
     * Adds a chat message to the history.
     *
     * @param message ChatMessage to add
     */
    public boolean add(ChatMessage message) {
        sendUpdate(message, IChatMessageProcessor.UpdateType.NEW);
        for (int i = 0;
                i < ConfigStorage.General.CHAT_STACK.config.getIntegerValue()
                        && i < messages.size();
                i++) {
            // Check for stacks
            ChatMessage chatLine = messages.get(i);
            if (message.isSimilar(chatLine)) {
                chatLine.setStacks(chatLine.getStacks() + 1);
                sendUpdate(chatLine, IChatMessageProcessor.UpdateType.STACK);
                return false;
            }
        }
        sendUpdate(message, IChatMessageProcessor.UpdateType.ADDED);
        messages.addFirst(message);
        while (this.messages.size() > maxLines) {
            sendUpdate(
                    this.messages.removeLast(),
                    IChatMessageProcessor.UpdateType.REMOVE);
        }
        return true;
    }

    /**
     * Removes a message based off of it's messageId.
     *
     * @param messageId Message ID to find and remove
     */
    public void removeMessage(int messageId) {
        List<ChatMessage> toRemove =
                this.messages.stream()
                        .filter(line -> line.getId() == messageId)
                        .toList();
        this.messages.removeAll(toRemove);
        for (ChatMessage m : toRemove) {
            sendUpdate(m, IChatMessageProcessor.UpdateType.REMOVE);
        }
    }
}
