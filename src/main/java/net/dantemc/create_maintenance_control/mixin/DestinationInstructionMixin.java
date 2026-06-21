package net.dantemc.create_maintenance_control.mixin;

import com.simibubi.create.content.trains.graph.DiscoveredPath;
import com.simibubi.create.content.trains.schedule.ScheduleRuntime;
import com.simibubi.create.content.trains.schedule.destination.DestinationInstruction;

import com.simibubi.create.content.trains.station.GlobalStation;
import net.dantemc.create_maintenance_control.CreateMaintenance;
import net.dantemc.create_maintenance_control.content.maintenance_box.StationUtils;
import net.dantemc.create_maintenance_control.railway.OfflineStationManager;
import net.minecraft.world.level.Level;
import com.simibubi.create.content.trains.graph.EdgePointType;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;

@Mixin(DestinationInstruction.class)
public abstract class DestinationInstructionMixin {

    @Shadow
    public abstract String getFilterForRegex();

    @Shadow public abstract String getFilter();

    @Redirect(
            method = "start",
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/util/ArrayList;add(Ljava/lang/Object;)Z"
            )
    )
    private boolean maintenance$filterSkippedStations(
            ArrayList<GlobalStation> validStations,
            Object stationObj,
            ScheduleRuntime runtime,
            Level level) {

        GlobalStation station = (GlobalStation) stationObj;

        if (OfflineStationManager.isStationSkipped(level, station)) {
            return false;
        }

        return validStations.add(station);

    }

    @Inject(
            method = "start",
            at = @At("HEAD"),
            cancellable = true
    )
    private void maintenance$skipEntryIfAllMatchingStationsAreSkipped(
            ScheduleRuntime runtime,
            Level level,
            CallbackInfoReturnable<DiscoveredPath> cir) {

        String regex = getFilterForRegex();

        boolean foundMatch = false;
        boolean allOffline = true;

        for (GlobalStation station : runtime.train.graph.getPoints(EdgePointType.STATION)) {
            if (!station.name.matches(regex))
                continue;

            foundMatch = true;

            if (!OfflineStationManager.isStationSkipped(level, station)) {
                allOffline = false;
                break;
            }
        }

        if (!foundMatch || !allOffline) {
            return;
        }

        CreateMaintenance.debug(
                "Skipping destination entry {} with filter '{}' for {} because matching stations are under maintenance",
                runtime.currentEntry,
                getFilterForRegex(),
                runtime.train
        );

        runtime.currentEntry =
                (runtime.currentEntry + 1)
                        % runtime.schedule.entries.size();

        cir.setReturnValue(null);
        cir.cancel();
    }
}