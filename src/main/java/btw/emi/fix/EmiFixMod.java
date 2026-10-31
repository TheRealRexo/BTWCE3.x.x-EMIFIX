package btw.emi.fix;

import api.AddonHandler;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import emi.dev.emi.emi.runtime.EmiPersistentData;

/**
 * EMI Fix for Better Than Wolves CE.
 * 
 * Fixes the issue where EMI favorites disappear on game shutdown.
 * The vanilla EMI integration in BTWCE doesn't properly persist
 * favorites when the game exits. This mod hooks into the client
 * lifecycle and ensures favorites are saved before shutdown.
 */
@Environment(EnvType.CLIENT)
public class EmiFixMod implements ClientModInitializer {
    
    @Override
    public void onInitializeClient() {
        AddonHandler.logMessage("EMI Fix: Initializing...");
        
        // Register shutdown hook to save EMI data before game closes
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                AddonHandler.logMessage("EMI Fix: Saving favorites before shutdown...");
                EmiPersistentData.save();
                AddonHandler.logMessage("EMI Fix: Favorites saved successfully.");
            } catch (Exception e) {
                AddonHandler.logMessage("EMI Fix: Failed to save favorites: " + e.getMessage());
                e.printStackTrace();
            }
        }, "emi-fix-shutdown-hook"));
        
        AddonHandler.logMessage("EMI Fix: Ready. Favorites will now persist across game sessions.");
    }
}
