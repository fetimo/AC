/*
 * Copyright (C) 2021 DarkKronicle
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package io.github.darkkronicle.advancedchatfilters.config;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import fi.dy.masa.malilib.config.ConfigType;
import fi.dy.masa.malilib.config.IConfigBase;
import fi.dy.masa.malilib.config.IConfigOptionListEntry;
import fi.dy.masa.malilib.config.options.ConfigBoolean;
import fi.dy.masa.malilib.config.options.ConfigOptionList;
import fi.dy.masa.malilib.config.options.ConfigString;
import fi.dy.masa.malilib.util.StringUtils;
import io.github.darkkronicle.advancedchatcore.AdvancedChatCore;
import io.github.darkkronicle.advancedchatcore.config.SaveableConfig;
import io.github.darkkronicle.advancedchatcore.config.options.ConfigColor;
import io.github.darkkronicle.advancedchatcore.interfaces.ConfigRegistryOption;
import io.github.darkkronicle.advancedchatcore.interfaces.IJsonSave;
import io.github.darkkronicle.advancedchatcore.interfaces.IMatchProcessor;
import io.github.darkkronicle.advancedchatcore.util.Colors;
import io.github.darkkronicle.advancedchatcore.util.FindType;
import io.github.darkkronicle.advancedchatfilters.interfaces.IMatchReplace;
import io.github.darkkronicle.advancedchatfilters.registry.MatchProcessorRegistry;
import io.github.darkkronicle.advancedchatfilters.registry.MatchReplaceRegistry;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.Data;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import org.jetbrains.annotations.NotNull;

/**
 * Filter Storage This class is used to store data for filters. Each filter is based off of this
 * class. These are stored in an ArrayList for later usage.
 *
 * <p>Note: this class has a natural ordering that is inconsistent with equals for ordering.
 */
@Environment(EnvType.CLIENT)
@Data
public class Filter implements Comparable<Filter> {

    private static String translate(String key) {
        return StringUtils.translate("advancedchatfilters.config.filter." + key);
    }

    private Integer order = 0;

    /**
     * Name is only cosmetic. Shows up when editing filters. Way to distinguish filters for the
     * player.
     */
    private SaveableConfig<ConfigString> name =
            SaveableConfig.fromConfig(
                    "name", new ConfigString(translate("name"), "Default", translate("info.name")));

    /** Whether or not it should be used to filter chat messages currently. */
    private SaveableConfig<ConfigBoolean> active =
            SaveableConfig.fromConfig(
                    "active",
                    new ConfigBoolean(translate("active"), false, translate("info.active")));

    /** Whether or not it should be used to filter chat messages currently. */
    private SaveableConfig<ConfigBoolean> stripColors =
            SaveableConfig.fromConfig(
                    "stripColors",
                    new ConfigBoolean(
                            translate("stripcolors"), true, translate("info.stripcolors")));

    /** The Expression to find a match. The way it is interpreted is defined by findType. */
    private SaveableConfig<ConfigString> findString =
            SaveableConfig.fromConfig(
                    "findString",
                    new ConfigString(
                            translate("findstring"), "Hello", translate("info.findstring")));

    /**
     * How findString is used. LITERAL checks just for a match character to character. UPPERLOWER is
     * like literal, but ignore case. REGEX interprets findString as a regular expression.
     */
    private SaveableConfig<ConfigOptionList> findType =
            SaveableConfig.fromConfig(
                    "findType",
                    new ConfigOptionList(
                            translate("findtype"), FindType.LITERAL, translate("info.findtype")));

    public FindType getFind() {
        return FindType.fromFindType(findType.config.getStringValue());
    }

    /**
     * How the found string is modified. ONLYMATCH replaces only what was matched. FULLLINE replaces
     * the full message.
     */
    private SaveableConfig<ConfigOptionList> replaceType =
            SaveableConfig.fromConfig(
                    "replaceType",
                    new ConfigOptionList(
                            translate("replacetype"),
                            MatchReplaceRegistry.getInstance().getDefaultOption(),
                            translate("info.replacetype")));

    public IMatchReplace getReplace() {
        return ((MatchReplaceRegistry.MatchReplaceOption) replaceType.config.getOptionListValue())
                .getOption();
    }

    /**
     * What the found string replaces to. (ex. If replaceType is FULLLINE this will replace the
     * message with this)
     */
    private SaveableConfig<ConfigString> replaceTo =
            SaveableConfig.fromConfig(
                    "replaceTo",
                    new ConfigString(
                            translate("replaceto"), "Welcome", translate("info.replaceto")));

    private SaveableConfig<ConfigBoolean> replaceTextColor =
            SaveableConfig.fromConfig(
                    "replaceTextColor",
                    new ConfigBoolean(
                            translate("replacetextcolor"),
                            false,
                            translate("info.replacetextcolor")));

    private SaveableConfig<ConfigColor> textColor =
            SaveableConfig.fromConfig(
                    "textColor",
                    new ConfigColor(
                            translate("textcolor"),
                            Colors.getInstance().getColorOrWhite("white"),
                            translate("info.textcolor")));

    private SaveableConfig<ConfigBoolean> replaceBackgroundColor =
            SaveableConfig.fromConfig(
                    "replaceBackgroundColor",
                    new ConfigBoolean(
                            translate("replacebackgroundcolor"),
                            false,
                            translate("info.replacebackgroundcolor")));

    private SaveableConfig<ConfigColor> backgroundColor =
            SaveableConfig.fromConfig(
                    "backgroundColor",
                    new ConfigColor(
                            translate("backgroundcolor"),
                            Colors.getInstance().getColorOrWhite("white"),
                            translate("info.backgroundcolor")));

    private MatchProcessorRegistry processors = MatchProcessorRegistry.getInstance().clone();

    private final ImmutableList<SaveableConfig<?>> options =
            ImmutableList.of(
                    name,
                    active,
                    stripColors,
                    findString,
                    findType,
                    replaceType,
                    replaceTo,
                    replaceTextColor,
                    textColor,
                    replaceBackgroundColor,
                    backgroundColor);

    public List<String> getWidgetHoverLines() {
        String translated = StringUtils.translate("advancedchatfilters.config.filterdescription");
        ArrayList<String> hover = new ArrayList<>();
        for (String s : translated.split("\n")) {
            hover.add(
                    s.replaceAll(
                                    Pattern.quote("<name>"),
                                    Matcher.quoteReplacement(name.config.getStringValue()))
                            .replaceAll(
                                    Pattern.quote("<active>"),
                                    Matcher.quoteReplacement(active.config.getStringValue()))
                            .replaceAll(
                                    Pattern.quote("<find>"),
                                    Matcher.quoteReplacement(findString.config.getStringValue()))
                            .replaceAll(
                                    Pattern.quote("<findtype>"),
                                    Matcher.quoteReplacement(getFind().getDisplayName())));
        }
        return hover;
    }

    public static class FilterJsonSave implements IJsonSave<Filter> {

        @Override
        public Filter load(JsonObject obj) {
            Filter f = new Filter();
            if (obj.get("order") != null) {
                try {
                    f.setOrder(obj.get("order").getAsInt());
                } catch (Exception e) {
                    f.setOrder(0);
                }
            }
            for (SaveableConfig<?> conf : f.getOptions()) {
                IConfigBase option = conf.config;
                if (obj.has(conf.key)) {
                    option.setValueFromJsonElement(obj.get(conf.key));
                }
            }

            JsonElement processors = obj.get("processors");
            if (processors != null && processors.isJsonObject()) {
                JsonObject processorsObj = processors.getAsJsonObject();
                for (ConfigRegistryOption<IMatchProcessor> o : f.processors.getAll()) {
                    o.load(processorsObj.get(o.getSaveString()));
                }
            }

            return f;
        }

        @Override
        public JsonObject save(Filter filter) {
            JsonObject obj = new JsonObject();
            for (SaveableConfig<?> option : filter.getOptions()) {
                obj.add(option.key, option.config.getAsJsonElement());
            }

            JsonObject processors = new JsonObject();
            for (MatchProcessorRegistry.MatchProcessorOption o : filter.processors.getAll()) {
                processors.add(o.getSaveString(), o.save());
            }
            obj.add("processors", processors);

            obj.addProperty("order", filter.getOrder());
            return obj;
        }
    }

    @Override
    public int compareTo(@NotNull Filter o) {
        return order.compareTo(o.order);
    }

    public enum NotifySound implements IConfigOptionListEntry {
        NONE("none", null),
        ARROW_HIT_PLAYER("arrow_hit_player", SoundEvents.ENTITY_ARROW_HIT_PLAYER),
        ANVIL_BREAK("anvil_break", SoundEvents.BLOCK_ANVIL_BREAK),
        BEACON_ACTIVATE("beacon_activate", SoundEvents.BLOCK_BEACON_ACTIVATE),
        ELDER_GUARDIAN_CURSE("elder_guardian_curse", SoundEvents.ENTITY_ELDER_GUARDIAN_CURSE),
        ENDERMAN_TELEPORT("enderman_teleport", SoundEvents.ENTITY_ENDERMAN_TELEPORT),
        WOLOLO("wololo", SoundEvents.ENTITY_EVOKER_PREPARE_WOLOLO),
        BELL("bell_use", SoundEvents.BLOCK_BELL_USE),
        CLICK("button_click", SoundEvents.UI_BUTTON_CLICK.value()),
        HUSK_TO_ZOMBIE("husk_to_zombie", SoundEvents.ENTITY_HUSK_CONVERTED_TO_ZOMBIE),
        GLASS_BREAK("glass_break", SoundEvents.BLOCK_GLASS_BREAK);

        public final String configString;
        public final SoundEvent event;

        private static String translate(String key) {
            return StringUtils.translate("advancedchatfilters.config.notifysound." + key);
        }

        NotifySound(String configString, SoundEvent sound) {
            this.configString = configString;
            this.event = sound;
        }

        @Override
        public String getStringValue() {
            return configString;
        }

        @Override
        public String getDisplayName() {
            return translate(configString);
        }

        @Override
        public IConfigOptionListEntry cycle(boolean forward) {
            int id = this.ordinal();
            if (forward) {
                id++;
            } else {
                id--;
            }
            if (id >= values().length) {
                id = 0;
            } else if (id < 0) {
                id = values().length - 1;
            }
            return values()[id % values().length];
        }

        @Override
        public IConfigOptionListEntry fromString(String value) {
            return fromNotifySoundString(value);
        }

        public static NotifySound fromNotifySoundName(String notifysound) {
            for (NotifySound r : NotifySound.values()) {
                if (r.event == null) {
                    continue;
                }
                if (r.event
                        .getId()
                        .getPath()
                        .replaceAll("\\.", "_")
                        .toLowerCase()
                        .equalsIgnoreCase(notifysound)) {
                    return r;
                }
            }
            return NotifySound.NONE;
        }

        public static NotifySound fromNotifySoundString(String notifysound) {
            for (NotifySound r : NotifySound.values()) {
                if (r.configString.equals(notifysound)) {
                    return r;
                }
            }
            return NotifySound.NONE;
        }
    }

    public static Filter getRandomFilter() {
        Filter filter = new Filter();
        for (SaveableConfig<?> c : filter.getOptions()) {
            if (c.config.getType() == ConfigType.STRING) {
                ((ConfigString) c.config).setValueFromString(AdvancedChatCore.getRandomString());
            }
        }
        return filter;
    }
}
