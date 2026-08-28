package notification;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import dto.ETAResponse;

public class ETANotificationService {
	private final BusNotificationService busNotificationService = new BusNotificationService();
	private final Map<String, Integer> previousEtaMap = new ConcurrentHashMap<>();
	private final Map<String, Integer> lastNotificationSentMap = new ConcurrentHashMap<>();
	public void checkAndSendNotificationForSubscriber(ETAResponse response, String deviceToken) {
		long currentEta = response.getEtaToBoardingStop();
		if (currentEta <= 0) {
			return;
		}
		String stateKey = response.getBusId().trim() + ":" + deviceToken.trim();
		Integer previousEta = previousEtaMap.get(stateKey);
		if (previousEta == null) {
			previousEtaMap.put(stateKey, (int) currentEta);
			System.out.println("[ETA-NOTIFICATION] First ETA recorded for Bus " + response.getBusId() + ": "
					+ currentEta + " mins");
			return;
		}
		previousEtaMap.put(stateKey, (int) currentEta);
		if (currentEta >= previousEta) {
			return;
		}
		Integer milestone = findCrossedMilestone(previousEta, (int) currentEta);
		if (milestone == null) {
			return;
		}
		Integer lastSent = lastNotificationSentMap.get(stateKey);
		if (lastSent != null && lastSent.equals(milestone)) {
			return;
		}
		String title = "Bus " + response.getBusId() + " Arrival Alert";
		String body = buildMessage(response, milestone);
		boolean sent = busNotificationService.sendSingleNotification(deviceToken, title, body, response.getBusId());
		if (sent) {
			lastNotificationSentMap.put(stateKey, milestone);
			System.out.println("[ETA-NOTIFICATION] Alert sent successfully for " + milestone + " min milestone.");
		}
	}
	private Integer findCrossedMilestone(int previousEta, int currentEta) {
		int[] milestones = { 40, 20, 10, 9, 8, 7, 6, 5, 4, 3, 2, 1 };
		for (int milestone : milestones) {
			if (previousEta > milestone && currentEta <= milestone) {
				return milestone;
			}
		}
		return null;
	}
	private String buildMessage(ETAResponse response, int etaMinutes) {
		String stop = response.getBoardingStop();
		if (stop == null || stop.trim().isEmpty()) {
			stop = "your boarding stop";
		}
		if (etaMinutes == 1) {
			return "Bus " + response.getBusId() + " will reach " + stop + " in about 1 minute.";
		}
		return "Bus " + response.getBusId() + " will reach " + stop + " in about " + etaMinutes + " minutes.";
	}
	public void clearSubscriberState(String busId, String deviceToken) {
		if (busId == null || deviceToken == null) {
			return;
		}
		String stateKey = busId.trim() + ":" + deviceToken.trim();
		previousEtaMap.remove(stateKey);
		lastNotificationSentMap.remove(stateKey);
		System.out.println("[ETA-NOTIFICATION] Cleared state for Bus: " + busId);
	}
}