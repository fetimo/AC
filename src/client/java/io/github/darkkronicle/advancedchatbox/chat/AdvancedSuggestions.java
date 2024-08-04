/*
 * Copyright (C) 2021 DarkKronicle
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package io.github.darkkronicle.advancedchatbox.chat;

import com.mojang.brigadier.context.StringRange;
import com.mojang.brigadier.suggestion.Suggestion;
import com.mojang.brigadier.suggestion.Suggestions;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import lombok.Getter;

/**
 * A holder of {@link AdvancedSuggestion}
 *
 * <p>
 * Maintains the start/stop range and suggestions in that range
 */
@Getter
public class AdvancedSuggestions {
    private final List<AdvancedSuggestion> suggestions;

    private StringRange range;

    /** Empty suggestions */
    public static final AdvancedSuggestions EMPTY = new AdvancedSuggestions(StringRange.at(0), new ArrayList<>());

    /** Future of EMPTY */
    public static CompletableFuture<AdvancedSuggestions> empty() {
        return CompletableFuture.completedFuture(EMPTY);
    }

    public AdvancedSuggestions(StringRange range, List<AdvancedSuggestion> suggestions) {
        this.suggestions = suggestions;
        suggestions.sort(AdvancedSuggestion::compareToIgnoreCase);
        if (range != null) {
            this.range = range;
        } else {
            setRange();
        }
    }

    private void setRange() {
        int start = Integer.MAX_VALUE;
        int end = Integer.MIN_VALUE;
        for (AdvancedSuggestion s : suggestions) {
            start = Math.min(s.getRange().getStart(), start);
            end = Math.max(s.getRange().getEnd(), end);
        }
        this.range = new StringRange(start, end);
    }

    /**
     * Converts {@link Suggestions} into {@link AdvancedSuggestions}
     *
     * @param suggestions Suggestions to convert
     * @return Converted object
     */
    public static AdvancedSuggestions fromSuggestions(Suggestions suggestions) {
        List<AdvancedSuggestion> s = new ArrayList<>();
        for (Suggestion suggestion : suggestions.getList()) {
            if (suggestion instanceof AdvancedSuggestion) {
                s.add((AdvancedSuggestion) suggestion);
            } else {
                s.add(AdvancedSuggestion.fromSuggestion(suggestion));
            }
        }
        return new AdvancedSuggestions(suggestions.getRange(), s);
    }
}
