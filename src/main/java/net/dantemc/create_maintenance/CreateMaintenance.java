package net.dantemc.create_maintenance;

import com.simibubi.create.AllCreativeModeTabs;
import net.dantemc.create_maintenance.maintenance_box.MaintenanceBoxBlock;
import net.dantemc.create_maintenance.maintenance_box.MaintenanceBoxBlockEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(CreateMaintenance.MODID)
public class CreateMaintenance {

    public static final String MODID = "create_maintenance";
    public static final Logger LOGGER = LogUtils.getLogger();

    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(MODID);
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MODID);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, MODID);

    // Maintenance Marker Block
    public static final DeferredBlock<Block> MAINTENANCE_BOX = BLOCKS.register(
            "maintenance_box", () -> new MaintenanceBoxBlock(
                    BlockBehaviour.Properties.of().sound(SoundType.METAL)));

    public static final Supplier<BlockEntityType<MaintenanceBoxBlockEntity>>
            MAINTENANCE_BOX_BE =
            BLOCK_ENTITY_TYPES.register(
                    "maintenance_box",
                    () -> BlockEntityType.Builder.of(
                            MaintenanceBoxBlockEntity::new,
                            MAINTENANCE_BOX.get()
                    ).build(null)
            );

    public static final DeferredItem<BlockItem> MAINTENANCE_BOX_ITEM = ITEMS.register(
            "maintenance_box", () -> new IgnorePlacingRulesItem(MAINTENANCE_BOX.get(),new Item.Properties()));

    public CreateMaintenance(IEventBus modEventBus, ModContainer modContainer) {
        modEventBus.addListener(this::commonSetup);

        BLOCKS.register(modEventBus);
        ITEMS.register(modEventBus);
        BLOCK_ENTITY_TYPES.register(modEventBus);

        NeoForge.EVENT_BUS.register(this);
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);

        modEventBus.addListener(this::addCreative);
    }

    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == AllCreativeModeTabs.BASE_CREATIVE_TAB.getKey()) {
            event.accept(MAINTENANCE_BOX_ITEM);
        }
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
