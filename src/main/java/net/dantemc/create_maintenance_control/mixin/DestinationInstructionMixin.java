package net.dantemc.create_maintenance_control.mixin;

import com.simibubi.create.content.trains.graph.DiscoveredPath;
import com.simibubi.create.content.trains.schedule.ScheduleRuntime;
import com.simibubi.create.content.trains.schedule.destination.DestinationInstruction;

import com.simibubi.create.content.trains.station.GlobalStation;
import net.dantemc.create_maintenance_control.CreateMaintenance;
import net.dantemc.create_maintenance_control.maintenance_box.OfflineStationManager;
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
            ),
            remap = false
    )

    private boolean maintenance$filterOfflineStations(
            ArrayList<GlobalStation> validStations,
            Object stationObj) {

        GlobalStation station = (GlobalStation) stationObj;

        if (OfflineStationManager.isOffline(station.getId())) {
            CreateMaintenance.debug(
                    "Rejected station: {}",
                    station.name
            );
            return false;
        }

        CreateMaintenance.debug(
                "Accepted station: {}",
                station.name
        );

        boolean result = validStations.add(station);

        CreateMaintenance.debug(
                "Valid stations count: {}",
                validStations.size()
        );

        for (GlobalStation valid : validStations) {
            CreateMaintenance.debug(
                    "-> {}",
                    valid.name
            );
        }

        return result;

    }

    @Inject(
            method = "start",
            at = @At("RETURN"),
            remap = false
    )
    private void maintenance$debugResult(
            ScheduleRuntime runtime,
            Level level,
            CallbackInfoReturnable<DiscoveredPath> cir) {

        DiscoveredPath path = cir.getReturnValue();

        if (path == null) {
            CreateMaintenance.debug(
                    "PATHFINDING FAILED FOR ENTRY {}",
                    runtime.currentEntry
            );
        } else {
            CreateMaintenance.debug(
                    "PATHFINDING SUCCESS FOR ENTRY {}",
                    runtime.currentEntry
            );
        }

        CreateMaintenance.debug(
                "Current navigation destination: {}",
                runtime.train.navigation.destination == null
                        ? "null"
                        : runtime.train.navigation.destination.name
        );

        CreateMaintenance.debug(
                "Train currently at: {}",
                runtime.train.navigation.destination
        );

        CreateMaintenance.debug(
                "Returned path = {}",
                path
        );
    }

    @Inject(
            method = "start",
            at = @At("HEAD"),
            remap = false,
            cancellable = true
    )
    private void maintenance$checkForOfflineDestination(
            ScheduleRuntime runtime,
            Level level,
            CallbackInfoReturnable<DiscoveredPath> cir) {

        CreateMaintenance.debug("START ENTRY: " + runtime.currentEntry);

        String regex = getFilterForRegex();

        boolean foundMatch = false;
        boolean allOffline = true;

        for (GlobalStation station : runtime.train.graph.getPoints(EdgePointType.STATION)) {

            if (!station.name.matches(regex))
                continue;

            foundMatch = true;

            CreateMaintenance.debug("Matched station: " + station.name);

            if (!OfflineStationManager.isOffline(station.getId())) {
                allOffline = false;
            }
        }

        CreateMaintenance.debug("Current entry: " + runtime.currentEntry);
        CreateMaintenance.debug("Regex: " + regex);
        CreateMaintenance.debug("Found match: " + foundMatch);
        CreateMaintenance.debug("All offline: " + allOffline);

        if (foundMatch && allOffline) {

            CreateMaintenance.debug(
                    "SKIPPING ENTRY {}",
                    runtime.currentEntry
            );

            CreateMaintenance.debug(
                    "Schedule size: {}",
                    runtime.schedule.entries.size()
            );

            runtime.currentEntry =
                    (runtime.currentEntry + 1)
                            % runtime.schedule.entries.size();

            CreateMaintenance.debug(
                    "NEW ENTRY {}",
                    runtime.currentEntry
            );

            cir.setReturnValue(null);
            cir.cancel();
        }
    }
}