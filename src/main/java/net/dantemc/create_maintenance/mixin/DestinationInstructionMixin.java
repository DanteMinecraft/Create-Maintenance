package net.dantemc.create_maintenance.mixin;

import com.simibubi.create.content.trains.graph.DiscoveredPath;
import com.simibubi.create.content.trains.schedule.ScheduleRuntime;
import com.simibubi.create.content.trains.schedule.destination.DestinationInstruction;

import com.simibubi.create.content.trains.station.GlobalStation;
import net.dantemc.create_maintenance.marker.OfflineStationManager;
import net.minecraft.world.level.Level;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;

import static com.simibubi.create.compat.jei.CreateJEI.runtime;

@Mixin(DestinationInstruction.class)
public class DestinationInstructionMixin {

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

        if (OfflineStationManager.OFFLINE_STATIONS.contains(station.getId())) {
            System.out.println("Skipping offline station: " + station.name);
            runtime.currentEntry++;
            cir.setReturnValue(null);
            return false;
        }

        return validStations.add(station);
    }
}