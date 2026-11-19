package smartlaundry;

import java.util.concurrent.Semaphore;
import java.util.concurrent.ThreadLocalRandom;

public class PaymentKiosk {

    private final int kioskId;

    // Only one customer can use a kiosk at a time
    private final Semaphore kioskLock =
            new Semaphore(1, true);

    public PaymentKiosk(int kioskId) {
        this.kioskId = kioskId;
    }

    public int getKioskId() {
        return kioskId;
    }

    public void acquire(String customerName)
            throws InterruptedException {

        kioskLock.acquire();

        LaundryGUI.logActivity(
                "[" + customerName + "] "
                + "is using Kiosk-"
                + kioskId
        );
    }

    public void release(String customerName) {

        LaundryGUI.logActivity(
                "[" + customerName + "] "
                + "released Kiosk-"
                + kioskId
        );

        kioskLock.release();
    }

    // 5% chance that the payment kiosk fails
    public boolean hasFailed() {

        return ThreadLocalRandom
                .current()
                .nextInt(100) < 5;
    }
}