/*
 * Copyright (C) 2021 DarkKronicle
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package io.github.darkkronicle.advancedchatfilters;

import fi.dy.masa.malilib.event.InitializationHandler;
import io.github.darkkronicle.advancedchatcore.ModuleHandler;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public class AdvancedChatFilters implements ClientModInitializer {

    public static final String MOD_ID = "advancedchatfilters";

    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);

    @Override
    public void onInitializeClient() {
        // This will run after AdvancedChatCore's because of load order
        ModuleHandler.getInstance().registerInitHandler(MOD_ID, -1, new FiltersInitHandler());
    }
}
