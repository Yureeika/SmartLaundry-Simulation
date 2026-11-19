package smartlaundry;

import java.util.Random;

public class Customer extends Thread {

    private final String customerName;
    private final LaundryFacility facility;
    private final Random random = new Random();

    public Customer(String customerName,
                    LaundryFacility facility) {

        super(customerName);

        this.customerName = customerName;
        this.facility = facility;
    }

    @Override
    public void run() {

        long startTime = System.currentTimeMillis();

        LaundryGUI.logActivity(
                "[" + customerName + "] arrived at the laundry."
        );

        try {

            // =========================================
            // STEP 1: WASHING
            // =========================================

            boolean washingComplete = false;

            while (!washingComplete) {

                WashingMachine washer =
                        facility.acquireWasher();

                try {

                    washer.acquire(customerName);

                    facility.getStatistics().washerStarted();

                    LaundryGUI.logActivity(
                            "[" + customerName + "] "
                            + "started washing."
                    );

                    // Random washing time: 4-6 seconds
                    int washTime =
                            4000 + random.nextInt(2001);

                    // First half of washing
                    Thread.sleep(washTime / 2);

                    // 5% chance that the washer fails
                    if (washer.hasFailed()) {

                        LaundryGUI.logActivity(
                                "[" + customerName + "] "
                                + "Washer-"
                                + washer.getMachineId()
                                + " FAILED during washing."
                        );

                        facility.getStatistics()
                                .recordWasherFailure();

                        facility.getStatistics()
                                .recordRetry();

                    } else {

                        // Second half of washing
                        Thread.sleep(washTime / 2);

                        washingComplete = true;

                        LaundryGUI.logActivity(
                                "[" + customerName + "] "
                                + "finished washing."
                        );
                    }

                } finally {

                    facility.getStatistics()
                            .washerFinished();

                    washer.release(customerName);

                    facility.releaseWasher(washer);
                }

                // If washing failed, retry with another washer
                if (!washingComplete) {

                    LaundryGUI.logActivity(
                            "[" + customerName + "] "
                            + "will retry washing."
                    );
                }
            }

            // =========================================
            // STEP 2: DRYING
            // =========================================

            Dryer dryer =
                    facility.acquireDryer();

            try {

                dryer.acquire(customerName);

                facility.getStatistics().dryerStarted();

                LaundryGUI.logActivity(
                        "[" + customerName + "] "
                        + "started drying."
                );

                // Random drying time: 3-5 seconds
                int dryingTime =
                        3000 + random.nextInt(2001);

                Thread.sleep(dryingTime);

                LaundryGUI.logActivity(
                        "[" + customerName + "] "
                        + "finished drying."
                );

            } finally {

                facility.getStatistics().dryerFinished();

                dryer.release(customerName);

                facility.releaseDryer(dryer);
            }

            // =========================================
            // STEP 3: PAYMENT
            // =========================================

            // Bonus scenario: wait if both payment kiosks
            // are temporarily unavailable
            facility.waitForPaymentAvailability(customerName);

            boolean paymentComplete = false;

            while (!paymentComplete) {

                PaymentKiosk kiosk =
                        facility.acquireKiosk();

                try {

                    kiosk.acquire(customerName);

                    LaundryGUI.logActivity(
                            "[" + customerName + "] "
                            + "started payment."
                    );

                    // 5% chance that the kiosk fails
                    if (kiosk.hasFailed()) {

                        LaundryGUI.logActivity(
                                "[" + customerName + "] "
                                + "Kiosk-"
                                + kiosk.getKioskId()
                                + " FAILED."
                        );

                        facility.getStatistics()
                                .recordKioskFailure();

                        facility.getStatistics()
                                .recordRetry();

                    } else {

                        // Payment takes 1-2 seconds
                        int paymentTime =
                                1000 + random.nextInt(1001);

                        Thread.sleep(paymentTime);

                        paymentComplete = true;

                        LaundryGUI.logActivity(
                                "[" + customerName + "] "
                                + "payment completed."
                        );
                    }

                } finally {

                    kiosk.release(customerName);

                    facility.releaseKiosk(kiosk);
                }

                // 2-second retry delay
                if (!paymentComplete) {

                    LaundryGUI.logActivity(
                            "[" + customerName + "] "
                            + "will retry payment in 2 seconds."
                    );

                    Thread.sleep(2000);
                }
            }

            // =========================================
            // CUSTOMER FINISHED
            // =========================================

            long endTime =
                    System.currentTimeMillis();

            long totalTime =
                    endTime - startTime;

            facility.getStatistics()
                    .customerCompleted(totalTime);

            LaundryGUI.logActivity(
                    "[" + customerName + "] "
                    + "finished the entire laundry process."
            );

            LaundryGUI.logActivity(
                    "[" + customerName + "] "
                    + "total time: "
                    + (totalTime / 1000.0)
                    + " seconds."
            );

        } catch (InterruptedException e) {

            Thread.currentThread().interrupt();

            System.out.println(
                    "[" + customerName + "] "
                    + "was interrupted."
            );
        }
    }
}