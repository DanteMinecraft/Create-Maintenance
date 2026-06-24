package net.dantemc.create_maintenance_control;

import com.mojang.logging.LogUtils;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.foundation.item.ItemDescription;
import com.tterrag.registrate.util.entry.BlockEntityEntry;
import com.tterrag.registrate.util.entry.BlockEntry;
import net.createmod.catnip.lang.FontHelper;
import net.dantemc.create_maintenance_control.content.maintenance_box.MaintenanceBoxBlock;
import net.dantemc.create_maintenance_control.content.maintenance_box.MaintenanceBoxBlockEntity;
import net.dantemc.create_maintenance_control.foundation.IgnorePlacingRulesItem;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@Mod(CreateMaintenance.MODID)
public class CreateMaintenance {

    public static final String MODID = "create_maintenance_control";
    public static final Logger LOGGER = LogUtils.getLogger();

    public static final CreateRegistrate REGISTRATE =
            CreateRegistrate.create(MODID)
                    .defaultCreativeTab(CreateMaintenanceCreativeTabs.getBaseTab())
                    .setTooltipModifierFactory(item -> new ItemDescription.Modifier(item, FontHelper.Palette.STANDARD_CREATE));

    public static final BlockEntry<MaintenanceBoxBlock> MAINTENANCE_BOX =
            REGISTRATE.block("maintenance_box", MaintenanceBoxBlock::new)
                    .initialProperties(() -> Blocks.IRON_BLOCK)
                    .properties(p -> p.sound(SoundType.METAL))
                    .item(IgnorePlacingRulesItem::new)
                    .build()
                    .register();

    public static final BlockEntityEntry<MaintenanceBoxBlockEntity> MAINTENANCE_BOX_BE =
            REGISTRATE.blockEntity(
                            "maintenance_box",
                            MaintenanceBoxBlockEntity::new)
                    .validBlocks(MAINTENANCE_BOX)
                    .register();

    public CreateMaintenance() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        modEventBus.addListener(this::commonSetup);

        CreateMaintenanceCreativeTabs.CREATIVE_TABS.register(modEventBus);

        REGISTRATE.registerEventListeners(modEventBus);

        ModPackets.register();

        MinecraftForge.EVENT_BUS.register(this);

        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    public static ResourceLocation asResource(String path) {
        return ResourceLocation.fromNamespaceAndPath(MODID, path);
    }

    //helper method for loggers
    public static void debug(String message, Object... args) {
        if (Config.WRITE_DEBUG_LOGS.get()) {
            LOGGER.info("[CM] " + message, args);
        }
    }

    private void commonSetup(FMLCommonSetupEvent event) {
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        CreateMaintenance.debug("Create: Maintenance Control has been initiated on the server");
    }
}
