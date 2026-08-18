package controller;

import java.util.Map;

import dto.DeviceTokenRequest;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import model.DeviceToken;
import repository.DeviceTokenRepository;
import service.BusNotificationService;

@Path("/bus")
@Produces(MediaType.APPLICATION_JSON)
public class DeviceTokenController {

    private final DeviceTokenRepository deviceTokenRepository = new DeviceTokenRepository();
    private final BusNotificationService busNotificationService = new BusNotificationService();

    @POST
    @Path("/device/register")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response registerDevice(DeviceTokenRequest request) {
        try {
            if (request == null) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(Map.of("error", "Request body is required"))
                        .build();
            }

            if (request.getDeviceToken() == null || request.getDeviceToken().trim().isEmpty()) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(Map.of("error", "Device token is required"))
                        .build();
            }

            if (request.getBusId() == null || request.getBusId().trim().isEmpty()) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(Map.of("error", "Bus ID is required"))
                        .build();
            }

            if (request.getBoardingStop() == null || request.getBoardingStop().trim().isEmpty()) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(Map.of("error", "Boarding stop is required"))
                        .build();
            }

            if (request.getDestinationStop() == null || request.getDestinationStop().trim().isEmpty()) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(Map.of("error", "Destination stop is required"))
                        .build();
            }

            DeviceToken token = new DeviceToken();
            token.setDeviceToken(request.getDeviceToken().trim());
            token.setBusId(request.getBusId().trim());
            token.setBoardingStop(request.getBoardingStop().trim());
            token.setDestinationStop(request.getDestinationStop().trim());
            token.setActive(true);
            token.setCompleted(false);

            deviceTokenRepository.save(token);

            return Response.ok(
                    Map.of(
                            "message", "Bus notification subscription received successfully",
                            "busId", token.getBusId(),
                            "boardingStop", token.getBoardingStop(),
                            "destinationStop", token.getDestinationStop()
                    )
            ).build();

        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of("error", e.getMessage() != null ? e.getMessage() : "Internal server error"))
                    .build();
        }
    }

    @POST
    @Path("/notification/test")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response testNotification(Map<String, String> request) {
        try {
            if (request == null || request.get("busId") == null || request.get("busId").trim().isEmpty()) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(Map.of("error", "Bus ID is required"))
                        .build();
            }

            String busId = request.get("busId").trim();

            busNotificationService.notifyBusPassengers(
                    busId,
                    "Smart Bus Tracker",
                    "This is a test notification for your bus."
            );

            return Response.ok(
                    Map.of(
                            "message", "Test notification triggered successfully",
                            "busId", busId
                    )
            ).build();

        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of("error", e.getMessage() != null ? e.getMessage() : "Internal server error"))
                    .build();
        }
    }
}
