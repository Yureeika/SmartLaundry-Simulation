package smartlaundry;

import java.util.Random;

public class Main {
    
    private static final boolean BONUS_SCENARIO = true;
    
    public static void main(String[] args) {
        
        javax.swing.SwingUtilities.invokeLater(() -> {
            new LaundryGUI().setVisible(true);
        });
        
        LaundryFacility facility =
            new LaundryFacility();

        Random random = new Random();

        // Enable the bonus payment failure scenario
        if (BONUS_SCENARIO) {
            facility.enablePaymentFailureScenario();
        }

        facility.printFacilityStatus();
        
        LaundryGUI.logActivity("Smart Laundry Facility started.");
        LaundryGUI.logActivity("Washing Machines: 6");
        LaundryGUI.logActivity("Dryers: 4");
        LaundryGUI.logActivity("Payment Kiosks: 2");
        LaundryGUI.logActivity("");

        LaundryGUI.logActivity(
        "Starting customer simulation..."
        );

        // Create 50 customers
        Customer[] customers =
                new Customer[50];

        for (int i = 0; i < customers.length; i++) {

            customers[i] = new Customer(
                    String.format(
                            "Customer-%02d",
                            i + 1
                    ),
                    facility
            );

            // Start the customer thread
            customers[i].start();

            // Random arrival interval: 0-3 seconds
            try {

                Thread.sleep(
                        random.nextInt(3001)
                );

            } catch (InterruptedException e) {

                Thread.currentThread().interrupt();

                System.out.println(
                        "Customer arrival simulation interrupted."
                );

                break;
            }
        }

        // Wait for every customer to finish
        for (Customer customer : customers) {

            if (customer != null) {

                try {

                    customer.join();

                } catch (InterruptedException e) {

                    Thread.currentThread().interrupt();

                    System.out.println(
                            "Main thread was interrupted."
                    );

                    break;
                }
            }
        }

        // Print final statistics
        facility.getStatistics()
                .printStatistics();

        LaundryGUI.logActivity(
                "\nAll customers have finished."
        );

        LaundryGUI.logActivity(
                "Smart Laundry Simulation Complete."
        );
    }
}