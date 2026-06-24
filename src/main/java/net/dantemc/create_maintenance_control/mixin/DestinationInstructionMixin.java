package net.dantemc.create_maintenance_control.mixin;

import com.simibubi.create.content.trains.graph.DiscoveredPath;
import com.simibubi.create.content.trains.schedule.ScheduleRuntime;
import com.simibubi.create.content.trains.schedule.destination.DestinationInstruction;

import com.simibubi.create.content.trains.station.GlobalStation;
import net.dantemc.create_maintenance_control.CreateMaintenance;
import net.dantemc.create_maintenance_control.railway.MaintenanceEntry;
import net.dantemc.create_maintenance_control.railway.MaintenanceSkipState;
import net.dantemc.create_maintenance_control.railway.OfflineStationManager;
import net.minecraft.world.level.Level;
import com.simibubi.create.content.trains.graph.EdgePointType;

import org.spongepowered.asm.mixin.Unique;
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
        MaintenanceSkipState state = maintenance$analyzeMatchingStations(runtime, level, regex);

        if (!state.anyMatch())
            return;

        if (!state.allSkipped())
            return;

        int scheduleSize = runtime.schedule.entries.size();

        CreateMaintenance.debug("All matching stations for '{}' are under maintenance", getFilter());

        // skip 1 entry (default behavior)
        if (!state.skipDownstream()) {
            runtime.currentEntry = (runtime.currentEntry + 1) % scheduleSize;
            cir.setReturnValue(null);
            cir.cancel();
            return;
        }

        // skipDownstream = true:
        // search for next destination entry that train can pathfind to
        int nextReachableEntry = maintenance$findNextReachableDestinationEntry(runtime, level);

        if (nextReachableEntry != -1) {
            CreateMaintenance.debug("Skipping downstream to schedule entry {}", nextReachableEntry);
            runtime.currentEntry = nextReachableEntry;
        } else {
            // fallback: if no stations work, only skip one
            runtime.currentEntry = (runtime.currentEntry + 1) % scheduleSize;
        }

        cir.setReturnValue(null);
        cir.cancel();
    }

    @Unique
    private MaintenanceSkipState maintenance$analyzeMatchingStations(ScheduleRuntime runtime, Level level, String regex) {
        boolean anyMatch = false;
        boolean allSkipped = true;
        boolean skipDownstream = false;

        for (GlobalStation station : runtime.train.graph.getPoints(EdgePointType.STATION)) {
            if (!station.name.matches(regex))
                continue;

            anyMatch = true;

            MaintenanceEntry entry = OfflineStationManager.getMatchingEntry(level, station);
            if (entry == null) {
                allSkipped = false;
                continue;
            }

            if (entry.skipDownstream()) {
                skipDownstream = true;
            }
        }

        return new MaintenanceSkipState(anyMatch, allSkipped, skipDownstream);
    }

    @Unique
    private ArrayList<GlobalStation> maintenance$getValidStations(ScheduleRuntime runtime, Level level, String regex) {
        ArrayList<GlobalStation> validStations = new ArrayList<>();

        for (GlobalStation station : runtime.train.graph.getPoints(EdgePointType.STATION)) {
            if (!station.name.matches(regex))
                continue;

            if (OfflineStationManager.isStationSkipped(level, station))
                continue;

            validStations.add(station);
        }

        return validStations;
    }

    @Unique
    private boolean maintenance$destinationEntryHasPath(ScheduleRuntime runtime, Level level, DestinationInstruction instruction) {
        ArrayList<GlobalStation> validStations = maintenance$getValidStations(runtime, level, instruction.getFilterForRegex());

        if (validStations.isEmpty())
            return false;

        DiscoveredPath path = runtime.train.navigation.findPathTo(validStations, Double.MAX_VALUE);
        return path != null;
    }

    @Unique
    private int maintenance$findNextReachableDestinationEntry(ScheduleRuntime runtime, Level level) {
        int scheduleSize = runtime.schedule.entries.size();
        int originalEntry = runtime.currentEntry;

        for (int offset = 1; offset < scheduleSize; offset++) {
            int candidateIndex = (originalEntry + offset) % scheduleSize;

            var scheduleEntry = runtime.schedule.entries.get(candidateIndex);
            if (scheduleEntry.instruction instanceof DestinationInstruction destinationInstruction) {
                if (maintenance$destinationEntryHasPath(runtime, level, destinationInstruction)) {
                    return candidateIndex;
                }
            }
        }
        return -1;
    }
}