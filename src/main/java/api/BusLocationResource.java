package api;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import model.BusLocation;
import repository.BusLocationRepository;
@Path("/bus-location")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class BusLocationResource {
	private final BusLocationRepository repository;
	public BusLocationResource() {
		repository = new BusLocationRepository();
	}
	@POST
	@Path("/update")
	public Response updateLocation(BusLocation location) {
		try {
			repository.saveOrUpdateBusLocation(location);
			return Response.ok("{\"message\":\"Bus location stored successfully\"}").build();
		} catch (IllegalArgumentException e) {
			return Response.status(Response.Status.BAD_REQUEST).entity("{\"error\":\"" + e.getMessage() + "\"}")
					.build();
		} catch (Exception e) {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
					.entity("{\"error\":\"Failed to store bus location\"}").build();
		}
	}
}