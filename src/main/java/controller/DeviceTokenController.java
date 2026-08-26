package controller;

import dto.DeviceTokenRequest;

import jakarta.ws.rs.Consumes;

import jakarta.ws.rs.POST;

import jakarta.ws.rs.Path;

import jakarta.ws.rs.Produces;

import jakarta.ws.rs.core.MediaType;

import jakarta.ws.rs.core.Response;

import model.DeviceToken;

import repository.DeviceTokenRepository;
 
@Path("/bus/device")

@Produces(MediaType.APPLICATION_JSON)

@Consumes(MediaType.APPLICATION_JSON)

public class DeviceTokenController {

    private final DeviceTokenRepository deviceTokenRepository = new DeviceTokenRepository();
 
    @POST

    @Path("/register")

    public Response registerDevice(DeviceTokenRequest request) {

        DeviceToken token = new DeviceToken();

        token.setDeviceToken(request.getDeviceToken());

        token.setBusId(request.getBusId());

        token.setBoardingStop(request.getBoardingStop());

        token.setDestinationStop(request.getDestinationStop());

        token.setActive(true);

        token.setCompleted(false);
 
        deviceTokenRepository.save(token);

        return Response.ok("Notification subscription successful").build();

    }

    @POST

    @Path("/unsubscribe")

    public Response unsubscribeDevice(DeviceTokenRequest request) {
 
        deviceTokenRepository.deactivateToken(

                request.getDeviceToken(),

                request.getBusId()

        );
 
        return Response.ok("Notification unsubscribed successfully").build();

    }

}
 