package api;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.servlet.ServletContext;
import model.Bus;
import model.BusLocation;
import model.Route;
import model.RouteStop;
import model.Schedule;
import model.Stop;
import service.AdminService;
import simulator.SimulationManager;
@Path("/admin")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AdminResource {
    private final AdminService adminService = new AdminService();
    @Context
    private ServletContext servletContext;
    @GET
    @Path("/bus")
    public Response getAllBuses() {
        return Response.ok(adminService.getAllBuses()).build();
    }
    @GET
    @Path("/bus/{busId}")
    public Response getBusById(
            @PathParam("busId") String busId) {
        Bus bus = adminService.getBusById(busId);
        if (bus == null) {
            return Response.status(Response.Status.NOT_FOUND ).entity("Bus Not Found").build();
        }
        return Response.ok(bus).build();
    }
    @POST
    @Path("/bus")
    public Response addBus(Bus bus) {
        try {
            adminService.addBus(bus);
            refreshAllData();
            return Response.status(Response.Status.CREATED ).entity("Bus Added Successfully").build();
        } catch (IllegalArgumentException e) {
            return Response.status( Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }
    @PUT
    @Path("/bus")
    public Response updateBus(Bus bus) {
        try {
            adminService.updateBus(bus);
            refreshAllData();
            return Response.ok("Bus Updated Successfully").build();
        } catch (IllegalArgumentException e) {
            return Response.status( Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }
    @DELETE
    @Path("/bus/{busId}")
    public Response deleteBus(
            @PathParam("busId") String busId) {
        try {
            adminService.deleteBus(busId);
            refreshAllData();
            return Response.ok("Bus Deleted Successfully").build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }
    @DELETE
    @Path("/route-stop/{id}")
    public Response deleteRouteStop(@PathParam("id") String id) {
        try {
            adminService.deleteRouteStopById(id); 
            return Response.ok("Route Stop Deleted Successfully").build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }
    @GET
    @Path("/stop")
    public Response getAllStops() {
        return Response.ok(adminService.getAllStops()).build();
    }
    @GET
    @Path("/stop/{stopId}")
    public Response getStopById(
            @PathParam("stopId") String stopId) {
        Stop stop = adminService.getStopById(stopId);
        if (stop == null) {
            return Response.status(Response.Status.NOT_FOUND).entity("Stop Not Found").build();
        }
        return Response.ok(stop).build();
    }
    @POST
    @Path("/stop")
    public Response addStop(Stop stop) {
        try {
            adminService.addStop(stop);
            return Response.status(Response.Status.CREATED).entity("Stop Added Successfully").build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }
    @PUT
    @Path("/stop")
    public Response updateStop(Stop stop) {
        try {
            adminService.updateStop(stop);
            return Response.ok("Stop Updated Successfully").build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }
    @DELETE
    @Path("/stop/{stopId}")
    public Response deleteStop(
            @PathParam("stopId") String stopId) {
        try {
            adminService.deleteStop(stopId);
            return Response.ok("Stop Deleted Successfully").build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }
    @GET
    @Path("/route")
    public Response getAllRoutes() {
        return Response.ok(adminService.getAllRoutes()).build();
    }
    @GET
    @Path("/route/{routeId}")
    public Response getRouteById(
            @PathParam("routeId") String routeId) {
        Route route = adminService.getRouteById(routeId);
        if (route == null) {
            return Response.status(Response.Status.NOT_FOUND).entity("Route Not Found").build();
        }
        return Response.ok(route).build();
    }
    @POST
    @Path("/route")
    public Response addRoute(Route route) {
        try {
            adminService.addRoute(route);
            refreshAllData();
            return Response.status(Response.Status.CREATED).entity("Route Added Successfully").build();
        } catch (IllegalArgumentException e) {
            return Response.status( Response.Status.BAD_REQUEST ).entity(e.getMessage()).build();
        }
    }
    @PUT
    @Path("/route")
    public Response updateRoute(Route route) {
        try {
            adminService.updateRoute(route);
            refreshAllData();
            return Response.ok("Route Updated Successfully").build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }
    @DELETE
    @Path("/route/{routeId}")
    public Response deleteRoute(
            @PathParam("routeId") String routeId) {
        try {
            adminService.deleteRoute(routeId);
            refreshAllData();
            return Response.ok("Route Deleted Successfully").build();
        } catch (IllegalArgumentException e) {
            return Response.status( Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }
    @GET
    @Path("/route-stop")
    public Response getAllRouteStops() {
        return Response.ok(adminService.getAllRouteStops()).build();
    }
    @GET
    @Path("/route-stop/{routeId}")
    public Response getRouteStopsByRouteId(
            @PathParam("routeId") String routeId) {
        return Response.ok(adminService.getRouteStopsByRouteId(routeId)).build();
    }
    @POST
    @Path("/route-stop")
    public Response addRouteStop(RouteStop routeStop) {
        try {
            adminService.addRouteStop(routeStop);
            refreshAllData();
            return Response.status(Response.Status.CREATED).entity( "Route Stop Added Successfully").build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }
    @PUT
    @Path("/route-stop")
    public Response updateRouteStop(RouteStop routeStop) {
        try {
            adminService.updateRouteStop(routeStop);
            refreshAllData();
            return Response.ok("Route Stop Updated Successfully").build();
        } catch (IllegalArgumentException e) {
            return Response.status( Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }
    @DELETE
    @Path("/route-stop/route/{routeId}")
    public Response deleteRouteStops(
            @PathParam("routeId") String routeId) {
        try {
            adminService.deleteRouteStopsByRouteId(routeId);
            refreshAllData();
            return Response.ok("Route Stops Deleted Successfully").build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }
    @GET
    @Path("/schedule")
    public Response getAllSchedules() {
        return Response.ok(adminService.getAllSchedules() ).build();
    }
    @GET
    @Path("/schedule/{scheduleId}")
    public Response getScheduleById(
            @PathParam("scheduleId") String scheduleId) {
        Schedule schedule = adminService.getScheduleById(scheduleId);
        if (schedule == null) {
            return Response.status(Response.Status.NOT_FOUND).entity("Schedule Not Found").build();
        }
        return Response.ok(schedule).build();
    }
    @POST
    @Path("/schedule")
    public Response addSchedule(Schedule schedule) {
        try {
            adminService.addSchedule(schedule);
            refreshAllData();
            return Response.status(Response.Status.CREATED).entity("Schedule Added Successfully").build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }
    @PUT
    @Path("/schedule")
    public Response updateSchedule(Schedule schedule) {
        try {
            adminService.updateSchedule(schedule);
            refreshAllData();
            return Response.ok("Schedule Updated Successfully").build();
        } catch (IllegalArgumentException e) {
            return Response.status( Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }
    @DELETE
    @Path("/schedule/{scheduleId}")
    public Response deleteSchedule(
            @PathParam("scheduleId") String scheduleId) {
        try {
            adminService.deleteSchedule(scheduleId);
            refreshAllData();
            return Response.ok("Schedule Deleted Successfully").build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }
    @GET
    @Path("/location")
    public Response getAllBusLocations() {
        return Response.ok( adminService.getAllBusLocations()).build();
    }
    @GET
    @Path("/location/{busId}")
    public Response getBusLocationByBusId(
            @PathParam("busId") String busId) {
        BusLocation location =adminService.getBusLocationByBusId(busId);
        if (location == null) {
            return Response.status( Response.Status.NOT_FOUND).entity("Bus Location Not Found").build();
        }
        return Response.ok(location).build();
    }
    @POST
    @Path("/location")
    public Response addBusLocation(BusLocation location) {
        try {
            adminService.addBusLocation(location);
            return Response.status(Response.Status.CREATED).entity("Bus Location Added Successfully").build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }
    @PUT
    @Path("/location")
    public Response updateBusLocation(BusLocation location) {
        try {
            adminService.updateBusLocation(location);
            return Response.ok("Bus Location Updated Successfully").build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }
    @DELETE
    @Path("/location/{busId}")
    public Response deleteBusLocation(
            @PathParam("busId") String busId) {
        try {
            adminService.deleteBusLocation(busId);
            return Response.ok("Bus Location Deleted Successfully").build();
        } catch (IllegalArgumentException e) {
            return Response.status( Response.Status.BAD_REQUEST  ).entity(e.getMessage()).build();
        }
    }
    private void refreshAllData() {
        Object manager = servletContext.getAttribute("simulationManager");
        if (manager instanceof SimulationManager) {
            ((SimulationManager) manager).refreshAllData();
        }
    }
}