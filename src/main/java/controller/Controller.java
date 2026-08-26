package controller;
import java.util.List;

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

        System.out.println("=================================");
        System.out.println("GET /bus/stops");
        System.out.println("Query : " + query);
        System.out.println("=================================");

        List<String> stops = etaService.searchStops(query);

        System.out.println("Stops found count : " + stops.size());

        return Response.ok(stops).build();
    }

    @POST
    @Path("/search")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response searchBus(ETARequest request) {

        System.out.println("=================================");
        System.out.println("POST /bus/search");
        System.out.println("=================================");

        System.out.println("Boarding Stop    : " + request.getBoardingStop());
        System.out.println("Destination Stop : " + request.getDestinationStop());
        System.out.println("Travel Time      : " + request.getTravelTime());

        List<ETAResponse> responses =
                etaService.calculateETAForAllBuses(request);

        System.out.println("Bus responses count : " + responses.size());

        return Response.ok(responses).build();
    }
}