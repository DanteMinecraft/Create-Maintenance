package net.dantemc.create_maintenance_control.content.maintenance_box;

public enum MaintenanceRedstoneMode {
    UNPOWERED_ACTIVE {
        @Override
        public boolean isMaintenanceActive(boolean powered) {
            return !powered;
        }
    },
    POWERED_ACTIVE {
        @Override
        public boolean isMaintenanceActive(boolean powered) {
            return powered;
        }
    };

    public abstract boolean isMaintenanceActive(boolean powered);
}
