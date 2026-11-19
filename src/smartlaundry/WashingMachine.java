package smartlaundry;

import java.util.concurrent.Semaphore;
import java.util.concurrent.ThreadLocalRandom;

public class WashingMachine {

    private final int machineId;

    // Only one customer can use this washer at a time
    private final Semaphore machineLock =
            new Semaphore(1, true);

    public WashingMachine(int machineId) {
        this.machineId = machineId;
    }

    public int getMachineId() {
        return machineId;
    }

    public void acquire(String customerName)
            throws InterruptedException {

        machineLock.acquire();

        LaundryGUI.logActivity(
                "[" + customerName + "] "
                + "is using Washer-"
                + machineId
        );
    }

    public void release(String customerName) {

        LaundryGUI.logActivity(
                "[" + customerName + "] "
                + "released Washer-"
                + machineId
        );

        machineLock.release();
    }

    // 5% chance of washer failure
    public boolean hasFailed() {

        return ThreadLocalRandom
                .current()
                .nextInt(100) < 5;
    }
}