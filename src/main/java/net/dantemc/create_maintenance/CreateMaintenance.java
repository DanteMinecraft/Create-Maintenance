package net.dantemc.create_maintenance;

import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.foundation.item.ItemDescription;
import com.tterrag.registrate.util.entry.BlockEntityEntry;
import com.tterrag.registrate.util.entry.BlockEntry;
import net.createmod.catnip.lang.FontHelper;
import net.dantemc.create_maintenance.maintenance_box.MaintenanceBoxBlock;
import net.dantemc.create_maintenance.maintenance_box.MaintenanceBoxBlockEntity;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import org.slf4j.Logger;
import com.mojang.logging.LogUtils;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStartingEvent;

@Mod(CreateMaintenance.MODID)
public class CreateMaintenance {

    public static final String MODID = "create_maintenance";
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

    public CreateMaintenance(IEventBus modEventBus, ModContainer modContainer) {
        modEventBus.addListener(this::commonSetup);

        CreateMaintenanceCreativeTabs.CREATIVE_TABS.register(modEventBus);

        REGISTRATE.registerEventListeners(modEventBus);

        NeoForge.EVENT_BUS.register(this);

        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
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
        CreateMaintenance.debug("Create: Maintenance has been initiated on the server");
    }
}
