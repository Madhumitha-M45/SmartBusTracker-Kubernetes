package controller;

import java.util.List;
import java.util.Map;

import dto.ETARequest;
import dto.ETAResponse;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import service.ETAService;

@Path("/bus")
@Produces(MediaType.APPLICATION_JSON)
public class Controller {

    private final ETAService etaService = new ETAService();

    @GET
    @Path("/stops")
    public Response searchStops(@QueryParam("query") String query) {
        try {
            System.out.println("=================================");
            System.out.println("GET /bus/stops");
            System.out.println("Query : " + query);
            System.out.println("=================================");

            if (query == null || query.trim().isEmpty()) {
                Map<String, String> errorResp =
                        Map.of("error", "Query is required");
                logResponse(400, errorResp);
                return Response
                        .status(Response.Status.BAD_REQUEST)
                        .entity(errorResp)
                        .build();
            }

            List<String> stops = etaService.searchStops(query);

            System.out.println("Stops found count : " + stops.size());
            logResponse(200, stops);

            return Response.ok(stops).build();

        } catch (Exception e) {
            System.err.println("ERROR IN GET /bus/stops");
            e.printStackTrace();

            Map<String, String> errorResp =
                    Map.of(
                            "error",
                            e.getMessage() != null
                                    ? e.getMessage()
                                    : "Internal server error"
                    );
            logResponse(500, errorResp);
            return Response
                    .status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(errorResp)
                    .build();
        }
    }

    @POST
    @Path("/search")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response searchBus(ETARequest request) {
        try {
            System.out.println("=================================");
            System.out.println("POST /bus/search");
            System.out.println("=================================");

            if (request == null) {
                Map<String, String> errorResp =
                        Map.of("error", "Request body is required");
                logResponse(400, errorResp);
                return Response
                        .status(Response.Status.BAD_REQUEST)
                        .entity(errorResp)
                        .build();
            }

            System.out.println("Boarding Stop    : " + request.getBoardingStop());
            System.out.println("Destination Stop : " + request.getDestinationStop());
            System.out.println("Travel Time      : " + request.getTravelTime());

            if (request.getBoardingStop() == null ||
                    request.getBoardingStop().trim().isEmpty()) {
                Map<String, String> errorResp =
                        Map.of("error", "Boarding stop is required");
                logResponse(400, errorResp);
                return Response
                        .status(Response.Status.BAD_REQUEST)
                        .entity(errorResp)
                        .build();
            }

            if (request.getDestinationStop() == null ||
                    request.getDestinationStop().trim().isEmpty()) {
                Map<String, String> errorResp =
                        Map.of("error", "Destination stop is required");
                logResponse(400, errorResp);
                return Response
                        .status(Response.Status.BAD_REQUEST)
                        .entity(errorResp)
                        .build();
            }

            List<ETAResponse> responses =
                    etaService.calculateETAForAllBuses(request);

            System.out.println("Bus responses count : " + responses.size());
            logResponse(200, responses);

            return Response.ok(responses).build();

        } catch (IllegalArgumentException e) {
            System.err.println("INVALID REQUEST IN POST /bus/search");
            e.printStackTrace();

            Map<String, String> errorResp =
                    Map.of(
                            "error",
                            e.getMessage() != null
                                    ? e.getMessage()
                                    : "Invalid request"
                    );
            logResponse(400, errorResp);
            return Response
                    .status(Response.Status.BAD_REQUEST)
                    .entity(errorResp)
                    .build();

        } catch (Exception e) {
            System.err.println("ERROR IN POST /bus/search");
            e.printStackTrace();

            Map<String, String> errorResp =
                    Map.of(
                            "error",
                            e.getMessage() != null
                                    ? e.getMessage()
                                    : "Internal server error"
                    );
            logResponse(500, errorResp);
            return Response
                    .status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(errorResp)
                    .build();
        }
    }

    private void logResponse(int statusCode, Object responseBody) {
        System.out.println("---------------------------------");
        System.out.println("RESPONSE STATUS CODE : " + statusCode);
        System.out.println("RESPONSE BODY        : " + responseBody);
        System.out.println("=================================");
    }
}
