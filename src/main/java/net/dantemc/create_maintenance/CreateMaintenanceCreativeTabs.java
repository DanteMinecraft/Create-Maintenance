package net.dantemc.create_maintenance;

import com.simibubi.create.AllCreativeModeTabs;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class CreateMaintenanceCreativeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, CreateMaintenance.MODID);

    private static final DeferredHolder<CreativeModeTab, CreativeModeTab> BASE_TAB =
            CREATIVE_TABS.register("main", () ->
                    CreativeModeTab.builder()
                            .title(Component.translatable("itemGroup.create_maintenance"))
                            .icon(() -> new ItemStack(CreateMaintenance.MAINTENANCE_BOX.asItem()))
                            .withTabsBefore(AllCreativeModeTabs.PALETTES_CREATIVE_TAB.getKey())
                            .build());

    public static ResourceKey<CreativeModeTab> getBaseTab() {
        return BASE_TAB.getKey();
    }

}
