<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Bus Tracking Management System</title>
    <link rel="stylesheet" href="css/style.css">
    <link rel="icon" href="data:,">
</head>
<body>

<div class="container">
    <h1>Bus Tracking Management System</h1>

    <!-- Navigation -->
    <div class="tabs">
        <button class="tabButton active" onclick="showTab('bus', event)">Manage Bus</button>
        <button class="tabButton" onclick="showTab('stop', event)">Manage Stop</button>
        <button class="tabButton" onclick="showTab('route', event)">Manage Route</button>
        <button class="tabButton" onclick="showTab('schedule', event)">Manage Schedule</button>
    </div>

    <!-- ================= BUS ================= -->
    <div id="bus" class="tabContent">
        <div class="card">
            <h2>Manage Bus</h2>
            <div class="formGrid">
                <div class="formGroup">
                    <label>Bus ID</label>
                    <input type="text" id="busId">
                </div>
                <div class="formGroup">
                    <label>Bus Number</label>
                    <input type="text" id="busNumber">
                </div>
                <div class="formGroup">
                    <label>Bus Name</label>
                    <input type="text" id="busName">
                </div>
                <div class="formGroup">
                    <label>Bus Type</label>
                    <select id="busType">
                        <option>Express</option>
                        <option>Deluxe</option>
                        <option>Ultra Deluxe</option>
                        <option>AC Sleeper</option>
                    </select>
                </div>
                <div class="formGroup">
                    <label>Route</label>
                    <select id="routeId"></select>
                </div>
            </div>
            <br>
            <button class="btnAdd" onclick="addBus()">Add Bus</button>
            <button class="btnUpdate" onclick="updateBus()">Update Bus</button>
            <button class="btnDelete" onclick="deleteBus()">Delete Bus</button>
            <button class="btnClear" onclick="clearBus()">Clear</button>
        </div>

        <div class="card">
            <h2>Bus List</h2>
            <table>
                <thead>
                    <tr>
                        <th>Bus ID</th>
                        <th>Bus Number</th>
                        <th>Bus Name</th>
                        <th>Bus Type</th>
                        <th>Route</th>
                        <th>Action</th>
                    </tr>
                </thead>
                <tbody id="busTableBody"></tbody>
            </table>
        </div>
    </div>

    <!-- ================= STOP ================= -->
    <div id="stop" class="tabContent" style="display:none;">
        <div class="card">
            <h2>Manage Stop</h2>
            <div class="formGrid">
                <div class="formGroup">
                    <label>Stop ID</label>
                    <input type="text" id="stopId">
                </div>
                <div class="formGroup">
                    <label>Stop Name</label>
                    <input type="text" id="stopName">
                </div>
            </div>
            <br>
            <button class="btnAdd" onclick="addStop()">Add Stop</button>
            <button class="btnUpdate" onclick="updateStop()">Update Stop</button>
            <button class="btnDelete" onclick="deleteStop()">Delete Stop</button>
            <button class="btnClear" onclick="clearStop()">Clear</button>
        </div>

        <div class="card">
            <h2>Stop List</h2>
            <table>
                <thead>
                    <tr>
                        <th>Stop ID</th>
                        <th>Stop Name</th>
                        <th>Action</th>
                    </tr>
                </thead>
                <tbody id="stopTableBody"></tbody>
            </table>
        </div>
    </div>

    <!-- ================= ROUTE ================= -->
  <!-- ================= ROUTE ================= -->
<div id="route" class="tabContent" style="display:none;">

    <div class="card">

        <h2>Manage Route</h2>

        <div class="formGrid">

            <div class="formGroup">
                <label>Route ID</label>
                <input type="text" id="routeInputId">
            </div>

            <div class="formGroup">
                <label>Route Name</label>
                <input type="text" id="routeName">
            </div>

            <div class="formGroup">
                <label>Total Distance (km)</label>
                <input type="number"
                       id="distance"
                       step="0.1"
                       min="0">
            </div>

        </div>

        <br>

        <h3>Select Stops and Enter Distance</h3>

        <table id="routeStopsTable">

            <thead>

                <tr>

                    <th>Select</th>
                    <th>Stop Name</th>
                    <th>Stop Order</th>
                    <th>Distance From Previous Stop (km)</th>

                </tr>

            </thead>

            <tbody id="routeStopsBody"></tbody>

        </table>

        <br>

        <button class="btnAdd" onclick="addRoute()">Add Route</button>
        <button class="btnUpdate" onclick="updateRoute()">Update Route</button>
        <button class="btnDelete" onclick="deleteRoute()">Delete Route</button>
        <button class="btnClear" onclick="clearRoute()">Clear</button>

    </div>

    <div class="card">

        <h2>Route List</h2>

        <table>

            <thead>

                <tr>

                    <th>Route ID</th>
                    <th>Route Name</th>
                    <th>Total Distance</th>
                    <th>Stops</th>
                    <th>Action</th>

                </tr>

            </thead>

            <tbody id="routeTableBody"></tbody>

        </table>

    </div>

</div>
    <!-- ================= SCHEDULE ================= -->
    <div id="schedule" class="tabContent" style="display:none;">
        <div class="card">
            <h2>Manage Schedule</h2>
            <div class="formGrid">
                <div class="formGroup">
                    <label>Schedule ID</label>
                    <input type="text" id="scheduleId">
                </div>
                <div class="formGroup">
                    <label>Bus</label>
                    <select id="scheduleBusId"></select>
                </div>
               <div class="formGroup">
    <label>Route</label>
    <select id="scheduleRouteId"
            onchange="loadSourceDestination()">
    </select>
</div>
<div class="formGroup">
    <label>Source</label>
    <input type="text"
           id="sourceName"
           readonly>
</div>

<div class="formGroup">
    <label>Destination</label>
    <input type="text"
           id="destinationName"
           readonly>
</div>
                <div class="formGroup">
                    <label>Departure Time</label>
                    <input type="time" id="departureTime">
                </div>
                <div class="formGroup">
                    <label>Arrival Time</label>
                    <input type="time" id="arrivalTime">
                </div>
                <div class="formGroup fullWidth">
                    <label>Operating Days</label>
                    <div class="daysCheckboxGroup">
                        <label class="dayCheckboxLabel"><input type="checkbox" name="operatingDays" value="MONDAY"> Monday</label>
                        <label class="dayCheckboxLabel"><input type="checkbox" name="operatingDays" value="TUESDAY"> Tuesday</label>
                        <label class="dayCheckboxLabel"><input type="checkbox" name="operatingDays" value="WEDNESDAY"> Wednesday</label>
                        <label class="dayCheckboxLabel"><input type="checkbox" name="operatingDays" value="THURSDAY"> Thursday</label>
                        <label class="dayCheckboxLabel"><input type="checkbox" name="operatingDays" value="FRIDAY"> Friday</label>
                        <label class="dayCheckboxLabel"><input type="checkbox" name="operatingDays" value="SATURDAY"> Saturday</label>
                        <label class="dayCheckboxLabel"><input type="checkbox" name="operatingDays" value="SUNDAY"> Sunday</label>
                    </div>
                </div>
            </div>
            <br>
            <button class="btnAdd" onclick="addSchedule()">Add Schedule</button>
            <button class="btnUpdate" onclick="updateSchedule()">Update Schedule</button>
            <button class="btnDelete" onclick="deleteSchedule()">Delete Schedule</button>
            <button class="btnClear" onclick="clearSchedule()">Clear</button>
        </div>

        <div class="card">
            <h2>Schedule List</h2>
            <table>
                <thead>
                    <tr>
                        <th>Schedule ID</th>
<th>Bus</th>
<th>Route</th>
<th>Source</th>
<th>Destination</th>
<th>Departure</th>
<th>Arrival</th>
<th>Operating Days</th>
<th>Action</th>
                    </tr>
                </thead>
                <tbody id="scheduleTableBody"></tbody>
            </table>
        </div>
    </div>

</div>

<script src="js/dashboard.js"></script>
</body>
</html>
