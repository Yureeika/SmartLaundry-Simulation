package smartlaundry;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class LaundryFacility {

    public static final int WASHER_COUNT = 6;
    public static final int DRYER_COUNT = 4;
    public static final int KIOSK_COUNT = 2;

    private final BlockingQueue<WashingMachine> availableWashers =
            new ArrayBlockingQueue<>(WASHER_COUNT);

    private final BlockingQueue<Dryer> availableDryers =
            new ArrayBlockingQueue<>(DRYER_COUNT);

    private final BlockingQueue<PaymentKiosk> availableKiosks =
            new ArrayBlockingQueue<>(KIOSK_COUNT);

    private final LaundryStatistics statistics =
            new LaundryStatistics();

    // =========================================
    // BONUS SCENARIO
    // =========================================

    private volatile boolean paymentSystemFailed = false;

    private final AtomicInteger paymentQueueCount =
            new AtomicInteger(0);

    private final AtomicBoolean ownerCalled =
            new AtomicBoolean(false);

    private final Object paymentFailureLock =
            new Object();

    public LaundryFacility() {

        for (int i = 1; i <= WASHER_COUNT; i++) {

            availableWashers.add(
                    new WashingMachine(i)
            );
        }

        for (int i = 1; i <= DRYER_COUNT; i++) {

            availableDryers.add(
                    new Dryer(i)
            );
        }

        for (int i = 1; i <= KIOSK_COUNT; i++) {

            availableKiosks.add(
                    new PaymentKiosk(i)
            );
        }
    }

    // =========================================
    // WASHERS
    // =========================================

    public WashingMachine acquireWasher()
            throws InterruptedException {

        return availableWashers.take();
    }

    public void releaseWasher(WashingMachine washer)
            throws InterruptedException {

        availableWashers.put(washer);
    }

    // =========================================
    // DRYERS
    // =========================================

    public Dryer acquireDryer()
            throws InterruptedException {

        return availableDryers.take();
    }

    public void releaseDryer(Dryer dryer)
            throws InterruptedException {

        availableDryers.put(dryer);
    }

    // =========================================
    // PAYMENT KIOSKS
    // =========================================

    public PaymentKiosk acquireKiosk()
            throws InterruptedException {

        return availableKiosks.take();
    }

    public void releaseKiosk(PaymentKiosk kiosk)
            throws InterruptedException {

        availableKiosks.put(kiosk);
    }

    // =========================================
    // BONUS: ENABLE PAYMENT FAILURE
    // =========================================

    public void enablePaymentFailureScenario() {

        synchronized (paymentFailureLock) {

            paymentSystemFailed = true;

            LaundryGUI.logActivity(
                    "\n*** BONUS SCENARIO ***"
            );

            LaundryGUI.logActivity(
                    "Both payment kiosks have failed for the day!"
            );

            LaundryGUI.logActivity(
                    "Customers will queue for payment."
            );
        }
    }

    // =========================================
    // BONUS: WAIT FOR PAYMENT SYSTEM
    // =========================================

    public void waitForPaymentAvailability(
            String customerName)
            throws InterruptedException {

        synchronized (paymentFailureLock) {

            // Normal operation
            if (!paymentSystemFailed) {
                return;
            }

            // Customer enters payment queue
            int waiting =
                    paymentQueueCount.incrementAndGet();

            LaundryGUI.logActivity(
                    "[" + customerName + "] "
                    + "entered the payment queue. "
                    + "Customers waiting: "
                    + waiting
            );

            // When 30 customers are waiting,
            // call the owner and restore the kiosks.
            if (waiting >= 30
                    && ownerCalled.compareAndSet(
                            false, true)) {

                LaundryGUI.logActivity(
                        "\n[BONUS] 30 customers are waiting!"
                );

                LaundryGUI.logActivity(
                        "[BONUS] OWNER HAS BEEN CALLED!"
                );

                paymentSystemFailed = false;

                LaundryGUI.logActivity(
                        "[BONUS] Payment kiosks have been restored."
                );

                LaundryGUI.logActivity(
                        "[BONUS] Payment queue can now be processed.\n"
                );

                // notify all waiting customers
                paymentFailureLock.notifyAll();
            }

            // Wait until the owner restores
            // the payment system.
            while (paymentSystemFailed) {

                paymentFailureLock.wait();
            }

            // Customer leaves the bonus queue
            paymentQueueCount.decrementAndGet();
        }
    }

    // =========================================
    // FACILITY STATUS
    // =========================================

    public void printFacilityStatus() {

        LaundryGUI.logActivity(
                "=== Smart Laundry Facility ==="
        );

        LaundryGUI.logActivity(
                "Washing machines: "
                + WASHER_COUNT
        );

        LaundryGUI.logActivity(
                "Dryers: "
                + DRYER_COUNT
        );

        LaundryGUI.logActivity(
                "Payment kiosks: "
                + KIOSK_COUNT
        );
    }

    // =========================================
    // STATISTICS
    // =========================================

    public LaundryStatistics getStatistics() {

        return statistics;
    }
}