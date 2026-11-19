package smartlaundry;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class LaundryGUI extends JFrame {

    private static LaundryGUI instance;

    private final FacilityPanel facilityPanel;
    private final JTextArea activityArea;

    // =========================================================
    // MACHINE STATUS
    // =========================================================

    private final Map<Integer, String> washerStatus =
            new HashMap<>();

    private final Map<Integer, String> dryerStatus =
            new HashMap<>();

    private final Map<Integer, String> kioskStatus =
            new HashMap<>();

    // =========================================================
    // CUSTOMER COUNTERS
    // =========================================================

    private int waitingCustomers = 0;
    private int washingCustomers = 0;
    private int dryingCustomers = 0;
    private int paymentCustomers = 0;
    private int completedCustomers = 0;

    // =========================================================
    // BONUS SCENARIO
    // =========================================================

    private boolean bonusActive = false;
    private boolean ownerCalled = false;
    private boolean paymentRestored = false;

    // Both kiosks are blocked during the bonus failure.
    private boolean paymentSystemDown = false;

    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public LaundryGUI() {

        instance = this;

        setTitle("Smart Laundry Facility");

        setSize(
                1100,
                760
        );

        setMinimumSize(
                new Dimension(
                        1000,
                        700
                )
        );

        setDefaultCloseOperation(
                JFrame.EXIT_ON_CLOSE
        );

        setLocationRelativeTo(null);

        setLayout(
                new BorderLayout()
        );

        // =====================================================
        // MAIN SIMULATION PANEL
        // =====================================================

        facilityPanel =
                new FacilityPanel();

        add(
                facilityPanel,
                BorderLayout.CENTER
        );

        // =====================================================
        // ACTIVITY LOG
        // =====================================================

        activityArea =
                new JTextArea();

        activityArea.setEditable(false);

        activityArea.setFont(
                new Font(
                        "Monospaced",
                        Font.PLAIN,
                        12
                )
        );

        activityArea.setBackground(
                new Color(
                        252,
                        249,
                        244
                )
        );

        activityArea.setForeground(
                new Color(
                        70,
                        55,
                        48
                )
        );

        activityArea.setLineWrap(false);

        JScrollPane scrollPane =
                new JScrollPane(
                        activityArea
                );

        scrollPane.setPreferredSize(
                new Dimension(
                        1100,
                        170
                )
        );

        add(
                scrollPane,
                BorderLayout.SOUTH
        );

        // =====================================================
        // INITIAL STATUS
        // =====================================================

        for (int i = 1; i <= 6; i++) {

            washerStatus.put(
                    i,
                    "FREE"
            );
        }

        for (int i = 1; i <= 4; i++) {

            dryerStatus.put(
                    i,
                    "FREE"
            );
        }

        for (int i = 1; i <= 2; i++) {

            kioskStatus.put(
                    i,
                    "FREE"
            );
        }

        // =====================================================
        // ANIMATION TIMER
        // =====================================================

        Timer timer =
                new Timer(
                        80,
                        e -> {

                            facilityPanel.updateAnimation();

                            facilityPanel.repaint();
                        }
                );

        timer.start();
    }

    // =========================================================
    // ACTIVITY LOG
    // =========================================================

    public static void logActivity(
            String message) {

        System.out.println(message);

        SwingUtilities.invokeLater(() -> {

            if (instance != null) {

                instance.activityArea.append(
                        message + "\n"
                );

                instance.activityArea.setCaretPosition(
                        instance.activityArea
                                .getDocument()
                                .getLength()
                );

                instance.updateStatusFromMessage(
                        message
                );
            }
        });
    }

    // =========================================================
    // UPDATE GUI FROM LOG MESSAGES
    // =========================================================

    private void updateStatusFromMessage(
            String message) {

        // =====================================================
        // BONUS SCENARIO START
        // =====================================================

        /*
         * Main.java enables the bonus scenario before customers
         * begin arriving.
         *
         * Therefore the kiosks should immediately appear
         * BLOCKED when this message is received.
         */
        if (message.contains(
                "Both payment kiosks have failed for the day")) {

            paymentSystemDown = true;

            bonusActive = true;

            ownerCalled = false;

            paymentRestored = false;
        }

        // =====================================================
        // WASHER STARTED
        // =====================================================

        if (message.contains(
                "is using Washer-")) {

            int id =
                    extractNumber(
                            message,
                            "Washer-"
                    );

            if (id > 0) {

                washerStatus.put(
                        id,
                        getCustomerName(message)
                );

                washingCustomers++;
            }
        }

        // =====================================================
        // WASHER RELEASED
        // =====================================================

        if (message.contains(
                "released Washer-")) {

            int id =
                    extractNumber(
                            message,
                            "Washer-"
                    );

            if (id > 0) {

                washerStatus.put(
                        id,
                        "FREE"
                );

                if (washingCustomers > 0) {

                    washingCustomers--;
                }
            }
        }

        // =====================================================
        // DRYER STARTED
        // =====================================================

        if (message.contains(
                "is using Dryer-")) {

            int id =
                    extractNumber(
                            message,
                            "Dryer-"
                    );

            if (id > 0) {

                dryerStatus.put(
                        id,
                        getCustomerName(message)
                );

                dryingCustomers++;
            }
        }

        // =====================================================
        // DRYER RELEASED
        // =====================================================

        if (message.contains(
                "finished with Dryer-")) {

            int id =
                    extractNumber(
                            message,
                            "Dryer-"
                    );

            if (id > 0) {

                dryerStatus.put(
                        id,
                        "FREE"
                );

                if (dryingCustomers > 0) {

                    dryingCustomers--;
                }
            }
        }

        // =====================================================
        // KIOSK STARTED
        // =====================================================

        if (message.contains(
                "is using Kiosk-")) {

            int id =
                    extractNumber(
                            message,
                            "Kiosk-"
                    );

            if (id > 0) {

                kioskStatus.put(
                        id,
                        getCustomerName(message)
                );

                paymentCustomers++;
            }
        }

        // =====================================================
        // KIOSK RELEASED
        // =====================================================

        if (message.contains(
                "released Kiosk-")) {

            int id =
                    extractNumber(
                            message,
                            "Kiosk-"
                    );

            if (id > 0) {

                kioskStatus.put(
                        id,
                        "FREE"
                );

                if (paymentCustomers > 0) {

                    paymentCustomers--;
                }
            }
        }

        // =====================================================
        // CUSTOMER ENTERS PAYMENT QUEUE
        // =====================================================

        if (message.contains(
                "entered the payment queue")) {

            waitingCustomers++;
        }

        // =====================================================
        // 30 CUSTOMERS WAITING
        // =====================================================

        if (message.contains(
                "30 customers are waiting")) {

            bonusActive = true;

            ownerCalled = false;

            paymentRestored = false;

            paymentSystemDown = true;
        }

        // =====================================================
        // OWNER CALLED
        // =====================================================

        if (message.contains(
                "OWNER HAS BEEN CALLED")) {

            bonusActive = true;

            ownerCalled = true;

            paymentRestored = false;

            paymentSystemDown = true;
        }

        // =====================================================
        // PAYMENT KIOSKS RESTORED
        // =====================================================

        if (message.contains(
                "Payment kiosks have been restored")) {

            bonusActive = true;

            ownerCalled = true;

            paymentRestored = true;

            // Kiosks become available again.
            paymentSystemDown = false;

            /*
             * Keep the success message visible for six seconds.
             */
            Timer bonusTimer =
                    new Timer(
                            6000,
                            e -> {

                                bonusActive = false;

                                ownerCalled = false;

                                paymentRestored = false;

                                facilityPanel.repaint();
                            }
                    );

            bonusTimer.setRepeats(false);

            bonusTimer.start();
        }

        // =====================================================
        // PAYMENT QUEUE CAN CONTINUE
        // =====================================================

        if (message.contains(
                "Payment queue can now be processed")) {

            waitingCustomers = 0;
        }

        // =====================================================
        // CUSTOMER COMPLETED
        // =====================================================

        if (message.contains(
                "finished the entire laundry process")) {

            completedCustomers++;
        }

        facilityPanel.repaint();
    }

    // =========================================================
    // EXTRACT NUMBER
    // =========================================================

    private int extractNumber(
            String message,
            String keyword) {

        try {

            int start =
                    message.indexOf(keyword)
                    + keyword.length();

            StringBuilder number =
                    new StringBuilder();

            while (
                    start < message.length()
                    && Character.isDigit(
                            message.charAt(start))
                    ) {

                number.append(
                        message.charAt(start)
                );

                start++;
            }

            return Integer.parseInt(
                    number.toString()
            );

        } catch (Exception e) {

            return -1;
        }
    }

    // =========================================================
    // CUSTOMER NAME
    // =========================================================

    private String getCustomerName(
            String message) {

        if (message.startsWith("[")) {

            int end =
                    message.indexOf("]");

            if (end > 0) {

                return message.substring(
                        1,
                        end
                );
            }
        }

        return "BUSY";
    }

    // =========================================================
    // FACILITY PANEL
    // =========================================================

    private class FacilityPanel
            extends JPanel {

        private double bubbleOffset = 0;

        private final Font titleFont =
                new Font(
                        "SansSerif",
                        Font.BOLD,
                        25
                );

        private final Font sectionFont =
                new Font(
                        "SansSerif",
                        Font.BOLD,
                        16
                );

        private final Font normalFont =
                new Font(
                        "SansSerif",
                        Font.PLAIN,
                        12
                );

        private final Font smallBoldFont =
                new Font(
                        "SansSerif",
                        Font.BOLD,
                        11
                );

        public FacilityPanel() {

            setBackground(
                    new Color(
                            250,
                            246,
                            239
                    )
            );
        }

        // =====================================================
        // ANIMATION
        // =====================================================

        public void updateAnimation() {

            bubbleOffset += 0.8;

            if (bubbleOffset > 30) {

                bubbleOffset = 0;
            }
        }

        // =====================================================
        // PAINT
        // =====================================================

        @Override
        protected void paintComponent(
                Graphics g) {

            super.paintComponent(g);

            Graphics2D g2 =
                    (Graphics2D) g;

            g2.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON
            );

            g2.setColor(
                    new Color(
                            250,
                            246,
                            239
                    )
            );

            g2.fillRect(
                    0,
                    0,
                    getWidth(),
                    getHeight()
            );

            drawBackgroundBubbles(g2);

            drawHeader(g2);

            drawJourney(g2);

            drawWashers(g2);

            drawDryers(g2);

            drawKiosks(g2);

            drawCounters(g2);

            if (bonusActive) {

                drawBonusScenario(g2);
            }
        }

        // =====================================================
        // HEADER
        // =====================================================

        private void drawHeader(
                Graphics2D g2) {

            g2.setColor(
                    new Color(
                            76,
                            55,
                            45
                    )
            );

            g2.setFont(titleFont);

            g2.drawString(
                    "SMART LAUNDRY FACILITY",
                    35,
                    35
            );

            g2.setFont(normalFont);

            g2.setColor(
                    new Color(
                            120,
                            95,
                            80
                    )
            );

            g2.drawString(
                    "A tiny busy laundromat powered by Java threads",
                    37,
                    55
            );

            // Clothesline
            g2.setColor(
                    new Color(
                            180,
                            150,
                            130
                    )
            );

            g2.drawLine(
                    35,
                    65,
                    1060,
                    65
            );

            drawTinyShirt(
                    g2,
                    120,
                    66
            );

            drawTinyShirt(
                    g2,
                    200,
                    66
            );

            drawTinyShirt(
                    g2,
                    1000,
                    66
            );
        }

        // =====================================================
        // CUSTOMER JOURNEY
        // =====================================================

        private void drawJourney(
                Graphics2D g2) {

            int y = 75;

            g2.setFont(sectionFont);

            g2.setColor(
                    new Color(
                            76,
                            55,
                            45
                    )
            );

            g2.drawString(
                    "CUSTOMER JOURNEY",
                    35,
                    y + 16
            );

            String[] stages = {
                "WAITING",
                "WASHING",
                "DRYING",
                "PAYMENT",
                "DONE"
            };

            int[] counts = {
                waitingCustomers,
                washingCustomers,
                dryingCustomers,
                paymentCustomers,
                completedCustomers
            };

            for (
                    int i = 0;
                    i < stages.length;
                    i++
            ) {

                int x =
                        35
                        + i * 205;

                drawJourneyBox(
                        g2,
                        x,
                        y + 25,
                        stages[i],
                        counts[i]
                );

                if (i < 4) {

                    g2.setColor(
                            new Color(
                                    190,
                                    165,
                                    145
                            )
                    );

                    g2.drawLine(
                            x + 155,
                            y + 48,
                            x + 190,
                            y + 48
                    );

                    g2.drawLine(
                            x + 180,
                            y + 43,
                            x + 190,
                            y + 48
                    );

                    g2.drawLine(
                            x + 180,
                            y + 53,
                            x + 190,
                            y + 48
                    );
                }
            }
        }

        private void drawJourneyBox(
                Graphics2D g2,
                int x,
                int y,
                String title,
                int count) {

            g2.setColor(
                    new Color(
                            255,
                            252,
                            247
                    )
            );

            g2.fillRoundRect(
                    x,
                    y,
                    155,
                    40,
                    16,
                    16
            );

            g2.setColor(
                    new Color(
                            190,
                            165,
                            145
                    )
            );

            g2.drawRoundRect(
                    x,
                    y,
                    155,
                    40,
                    16,
                    16
            );

            g2.setFont(
                    smallBoldFont
            );

            g2.setColor(
                    new Color(
                            85,
                            65,
                            55
                    )
            );

            g2.drawString(
                    title,
                    x + 10,
                    y + 16
            );

            g2.setFont(
                    new Font(
                            "SansSerif",
                            Font.BOLD,
                            15
                    )
            );

            g2.drawString(
                    String.valueOf(count),
                    x + 128,
                    y + 27
            );
        }

        // =====================================================
        // WASHERS
        // =====================================================

        private void drawWashers(
                Graphics2D g2) {

            g2.setFont(sectionFont);

            g2.setColor(
                    new Color(
                            76,
                            55,
                            45
                    )
            );

            g2.drawString(
                    "WASHING MACHINES",
                    35,
                    175
            );

            for (
                    int i = 1;
                    i <= 6;
                    i++
            ) {

                int row =
                        (i - 1) / 3;

                int column =
                        (i - 1) % 3;

                int x =
                        35
                        + column * 145;

                int y =
                        187
                        + row * 92;

                drawMachine(
                        g2,
                        x,
                        y,
                        125,
                        72,
                        "W" + i,
                        washerStatus.get(i)
                );
            }
        }

        // =====================================================
        // DRYERS
        // =====================================================

        private void drawDryers(
                Graphics2D g2) {

            g2.setFont(sectionFont);

            g2.setColor(
                    new Color(
                            76,
                            55,
                            45
                    )
            );

            g2.drawString(
                    "DRYERS",
                    500,
                    175
            );

            for (
                    int i = 1;
                    i <= 4;
                    i++
            ) {

                int row =
                        (i - 1) / 2;

                int column =
                        (i - 1) % 2;

                int x =
                        500
                        + column * 145;

                int y =
                        187
                        + row * 92;

                drawMachine(
                        g2,
                        x,
                        y,
                        125,
                        72,
                        "D" + i,
                        dryerStatus.get(i)
                );
            }
        }

        // =====================================================
        // PAYMENT KIOSKS
        // =====================================================

        private void drawKiosks(
                Graphics2D g2) {

            g2.setFont(sectionFont);

            g2.setColor(
                    new Color(
                            76,
                            55,
                            45
                    )
            );

            g2.drawString(
                    "PAYMENT KIOSKS",
                    805,
                    175
            );

            for (
                    int i = 1;
                    i <= 2;
                    i++
            ) {

                int x = 805;

                int y =
                        187
                        + (i - 1) * 92;

                String displayStatus;

                if (paymentSystemDown) {

                    displayStatus = "BLOCKED";

                } else {

                    displayStatus =
                            kioskStatus.get(i);
                }

                drawKiosk(
                        g2,
                        x,
                        y,
                        160,
                        72,
                        "K" + i,
                        displayStatus
                );
            }
        }

        // =====================================================
        // KIOSK
        // =====================================================

        private void drawKiosk(
                Graphics2D g2,
                int x,
                int y,
                int width,
                int height,
                String name,
                String status) {

            boolean blocked =
                    status.equals("BLOCKED");

            boolean busy =
                    !blocked
                    && status != null
                    && !status.equals("FREE");

            // Shadow
            g2.setColor(
                    new Color(
                            215,
                            205,
                            195
                    )
            );

            g2.fillRoundRect(
                    x + 3,
                    y + 4,
                    width,
                    height,
                    16,
                    16
            );

            // Body
            if (blocked) {

                g2.setColor(
                        new Color(
                                238,
                                205,
                                195
                        )
                );

            } else if (busy) {

                g2.setColor(
                        new Color(
                                222,
                                193,
                                163
                        )
                );

            } else {

                g2.setColor(
                        new Color(
                                235,
                                231,
                                226
                        )
                );
            }

            g2.fillRoundRect(
                    x,
                    y,
                    width,
                    height,
                    16,
                    16
            );

            // Border
            g2.setColor(
                    blocked
                    ? new Color(
                            190,
                            90,
                            80
                    )
                    : new Color(
                            105,
                            85,
                            73
                    )
            );

            g2.drawRoundRect(
                    x,
                    y,
                    width,
                    height,
                    16,
                    16
            );

            // Name
            g2.setFont(
                    new Font(
                            "SansSerif",
                            Font.BOLD,
                            14
                    )
            );

            g2.setColor(
                    new Color(
                            76,
                            55,
                            45
                    )
            );

            g2.drawString(
                    name,
                    x + 9,
                    y + 18
            );

            // Screen
            int screenSize = 38;

            int screenX =
                    x
                    + (width - screenSize) / 2;

            int screenY =
                    y + 21;

            g2.setColor(
                    blocked
                    ? new Color(
                            215,
                            185,
                            180
                    )
                    : new Color(
                            220,
                            216,
                            211
                    )
            );

            g2.fillRoundRect(
                    screenX,
                    screenY,
                    screenSize,
                    screenSize,
                    8,
                    8
            );

            g2.setColor(
                    new Color(
                            105,
                            85,
                            73
                    )
            );

            g2.drawRoundRect(
                    screenX,
                    screenY,
                    screenSize,
                    screenSize,
                    8,
                    8
            );

            // =================================================
            // BLOCKED X
            // =================================================

            if (blocked) {

                g2.setStroke(
                        new BasicStroke(
                                3f
                        )
                );

                g2.setColor(
                        new Color(
                                180,
                                75,
                                70
                        )
                );

                g2.drawLine(
                        screenX + 9,
                        screenY + 9,
                        screenX + 29,
                        screenY + 29
                );

                g2.drawLine(
                        screenX + 29,
                        screenY + 9,
                        screenX + 9,
                        screenY + 29
                );

            } else if (busy) {

                double angle =
                        System.currentTimeMillis()
                        / 180.0;

                int centerX =
                        screenX
                        + screenSize / 2;

                int centerY =
                        screenY
                        + screenSize / 2;

                int bubbleX =
                        centerX
                        + (int)
                        (
                                8
                                * Math.cos(angle)
                        );

                int bubbleY =
                        centerY
                        + (int)
                        (
                                8
                                * Math.sin(angle)
                        );

                g2.setColor(
                        new Color(
                                115,
                                170,
                                185
                        )
                );

                g2.fillOval(
                        bubbleX - 4,
                        bubbleY - 4,
                        8,
                        8
                );
            }

            // Status
            g2.setFont(
                    smallBoldFont
            );

            g2.setColor(
                    blocked
                    ? new Color(
                            165,
                            70,
                            65
                    )
                    : new Color(
                            76,
                            60,
                            52
                    )
            );

            g2.drawString(
                    status,
                    x + 9,
                    y + height - 7
            );

            // Status light
            if (blocked) {

                g2.setColor(
                        new Color(
                                220,
                                90,
                                80
                        )
                );

            } else if (busy) {

                g2.setColor(
                        new Color(
                                220,
                                135,
                                110
                        )
                );

            } else {

                g2.setColor(
                        new Color(
                                130,
                                180,
                                135
                        )
                );
            }

            g2.fillOval(
                    x + width - 17,
                    y + 9,
                    8,
                    8
            );
        }

        // =====================================================
        // WASHER / DRYER
        // =====================================================

        private void drawMachine(
                Graphics2D g2,
                int x,
                int y,
                int width,
                int height,
                String name,
                String status) {

            boolean busy =
                    status != null
                    && !status.equals("FREE");

            // Shadow
            g2.setColor(
                    new Color(
                            215,
                            205,
                            195
                    )
            );

            g2.fillRoundRect(
                    x + 3,
                    y + 4,
                    width,
                    height,
                    16,
                    16
            );

            // Body
            g2.setColor(
                    busy
                    ? new Color(
                            222,
                            193,
                            163
                    )
                    : new Color(
                            235,
                            231,
                            226
                    )
            );

            g2.fillRoundRect(
                    x,
                    y,
                    width,
                    height,
                    16,
                    16
            );

            // Border
            g2.setColor(
                    new Color(
                            105,
                            85,
                            73
                    )
            );

            g2.drawRoundRect(
                    x,
                    y,
                    width,
                    height,
                    16,
                    16
            );

            // Name
            g2.setFont(
                    new Font(
                            "SansSerif",
                            Font.BOLD,
                            14
                    )
            );

            g2.drawString(
                    name,
                    x + 9,
                    y + 18
            );

            // Door
            int doorSize = 38;

            int doorX =
                    x
                    + (width - doorSize) / 2;

            int doorY =
                    y + 21;

            g2.setColor(
                    new Color(
                            220,
                            216,
                            211
                    )
            );

            g2.fillOval(
                    doorX,
                    doorY,
                    doorSize,
                    doorSize
            );

            g2.setColor(
                    new Color(
                            105,
                            85,
                            73
                    )
            );

            g2.drawOval(
                    doorX,
                    doorY,
                    doorSize,
                    doorSize
            );

            // Bubbles
            if (busy) {

                double angle =
                        System.currentTimeMillis()
                        / 180.0;

                int centerX =
                        doorX
                        + doorSize / 2;

                int centerY =
                        doorY
                        + doorSize / 2;

                int bubbleX =
                        centerX
                        + (int)
                        (
                                10
                                * Math.cos(angle)
                        );

                int bubbleY =
                        centerY
                        + (int)
                        (
                                10
                                * Math.sin(angle)
                        );

                g2.setColor(
                        new Color(
                                115,
                                170,
                                185
                        )
                );

                g2.fillOval(
                        bubbleX - 4,
                        bubbleY - 4,
                        8,
                        8
                );

                g2.setColor(
                        new Color(
                                180,
                                220,
                                225
                        )
                );

                g2.fillOval(
                        doorX + 7,
                        doorY + 6,
                        5,
                        5
                );
            }

            // Status
            g2.setFont(
                    smallBoldFont
            );

            String displayStatus =
                    busy
                    ? status
                    : "FREE";

            if (displayStatus.length() > 16) {

                displayStatus =
                        displayStatus.substring(
                                0,
                                16
                        );
            }

            g2.setColor(
                    new Color(
                            76,
                            60,
                            52
                    )
            );

            g2.drawString(
                    displayStatus,
                    x + 9,
                    y + height - 7
            );

            // Status light
            g2.setColor(
                    busy
                    ? new Color(
                            220,
                            135,
                            110
                    )
                    : new Color(
                            130,
                            180,
                            135
                    )
            );

            g2.fillOval(
                    x + width - 17,
                    y + 9,
                    8,
                    8
            );
        }

        // =====================================================
        // LIVE COUNTERS
        // =====================================================

        private void drawCounters(
                Graphics2D g2) {

            int y = 390;

            g2.setFont(sectionFont);

            g2.setColor(
                    new Color(
                            76,
                            55,
                            45
                    )
            );

            g2.drawString(
                    "LIVE FACILITY STATUS",
                    35,
                    y
            );

            drawCounter(
                    g2,
                    35,
                    y + 12,
                    "WASHING",
                    washingCustomers
            );

            drawCounter(
                    g2,
                    210,
                    y + 12,
                    "DRYING",
                    dryingCustomers
            );

            drawCounter(
                    g2,
                    385,
                    y + 12,
                    "PAYMENT",
                    paymentCustomers
            );

            drawCounter(
                    g2,
                    560,
                    y + 12,
                    "WAITING",
                    waitingCustomers
            );

            drawCounter(
                    g2,
                    735,
                    y + 12,
                    "COMPLETED",
                    completedCustomers
            );
        }

        private void drawCounter(
                Graphics2D g2,
                int x,
                int y,
                String title,
                int value) {

            g2.setColor(
                    new Color(
                            255,
                            252,
                            247
                    )
            );

            g2.fillRoundRect(
                    x,
                    y,
                    150,
                    52,
                    16,
                    16
            );

            g2.setColor(
                    new Color(
                            190,
                            165,
                            145
                    )
            );

            g2.drawRoundRect(
                    x,
                    y,
                    150,
                    52,
                    16,
                    16
            );

            g2.setFont(
                    smallBoldFont
            );

            g2.setColor(
                    new Color(
                            100,
                            80,
                            68
                    )
            );

            g2.drawString(
                    title,
                    x + 10,
                    y + 19
            );

            g2.setFont(
                    new Font(
                            "SansSerif",
                            Font.BOLD,
                            18
                    )
            );

            g2.drawString(
                    String.valueOf(value),
                    x + 122,
                    y + 35
            );
        }

        // =====================================================
        // BONUS PANEL
        // =====================================================

        private void drawBonusScenario(
                Graphics2D g2) {

            int x = 280;

            int y = 470;

            int width = 540;

            int height = 92;

            // Shadow
            g2.setColor(
                    new Color(
                            210,
                            195,
                            185
                    )
            );

            g2.fillRoundRect(
                    x + 4,
                    y + 4,
                    width,
                    height,
                    22,
                    22
            );

            // Background
            g2.setColor(
                    paymentRestored
                    ? new Color(
                            224,
                            242,
                            225
                    )
                    : new Color(
                            255,
                            237,
                            215
                    )
            );

            g2.fillRoundRect(
                    x,
                    y,
                    width,
                    height,
                    22,
                    22
            );

            // Border
            g2.setColor(
                    paymentRestored
                    ? new Color(
                            115,
                            165,
                            120
                    )
                    : new Color(
                            210,
                            150,
                            100
                    )
            );

            g2.setStroke(
                    new BasicStroke(
                            2f
                    )
            );

            g2.drawRoundRect(
                    x,
                    y,
                    width,
                    height,
                    22,
                    22
            );

            // =================================================
            // RESTORED
            // =================================================

            if (paymentRestored) {

                g2.setFont(
                        new Font(
                                "SansSerif",
                                Font.BOLD,
                                19
                        )
                );

                g2.setColor(
                        new Color(
                                70,
                                110,
                                75
                        )
                );

                g2.drawString(
                        "PAYMENT CHAOS SOLVED!",
                        x + 155,
                        y + 27
                );

                g2.setFont(
                        new Font(
                                "SansSerif",
                                Font.PLAIN,
                                13
                        )
                );

                g2.drawString(
                        "Owner helped restore the payment system!",
                        x + 145,
                        y + 50
                );

                g2.setFont(
                        new Font(
                                "SansSerif",
                                Font.BOLD,
                                13
                        )
                );

                g2.drawString(
                        "Payment queue can continue!",
                        x + 175,
                        y + 73
                );

                drawOwner(
                        g2,
                        x + 45,
                        y + 8,
                        true
                );

            } else {

                // =================================================
                // FAILURE
                // =================================================

                g2.setFont(
                        new Font(
                                "SansSerif",
                                Font.BOLD,
                                19
                        )
                );

                g2.setColor(
                        new Color(
                                150,
                                90,
                                55
                        )
                );

                g2.drawString(
                        "PAYMENT CHAOS!",
                        x + 180,
                        y + 25
                );

                g2.setFont(
                        new Font(
                                "SansSerif",
                                Font.BOLD,
                                14
                        )
                );

                if (ownerCalled) {

                    g2.drawString(
                            "30+ CUSTOMERS WAITING",
                            x + 180,
                            y + 48
                    );

                    g2.drawString(
                            "OWNER HAS BEEN CALLED!",
                            x + 180,
                            y + 69
                    );

                } else {

                    g2.drawString(
                            "PAYMENT SYSTEM IS BLOCKED",
                            x + 160,
                            y + 52
                    );
                }

                drawOwner(
                        g2,
                        x + 45,
                        y + 8,
                        false
                );
            }
        }

        // =====================================================
        // OWNER CHARACTER
        // =====================================================

        private void drawOwner(
                Graphics2D g2,
                int x,
                int y,
                boolean happy) {

            // Head
            g2.setColor(
                    new Color(
                            245,
                            205,
                            170
                    )
            );

            g2.fillOval(
                    x,
                    y,
                    34,
                    34
            );

            // Hair
            g2.setColor(
                    new Color(
                            85,
                            65,
                            55
                    )
            );

            g2.fillArc(
                    x,
                    y - 3,
                    34,
                    22,
                    0,
                    180
            );

            // Body
            g2.setColor(
                    new Color(
                            115,
                            145,
                            175
                    )
            );

            g2.fillRoundRect(
                    x - 3,
                    y + 31,
                    40,
                    40,
                    10,
                    10
            );

            // Eyes
            g2.setColor(
                    new Color(
                            60,
                            50,
                            45
                    )
            );

            g2.fillOval(
                    x + 8,
                    y + 13,
                    4,
                    4
            );

            g2.fillOval(
                    x + 22,
                    y + 13,
                    4,
                    4
            );

            // Smile
            if (happy) {

                g2.drawArc(
                        x + 9,
                        y + 16,
                        16,
                        10,
                        200,
                        140
                );

            } else {

                g2.drawArc(
                        x + 9,
                        y + 20,
                        16,
                        8,
                        20,
                        140
                );
            }

            // Clipboard
            g2.setColor(
                    new Color(
                            225,
                            205,
                            170
                    )
            );

            g2.fillRoundRect(
                    x + 29,
                    y + 39,
                    20,
                    25,
                    4,
                    4
            );

            g2.setColor(
                    new Color(
                            100,
                            80,
                            65
                    )
            );

            g2.setFont(
                    new Font(
                            "SansSerif",
                            Font.BOLD,
                            8
                    )
            );

            g2.drawString(
                    "OK",
                    x + 32,
                    y + 55
            );
        }

        // =====================================================
        // BUBBLES
        // =====================================================

        private void drawBackgroundBubbles(
                Graphics2D g2) {

            int baseY =
                    400
                    - (int) bubbleOffset;

            drawBubble(
                    g2,
                    1000,
                    baseY,
                    12
            );

            drawBubble(
                    g2,
                    1030,
                    baseY + 20,
                    8
            );

            drawBubble(
                    g2,
                    970,
                    baseY + 35,
                    6
            );
        }

        private void drawBubble(
                Graphics2D g2,
                int x,
                int y,
                int size) {

            g2.setColor(
                    new Color(
                            170,
                            210,
                            215
                    )
            );

            g2.drawOval(
                    x,
                    y,
                    size,
                    size
            );

            g2.fillOval(
                    x + 3,
                    y + 3,
                    2,
                    2
            );
        }

        // =====================================================
        // LITTLE SHIRT DECORATION
        // =====================================================

        private void drawTinyShirt(
                Graphics2D g2,
                int x,
                int y) {

            g2.setColor(
                    new Color(
                            200,
                            155,
                            140
                    )
            );

            Polygon shirt =
                    new Polygon();

            shirt.addPoint(
                    x,
                    y
            );

            shirt.addPoint(
                    x - 7,
                    y + 5
            );

            shirt.addPoint(
                    x - 4,
                    y + 13
            );

            shirt.addPoint(
                    x + 2,
                    y + 10
            );

            shirt.addPoint(
                    x + 8,
                    y + 13
            );

            shirt.addPoint(
                    x + 11,
                    y + 5
            );

            shirt.addPoint(
                    x + 4,
                    y
            );

            g2.fillPolygon(
                    shirt
            );
        }
    }
}