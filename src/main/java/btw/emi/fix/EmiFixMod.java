package btw.emi.fix;

import api.AddonHandler;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import emi.dev.emi.emi.runtime.EmiPersistentData;

import java.io.File;
import java.lang.reflect.Field;

/**
 * EMI favorites persistence fix for Better Than Wolves CE 3.1.1.
 * Patches EmiPersistentData.FILE to an absolute path inside the game directory
 * because the original class uses a relative File("emi.json") which resolves
 * to the JVM working directory. We also keep a shutdown hook to flush data.
 */
@Environment(EnvType.CLIENT)
public class EmiFixMod implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        AddonHandler.logMessage("EMI Fix: Initializing...");

        try {
            // Determine the actual game directory. For 1.6.4 Fabric with FreesmLauncher,
            // the working directory is usually the instance root, so we need to go into .minecraft/
            File workingDir = new File(System.getProperty("user.dir"));
            File gameDir = new File(workingDir, ".minecraft");
            if (!gameDir.isDirectory()) {
                // Fallback: if we are already in .minecraft
                gameDir = workingDir;
            }

            File fixedFile = new File(gameDir, "emi.json");

            // Replace the relative FILE used by EmiPersistentData
            Field fileField = EmiPersistentData.class.getDeclaredField("FILE");
            fileField.setAccessible(true);
            fileField.set(null, fixedFile);

            AddonHandler.logMessage("EMI Fix: Patched EmiPersistentData.FILE to " + fixedFile.getAbsolutePath());

            // Ensure the directory exists
            if (!gameDir.exists()) {
                gameDir.mkdirs();
            }
        } catch (Exception e) {
            AddonHandler.logMessage("EMI Fix: Failed to patch FILE: " + e.getMessage());
            e.printStackTrace();
        }

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
