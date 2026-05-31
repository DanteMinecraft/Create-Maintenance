package net.dantemc.create_maintenance.mixin;

import com.simibubi.create.content.trains.graph.DiscoveredPath;
import com.simibubi.create.content.trains.schedule.ScheduleRuntime;
import com.simibubi.create.content.trains.schedule.destination.DestinationInstruction;

import com.simibubi.create.content.trains.station.GlobalStation;
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
            System.out.println("Skipping offline station: " + station.name);
            return false;
        }

        return validStations.add(station);
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

        String regex = getFilterForRegex();

        boolean foundMatch = false;
        boolean allOffline = true;

        for (GlobalStation station : runtime.train.graph.getPoints(EdgePointType.STATION)) {

            if (!station.name.matches(regex))
                continue;

            foundMatch = true;

            System.out.println("Matched station: " + station.name);

            if (!OfflineStationManager.isOffline(station.getId())) {
                allOffline = false;
            }
        }

        System.out.println("Current entry: " + runtime.currentEntry);
        System.out.println("Regex: " + regex);
        System.out.println("Found match: " + foundMatch);
        System.out.println("All offline: " + allOffline);

        if (foundMatch && allOffline) {
            System.out.println("SKIPPING ENTRY");
            runtime.currentEntry++;
            cir.setReturnValue(null);

        }
    }
}