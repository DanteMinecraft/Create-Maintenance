package net.dantemc.create_maintenance;

import net.createmod.catnip.config.ui.BaseConfigScreen;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

import java.util.function.Supplier;

// This class will not load on dedicated servers. Accessing client side code from here is safe.
@Mod(value = CreateMaintenance.MODID, dist = Dist.CLIENT)
// You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
@EventBusSubscriber(modid = CreateMaintenance.MODID, value = Dist.CLIENT)
public class CreateMaintenanceClient {
    public CreateMaintenanceClient(ModContainer container) {
        // Allows NeoForge to create a config screen for this mod's configs.
        // The config screen is accessed by going to the Mods screen > clicking on your mod > clicking on config.
        // Do not forget to add translations for your config options to the en_us.json file.
        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
    }

    @SubscribeEvent
    static void onClientSetup(FMLClientSetupEvent event) {
        // Some client setup code
        CreateMaintenance.LOGGER.info("HELLO FROM CLIENT SETUP");
        CreateMaintenance.LOGGER.info("MINECRAFT NAME >> {}", Minecraft.getInstance().getUser().getName());
    }

    @SubscribeEvent
    public static void onLoadComplete(FMLLoadCompleteEvent event) {

        ModContainer container = ModList.get().getModContainerById(CreateMaintenance.MODID).orElseThrow();
        Supplier<IConfigScreenFactory> configScreen = () -> (mc, previousScreen) ->
                new BaseConfigScreen(previousScreen, CreateMaintenance.MODID);
        container.registerExtensionPoint(IConfigScreenFactory.class, configScreen
        );
    }
}
