package com.hyrulecraft;

import com.hyrulecraft.net.HyruleNet;
import com.hyrulecraft.registry.*;
import com.hyrulecraft.util.HyruleEvents;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HyruleCraft implements ModInitializer {
    public static final String MOD_ID = "hyrulecraft";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        ModSounds.init();
        ModParticles.init();
        ModEntities.init();
        ModBlocks.init();
        ModItems.init();
        ModSpawns.init();
        HyruleNet.registerServer();
        HyruleEvents.register();
        LOGGER.info("HyruleCraft cargado: ¡Hey! ¡Escucha!");
    }
}
