package btw.emi.fix;

import api.AddonHandler;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.impl.networking.server.ServerPlayNetworkingImpl;
import com.mojang.logging.LogUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import emi.dev.emi.emi.runtime.EmiPersistentData;

/**
 * Very small mod that ensures EMI favorites are persisted to disk on shutdown.
 * The original Better‑Than‑Wolves mod ships with EMI integrated, but does not
 * explicitly flush the persistent data when the client shuts down, so favorites
 * that were added during gameplay can be lost.
 *
 * This replace‑in‑place mod contains a minimal initializer that registers a
 * shutdown callback. It uses the EMIPersistentData class from the installed
 * EMI mod to write the favorites to the disk location that EMI expects
 * (``emi.json`` in the game directory).
 */
@Environment(EnvType.CLIENT)
public class EmiFixMod implements ClientModInitializer {
    private static final Logger LOGGER = LogUtils.getLogger();

    @Override
    public void onInitializeClient() {
        AddonHandler.logMessage("EMI Fix: Initializing...");
        LOGGER.info("[EmiFixMod] EMI Fix: Initializing...");

        // Register shutdown hook to save EMI data before game closes
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                AddonHandler.logMessage("EMI Fix: Saving favorites before shutdown...");
                LOGGER.info("[EmiFixMod] EMI Fix: Saving favorites before shutdown...");
                EmiPersistentData.save();
                AddonHandler.logMessage("EMI Fix: Favorites saved successfully.");
                LOGGER.info("[EmiFixMod] EMI Fix: Favorites saved successfully.");
            } catch (Exception e) {
                AddonHandler.logMessage("EMI Fix: Failed to save favorites: " + e.getMessage());
                LOGGER.error("[EmiFixMod] Failed to save favorites: " + e.getMessage(), e);
                e.printStackTrace();
            }
        }, "emi-fix-shutdown-hook"));

        AddonHandler.logMessage("EMI Fix: Ready. Favorites will now persist across game sessions.");
        LOGGER.info("[EmiFixMod] EMI Fix: Ready. Favorites will now persist across game sessions.");
    }
}
