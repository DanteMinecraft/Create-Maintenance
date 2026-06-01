package net.dantemc.create_maintenance.mixin;

import com.simibubi.create.content.trains.graph.DiscoveredPath;
import com.simibubi.create.content.trains.schedule.ScheduleRuntime;
import com.simibubi.create.content.trains.schedule.destination.DestinationInstruction;

import com.simibubi.create.content.trains.station.GlobalStation;
import net.dantemc.create_maintenance.CreateMaintenance;
import net.dantemc.create_maintenance.marker.OfflineStationManager;
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

    @Redirect(
            method = "start",
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/util/ArrayList;add(Ljava/lang/Object;)Z"
            )
    )
    private boolean maintenance$filterOfflineStations(
            ArrayList<GlobalStation> validStations,
            Object stationObj) {

        GlobalStation station = (GlobalStation) stationObj;

        if (OfflineStationManager.isOffline(station.getId())) {
            CreateMaintenance.LOGGER.info(
                    "[CM] Rejected station: {}",
                    station.name
            );
            return false;
        }

        CreateMaintenance.LOGGER.info(
                "[CM] Accepted station: {}",
                station.name
        );

        boolean result = validStations.add(station);

        CreateMaintenance.LOGGER.info(
                "[CM] Valid stations count: {}",
                validStations.size()
        );

        for (GlobalStation valid : validStations) {
            CreateMaintenance.LOGGER.info(
                    "[CM] -> {}",
                    valid.name
            );
        }

        return result;

    }

    @Inject(
            method = "start",
            at = @At("RETURN")
    )
    private void maintenance$debugResult(
            ScheduleRuntime runtime,
            Level level,
            CallbackInfoReturnable<DiscoveredPath> cir) {

        DiscoveredPath path = cir.getReturnValue();

        if (path == null) {
            CreateMaintenance.LOGGER.info(
                    "[CM] PATHFINDING FAILED FOR ENTRY {}",
                    runtime.currentEntry
            );
        } else {
            CreateMaintenance.LOGGER.info(
                    "[CM] PATHFINDING SUCCESS FOR ENTRY {}",
                    runtime.currentEntry
            );
        }

        CreateMaintenance.LOGGER.info(
                "[CM] Train currently at: {}",
                runtime.train.navigation.destination
        );

        CreateMaintenance.LOGGER.info(
                "[CM] Returned path = {}",
                path
        );
    }

    @Inject(
            method = "start",
            at = @At("HEAD"),
            cancellable = true
    )
    private void maintenance$checkForOfflineDestination(
            ScheduleRuntime runtime,
            Level level,
            CallbackInfoReturnable<DiscoveredPath> cir) {

        CreateMaintenance.LOGGER.info("[CM] START ENTRY: " + runtime.currentEntry);

        String regex = getFilterForRegex();

        boolean foundMatch = false;
        boolean allOffline = true;

        for (GlobalStation station : runtime.train.graph.getPoints(EdgePointType.STATION)) {

            if (!station.name.matches(regex))
                continue;

            foundMatch = true;

            CreateMaintenance.LOGGER.info("Matched station: " + station.name);

            if (!OfflineStationManager.isOffline(station.getId())) {
                allOffline = false;
            }
        }

        CreateMaintenance.LOGGER.info("Current entry: " + runtime.currentEntry);
        CreateMaintenance.LOGGER.info("Regex: " + regex);
        CreateMaintenance.LOGGER.info("Found match: " + foundMatch);
        CreateMaintenance.LOGGER.info("All offline: " + allOffline);

        if (foundMatch && allOffline) {

            CreateMaintenance.LOGGER.info(
                    "[CM] SKIPPING ENTRY {}",
                    runtime.currentEntry
            );

            CreateMaintenance.LOGGER.info(
                    "[CM] Schedule size: {}",
                    runtime.schedule.entries.size()
            );

            runtime.currentEntry =
                    (runtime.currentEntry + 1)
                            % runtime.schedule.entries.size();

            CreateMaintenance.LOGGER.info(
                    "[CM] NEW ENTRY {}",
                    runtime.currentEntry
            );

            cir.setReturnValue(null);
            cir.cancel();
        }
    }
}