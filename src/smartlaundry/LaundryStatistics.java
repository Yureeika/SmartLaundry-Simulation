package smartlaundry;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class LaundryStatistics {

    // Number of customers who completed the whole process
    private final AtomicInteger completedCustomers =
            new AtomicInteger();

    // Total time spent by all completed customers
    private final AtomicLong totalCustomerTime =
            new AtomicLong();

    // Current and maximum number of washers in use
    private final AtomicInteger activeWashers =
            new AtomicInteger();

    private final AtomicInteger maxConcurrentWashers =
            new AtomicInteger();

    // Current and maximum number of dryers in use
    private final AtomicInteger activeDryers =
            new AtomicInteger();

    private final AtomicInteger maxConcurrentDryers =
            new AtomicInteger();

    // Failure and retry statistics
    private final AtomicInteger washerFailures =
            new AtomicInteger();

    private final AtomicInteger kioskFailures =
            new AtomicInteger();

    private final AtomicInteger retries =
            new AtomicInteger();

    // Record one completed customer
    public void customerCompleted(long customerTime) {

        completedCustomers.incrementAndGet();
        totalCustomerTime.addAndGet(customerTime);
    }

    // Record a washer becoming occupied
    public void washerStarted() {

        int current = activeWashers.incrementAndGet();

        maxConcurrentWashers.accumulateAndGet(
                current,
                Math::max
        );
    }

    // Record a washer becoming available
    public void washerFinished() {

        activeWashers.decrementAndGet();
    }

    // Record a dryer becoming occupied
    public void dryerStarted() {

        int current = activeDryers.incrementAndGet();

        maxConcurrentDryers.accumulateAndGet(
                current,
                Math::max
        );
    }

    // Record a dryer becoming available
    public void dryerFinished() {

        activeDryers.decrementAndGet();
    }

    public void recordWasherFailure() {

        washerFailures.incrementAndGet();
    }

    public void recordKioskFailure() {

        kioskFailures.incrementAndGet();
    }

    public void recordRetry() {

        retries.incrementAndGet();
    }

    // Display final statistics
    public void printStatistics() {

        int customers = completedCustomers.get();

        double averageTime = 0;

        if (customers > 0) {
            averageTime =
                totalCustomerTime.get()
                / (double) customers
                / 1000.0;
        }

        LaundryGUI.logActivity(
                "\n===== LAUNDRY STATISTICS ====="
        );

        LaundryGUI.logActivity(
                "Customers served: " + customers
        );

        LaundryGUI.logActivity(
                String.format(
                        "Average total time per customer: %.2f seconds",
                        averageTime
                )
        );
 
        LaundryGUI.logActivity(
                "Max concurrent washers: "
                + maxConcurrentWashers.get()
        );

        LaundryGUI.logActivity(
                "Max concurrent dryers: "
                + maxConcurrentDryers.get()
        );

        LaundryGUI.logActivity(
                "Washer failures: "
                + washerFailures.get()
        );

        LaundryGUI.logActivity(
                "Payment kiosk failures: "
                + kioskFailures.get()
        );

        LaundryGUI.logActivity(
                "Total retries: "
                + retries.get()
        );

        LaundryGUI.logActivity(
                "=============================="
        );
    }
}