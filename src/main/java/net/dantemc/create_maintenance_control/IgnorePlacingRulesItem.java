package net.dantemc.create_maintenance_control;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.Event;

@EventBusSubscriber
public class IgnorePlacingRulesItem extends BlockItem {

    public IgnorePlacingRulesItem(Block block, Properties properties) {
        super(block, properties);
    }

    @SubscribeEvent
    public static void alwaysPlace(PlayerInteractEvent.RightClickBlock event) {

        ItemStack stack = event.getItemStack();

        if (!(stack.getItem() instanceof IgnorePlacingRulesItem))
            return;

        event.setUseBlock(Event.Result.DENY);
    }
}
