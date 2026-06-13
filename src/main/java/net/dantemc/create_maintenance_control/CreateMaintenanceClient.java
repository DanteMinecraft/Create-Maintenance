package net.dantemc.create_maintenance_control;

import net.createmod.catnip.config.ui.BaseConfigScreen;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;

@Mod.EventBusSubscriber(modid = CreateMaintenance.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)

public class CreateMaintenanceClient {

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        CreateMaintenance.LOGGER.info("HELLO FROM CLIENT SETUP");
    }

    @SubscribeEvent
    public static void onLoadComplete(FMLLoadCompleteEvent event) {

        ModContainer container = ModList.get()
                .getModContainerById(CreateMaintenance.MODID)
                .orElseThrow();

        container.registerExtensionPoint(
                ConfigScreenHandler.ConfigScreenFactory.class,
                () -> new ConfigScreenHandler.ConfigScreenFactory(previousScreen -> new BaseConfigScreen(previousScreen, CreateMaintenance.MODID))
        );

        BaseConfigScreen.setDefaultActionFor(CreateMaintenance.MODID, base -> base
                .withButtonLabels(null, "Common Settings", null)
                .withSpecs(null, Config.SPEC, null)
        );
    }
}
