package net.dantemc.create_maintenance_control.content.maintenance_box.gui;

public class MaintenanceBoxScreen {
}

/*import com.simibubi.create.api.behaviour.display.DisplaySource;
import com.simibubi.create.api.behaviour.display.DisplayTarget;
import com.simibubi.create.content.redstone.displayLink.DisplayLinkBlockEntity;
import com.simibubi.create.foundation.gui.AllGuiTextures;
import com.simibubi.create.foundation.gui.AllIcons;
import com.simibubi.create.foundation.gui.ModularGuiLine;
import com.simibubi.create.foundation.gui.widget.IconButton;
import com.simibubi.create.foundation.gui.widget.Label;
import com.simibubi.create.foundation.gui.widget.ScrollInput;
import net.createmod.catnip.data.Couple;
import net.createmod.catnip.gui.AbstractSimiScreen;
import net.createmod.catnip.gui.widget.AbstractSimiWidget;
import net.dantemc.create_maintenance_control.content.maintenance_box.MaintenanceBoxBlockEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Collections;
import java.util.List;

public class MaintenanceBoxScreen extends AbstractSimiScreen {

    private static final ItemStack FALLBACK = new ItemStack(Items.BARRIER);

    private AllGuiTextures background;
    private MaintenanceBoxBlockEntity blockEntity;
    private IconButton confirmButton;

    BlockState sourceState;
    BlockState targetState;
    List<DisplaySource> sources;
    DisplayTarget target;

    ScrollInput sourceTypeSelector;
    Label sourceTypeLabel;
    ScrollInput targetLineSelector;
    Label targetLineLabel;
    AbstractSimiWidget sourceWidget;
    AbstractSimiWidget targetWidget;

    Couple<ModularGuiLine> configWidgets;

    public MaintenanceBoxScreen(MaintenanceBoxBlockEntity be) {
        this.background = AllGuiTextures.DATA_GATHERER;
        this.blockEntity = be;
        sources = Collections.emptyList();
        configWidgets = Couple.create(ModularGuiLine::new);
        target = null;
    }

}*/
