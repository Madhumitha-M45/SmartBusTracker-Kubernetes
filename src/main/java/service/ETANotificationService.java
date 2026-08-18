package service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import dto.ETAResponse;

public class ETANotificationService {

    private final BusNotificationService busNotificationService =
            new BusNotificationService();

    private final Map<String, Integer> previousEtaMap =
            new ConcurrentHashMap<>();

    private final Map<String, Integer> lastNotificationSentMap =
            new ConcurrentHashMap<>();

    private final Map<String, Object> notificationLocks =
            new ConcurrentHashMap<>();

    public void checkAndSendNotificationForSubscriber(
            ETAResponse response,
            String deviceToken) {

        if (response == null) {
            System.out.println("[ETA-NOTIFICATION] Response is null.");
            return;
        }

        if (deviceToken == null || deviceToken.trim().isEmpty()) {
            System.out.println("[ETA-NOTIFICATION] Device token is empty.");
            return;
        }

        String busId = response.getBusId();

        if (busId == null || busId.trim().isEmpty()) {
            System.out.println("[ETA-NOTIFICATION] Bus ID is empty.");
            return;
        }

        String boardingEta = response.getBoardingEta();

        if (boardingEta == null || boardingEta.trim().isEmpty()) {
            System.out.println("[ETA-NOTIFICATION] Boarding ETA is empty.");
            return;
        }

        int currentEta = extractMinutes(boardingEta);

        System.out.println(
                "[ETA-NOTIFICATION] Bus: " + busId
                        + " | Boarding Stop: " + response.getBoardingStop()
                        + " | ETA: " + boardingEta
                        + " | Minutes: " + currentEta
        );

        if (currentEta <= 0) {
            System.out.println("[ETA-NOTIFICATION] No notification. ETA = " + currentEta);
            return;
        }

        String stateKey =
                busId.trim() + ":" + deviceToken.trim();

        Object lock =
                notificationLocks.computeIfAbsent(stateKey, key -> new Object());

        synchronized (lock) {

            Integer previousEta = previousEtaMap.get(stateKey);

            if (previousEta == null) {
                previousEtaMap.put(stateKey, currentEta);
                System.out.println(
                        "[ETA-NOTIFICATION] First ETA recorded."
                                + " | Bus: " + busId
                                + " | ETA: " + currentEta + " mins"
                );
                return;
            }

            previousEtaMap.put(stateKey, currentEta);

            if (currentEta >= previousEta) {
                System.out.println(
                        "[ETA-NOTIFICATION] ETA did not decrease."
                                + " | Previous: " + previousEta
                                + " | Current: " + currentEta
                );
                return;
            }

            Integer crossedMilestone =
                    findCrossedMilestone(previousEta, currentEta);

            if (crossedMilestone == null) {
                System.out.println(
                        "[ETA-NOTIFICATION] No notification milestone crossed."
                                + " | Previous: " + previousEta
                                + " | Current: " + currentEta
                );
                return;
            }

            Integer lastSent = lastNotificationSentMap.get(stateKey);

            if (lastSent != null && lastSent == crossedMilestone) {
                System.out.println(
                        "[ETA-NOTIFICATION] Milestone already sent."
                                + " | Bus: " + busId
                                + " | Milestone: " + crossedMilestone
                );
                return;
            }

            String title =
                    "Bus " + busId + " Arrival Alert";

            String body =
                    buildMessage(response, crossedMilestone);

            System.out.println(
                    "[ETA-NOTIFICATION] Sending notification..."
                            + " | Bus: " + busId
                            + " | Stop: " + response.getBoardingStop()
                            + " | Milestone: " + crossedMilestone
            );

            boolean sent =
                    busNotificationService.sendSingleNotification(
                            deviceToken,
                            title,
                            body,
                            busId
                    );

            if (sent) {
                lastNotificationSentMap.put(stateKey, crossedMilestone);
                System.out.println(
                        "[ETA-NOTIFICATION] SUCCESS"
                                + " | Bus: " + busId
                                + " | Stop: " + response.getBoardingStop()
                                + " | Notification: " + crossedMilestone + " minutes"
                );
            } else {
                System.err.println(
                        "[ETA-NOTIFICATION] FAILED"
                                + " | Bus: " + busId
                                + " | Milestone: " + crossedMilestone
                );
            }
        }
    }

    private Integer findCrossedMilestone(
            int previousEta,
            int currentEta) {

        int[] majorMilestones = {40, 20};

        for (int milestone : majorMilestones) {
            if (previousEta > milestone && currentEta <= milestone) {
                return milestone;
            }
        }

        for (int milestone = 10; milestone >= 1; milestone--) {
            if (previousEta > milestone && currentEta <= milestone) {
                return milestone;
            }
        }

        return null;
    }

    private String buildMessage(ETAResponse response, int etaMinutes) {
        String boardingStop = response.getBoardingStop();

        if (boardingStop == null || boardingStop.trim().isEmpty()) {
            boardingStop = "your boarding stop";
        }

        if (etaMinutes == 1) {
            return "Bus " + response.getBusId()
                    + " will reach " + boardingStop
                    + " in about 1 minute.";
        }

        return "Bus " + response.getBusId()
                + " will reach " + boardingStop
                + " in about " + etaMinutes + " minutes.";
    }

    public void clearSubscriberState(String busId, String deviceToken) {
        if (busId == null || deviceToken == null) {
            return;
        }

        String stateKey =
                busId.trim() + ":" + deviceToken.trim();

        previousEtaMap.remove(stateKey);
        lastNotificationSentMap.remove(stateKey);
        notificationLocks.remove(stateKey);

        System.out.println(
                "[ETA-NOTIFICATION] Subscriber state cleared."
                        + " | Bus: " + busId
        );
    }

    private int extractMinutes(String eta) {
        try {
            if (eta == null || eta.trim().isEmpty()) {
                return -1;
            }

            String value = eta.trim();

            if (value.equals("arrived") || value.equals("reached")) {
                return 0;
            }

            int totalMinutes = 0;

            if (value.contains("hr") || value.contains("hour")) {
                value = value.replace("hours", "hr")
                        .replace("hour", "hr");

                String hoursPart =
                        value.substring(0, value.indexOf("hr"))
                                .replaceAll("[^0-9]", "");

                if (!hoursPart.isEmpty()) {
                    totalMinutes += Integer.parseInt(hoursPart) * 60;
                }

                if (value.contains("min")) {
                    String minsPart =
                            value.substring(value.indexOf("hr") + 2)
                                    .replaceAll("[^0-9]", "");

                    if (!minsPart.isEmpty()) {
                        totalMinutes += Integer.parseInt(minsPart);
                    }
                }

                return totalMinutes;
            }

            String number = value.replaceAll("[^0-9]", "");

            if (number.isEmpty()) {
                return -1;
            }

            return Integer.parseInt(number);

        } catch (Exception e) {
            System.err.println("[ETA-NOTIFICATION] Unable to parse ETA: " + eta);
            return -1;
        }
    }
}
