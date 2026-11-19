package smartlaundry;

import java.util.concurrent.Semaphore;

public class Dryer {

    private final int dryerId;

    // Only one customer can use this dryer at a time
    private final Semaphore dryerLock =
            new Semaphore(1, true);

    public Dryer(int dryerId) {
        this.dryerId = dryerId;
    }

    public int getDryerId() {
        return dryerId;
    }

    public void acquire(String customerName)
            throws InterruptedException {

        LaundryGUI.logActivity(
                "[" + customerName + "] "
                + "is waiting for Dryer-"
                + dryerId
        );

        dryerLock.acquire();

        LaundryGUI.logActivity(
                "[" + customerName + "] "
                + "is using Dryer-"
                + dryerId
        );
    }

    public void release(String customerName) {

        LaundryGUI.logActivity(
                "[" + customerName + "] "
                + "finished with Dryer-"
                + dryerId
        );

        dryerLock.release();
    }
}
        