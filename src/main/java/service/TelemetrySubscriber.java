package service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import repository.BusLocationRepository;
import model.BusLocation;

public class TelemetrySubscriber implements MqttCallback {

    private static final String BROKER_URI = "tcp://192.168.1.171:1883";
    private static final String TOPIC = "fleet/bus/locations";
    private static final String CLIENT_ID = "BusTrackerSubscriber";

    private final BusLocationRepository busLocationRepository;
    private final ObjectMapper objectMapper;
    private MqttClient mqttClient;
    private long receivedPingCount = 0;
    private volatile boolean running = false;

    public TelemetrySubscriber() {
        this.busLocationRepository = new BusLocationRepository();
        this.objectMapper = new ObjectMapper();
    }

    public TelemetrySubscriber(BusLocationRepository busLocationRepository) {
        this.busLocationRepository = busLocationRepository;
        this.objectMapper = new ObjectMapper();
    }

    public void start() {
        try {
            mqttClient = new MqttClient(BROKER_URI, CLIENT_ID, new MemoryPersistence());
            mqttClient.setCallback(this);

            MqttConnectOptions connectOptions = new MqttConnectOptions();
            connectOptions.setCleanSession(true);
            connectOptions.setAutomaticReconnect(true);
            connectOptions.setConnectionTimeout(10);
            connectOptions.setKeepAliveInterval(20);
            connectOptions.setMaxReconnectDelay(30);

            mqttClient.connect(connectOptions);
            mqttClient.subscribe(TOPIC);

            running = true;
            System.out.println("[SUBSCRIBER] Connected to " + BROKER_URI + " and listening on topic '" + TOPIC + "'");
        } catch (MqttException e) {
            System.err.println("[SUBSCRIBER] Failed to start: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void stop() {
        running = false;
        try {
            if (mqttClient != null && mqttClient.isConnected()) {
                mqttClient.unsubscribe(TOPIC);
                mqttClient.disconnect();
            }
            if (mqttClient != null) {
                mqttClient.close();
            }
        } catch (MqttException e) {
            System.err.println("[SUBSCRIBER] Error during shutdown: " + e.getMessage());
        }
        System.out.println("[SUBSCRIBER] Stopped. Total pings received: " + receivedPingCount);
    }

    @Override
    public void connectionLost(Throwable cause) {
        System.err.println("[SUBSCRIBER] Connection lost: " + cause.getMessage());
        System.out.println("[SUBSCRIBER] Automatic reconnect is enabled — waiting for reconnection...");
    }

    @Override
    public void messageArrived(String topic, MqttMessage mqttMessage) {
        receivedPingCount++;
        try {
            String payload = new String(mqttMessage.getPayload());
            System.out.println("[SUBSCRIBER] Raw message on '" + topic + "': " + payload);

            BusLocation busLocation = parseFromJson(payload);
            if (busLocation == null) {
                busLocation = parseFromCsv(payload);
            }

            if (busLocation != null) {
                busLocationRepository.saveOrUpdateBusLocation(busLocation);
                printReceivedPing(busLocation);
            } else {
                System.err.println("[SUBSCRIBER] Could not parse message: " + payload);
            }
        } catch (Exception e) {
            System.err.println("[SUBSCRIBER] Error processing message: " + e.getMessage());
        }
    }

    @Override
    public void deliveryComplete(org.eclipse.paho.client.mqttv3.IMqttDeliveryToken token) {
    }

    private BusLocation parseFromJson(String payload) {
        try {
            JsonNode node = objectMapper.readTree(payload);
            BusLocation loc = new BusLocation();
            loc.setBusId(getJsonText(node, "busId"));
            loc.setScheduleId(getJsonText(node, "scheduleId"));
            loc.setRouteId(getJsonText(node, "routeId"));
            loc.setStatus(getJsonText(node, "status"));
            loc.setCurrentStopId(getJsonText(node, "currentStopId"));
            loc.setCurrentStopName(getJsonText(node, "currentStopName"));
            loc.setNextStopId(getJsonText(node, "nextStopId"));
            loc.setNextStopName(getJsonText(node, "nextStopName"));
            loc.setLastUpdated(getJsonText(node, "lastUpdated"));
            loc.setEta(getJsonText(node, "eta"));
            loc.setSpeed(getJsonDouble(node, "speed"));
            loc.setDistanceCovered(getJsonDouble(node, "distanceCovered"));
            loc.setDistanceRemaining(getJsonDouble(node, "distanceRemaining"));
            loc.setProgress(getJsonDouble(node, "progress"));
            loc.setDelayMinutes(getJsonDouble(node, "delayMinutes"));
            return loc.getBusId() != null ? loc : null;
        } catch (Exception e) {
            return null;
        }
    }

    private String getJsonText(JsonNode node, String field) {
        JsonNode value = node.get(field);
        return value != null && !value.isNull() ? value.asText() : "";
    }

    private double getJsonDouble(JsonNode node, String field) {
        JsonNode value = node.get(field);
        return value != null && !value.isNull() ? value.asDouble() : 0.0;
    }

    private BusLocation parseFromCsv(String payload) {
        try {
            String[] f = payload.split(",", -1);
            if (f.length < 12) {
                return null;
            }
            return new BusLocation(
                    f[0].trim(),
                    Double.parseDouble(f[4].trim()),
                    "",
                    f[8].trim(),
                    "",
                    f[9].trim(),
                    f[10].trim(),
                    f[1].trim(),
                    f[2].trim(),
                    f[3].trim(),
                    Double.parseDouble(f[5].trim()),
                    Double.parseDouble(f[6].trim()),
                    Double.parseDouble(f[7].trim()),
                    0.0,
                    f[11].trim());
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
            return null;
        }
    }

    private void printReceivedPing(BusLocation busLocation) {
        System.out.printf("[PING #%-6d] Bus %-10s | %-8s | %6.2f km | %5.1f%% | %s -> %s | ETA %s%n",
                receivedPingCount,
                busLocation.getBusId(),
                busLocation.getStatus(),
                busLocation.getDistanceCovered(),
                busLocation.getProgress(),
                busLocation.getCurrentStopName(),
                busLocation.getNextStopName(),
                busLocation.getEta());
    }

    public boolean isRunning() {
        return running;
    }

    public long getReceivedPingCount() {
        return receivedPingCount;
    }

    public static void main(String[] args) {
        TelemetrySubscriber subscriber = new TelemetrySubscriber();
        Runtime.getRuntime().addShutdownHook(new Thread(subscriber::stop));
        subscriber.start();
        System.out.println("[SUBSCRIBER] Running standalone. Press Ctrl+C to stop.");
    }
}
