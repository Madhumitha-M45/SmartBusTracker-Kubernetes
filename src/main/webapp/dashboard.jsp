<%@ page language="java"
    contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<!DOCTYPE html>
<html lang="en">

<head>

    <meta charset="UTF-8">

    <title>Bus Tracking Management System</title>

    <style>

        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
            font-family: Arial, Helvetica, sans-serif;
        }

        body {
            background: #f4f6f9;
            padding: 20px;
        }

        .container {
            width: 95%;
            margin: auto;
        }

        h1 {
            text-align: center;
            margin-bottom: 20px;
            color: #2c3e50;
        }

        .tabs {
            display: flex;
            gap: 10px;
            margin-bottom: 20px;
            flex-wrap: wrap;
        }

        .tabButton {
            padding: 12px 20px;
            border: none;
            background: #ddd;
            cursor: pointer;
            border-radius: 5px;
            font-weight: bold;
        }

        .tabButton.active {
            background: #3498db;
            color: white;
        }

        .tabContent {
            display: none;
            background: white;
            padding: 20px;
            border-radius: 8px;
            box-shadow: 0 2px 8px rgba(0,0,0,0.08);
        }

        .tabContent.active {
            display: block;
        }

        .formGrid {
            display: grid;
            grid-template-columns: repeat(3, 1fr);
            gap: 15px;
            margin-bottom: 20px;
        }

        .formGroup {
            display: flex;
            flex-direction: column;
        }

        .formGroup label {
            font-weight: bold;
            margin-bottom: 6px;
            color: #34495e;
        }

        input,
        select {
            padding: 9px;
            border: 1px solid #ccc;
            border-radius: 4px;
            font-size: 14px;
        }

        button {
            cursor: pointer;
        }

        .btn {
            padding: 10px 16px;
            border: none;
            border-radius: 5px;
            margin-right: 5px;
            color: white;
            font-weight: bold;
        }

        .btnAdd {
            background: #27ae60;
        }

        .btnUpdate {
            background: #2980b9;
        }

        .btnDelete {
            background: #c0392b;
        }

        .btnClear {
            background: #7f8c8d;
        }

        .btnTableEdit {
            padding: 6px 10px;
            background: #2980b9;
            color: white;
            border: none;
            border-radius: 4px;
        }

        .tableContainer {
            overflow-x: auto;
            margin-top: 20px;
        }

        table {
            width: 100%;
            border-collapse: collapse;
            background: white;
        }

        th,
        td {
            border: 1px solid #ddd;
            padding: 10px;
            text-align: center;
        }

        th {
            background: #34495e;
            color: white;
        }

        tr:nth-child(even) {
            background: #f8f9fa;
        }

        .emptyCell {
            text-align: center;
            color: #777;
            padding: 20px;
        }

        .message {
            display: none;
            padding: 12px;
            margin-bottom: 15px;
            border-radius: 5px;
            font-weight: bold;
        }

        .message.success {
            background: #d4edda;
            color: #155724;
        }

        .message.error {
            background: #f8d7da;
            color: #721c24;
        }

        .timeRow {
            display: flex;
            gap: 8px;
            margin-bottom: 8px;
            align-items: center;
        }

        .timeRow input {
            flex: 1;
        }

        .daysContainer {
            display: flex;
            gap: 12px;
            flex-wrap: wrap;
        }

        .daysContainer label {
            font-weight: normal;
        }

        /* ==============================
           ROUTE STOP
        ============================== */

        .routeStopHeader {
            margin-bottom: 15px;
        }

        .routeStopHeader h2 {
            color: #2c3e50;
            margin-bottom: 5px;
        }

        .routeStopHeader p {
            color: #666;
        }

        .routeStopTable th {
            background: #2c3e50;
        }

        .routeNameCell {
            font-weight: bold;
            color: #2c3e50;
        }

        .stopNameCell {
            text-align: left;
            font-weight: 500;
        }

        .routeStopCheckbox {
            width: 18px;
            height: 18px;
            cursor: pointer;
        }

        .routeStopNumber {
            width: 90px;
            padding: 7px;
        }

        .routeStopDistance {
            width: 110px;
            padding: 7px;
        }

        .routeGroupRow td {
            background: #ecf0f1;
            font-weight: bold;
            text-align: left;
            color: #2c3e50;
        }

        .routeStopActions {
            display: flex;
            gap: 8px;
            margin-top: 15px;
        }

        @media (max-width: 900px) {

            .formGrid {
                grid-template-columns: 1fr;
            }

        }

    </style>

</head>

<body>

<div class="container">

    <h1>Bus Tracking Management System</h1>

    <div id="message" class="message"></div>

    <!-- =====================================================
         TABS
    ====================================================== -->

    <div class="tabs">

        <button
            class="tabButton active"
            onclick="showTab('bus', event)">
            Manage Bus
        </button>

        <button
            class="tabButton"
            onclick="showTab('stop', event)">
            Manage Stop
        </button>

        <button
            class="tabButton"
            onclick="showTab('route', event)">
            Manage Route
        </button>

        <button
            class="tabButton"
            onclick="showTab('routeStop', event)">
            Manage Route Stop
        </button>

        <button
            class="tabButton"
            onclick="showTab('schedule', event)">
            Manage Schedule
        </button>

    </div>


    <!-- =====================================================
         BUS
    ====================================================== -->

    <div id="bus" class="tabContent active">

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
                <input type="text" id="busType">
            </div>

            <div class="formGroup">
                <label>Status</label>
                <input type="text" id="busStatus">
            </div>

            <div class="formGroup">
                <label>Available From</label>
                <input type="text" id="availableFrom">
            </div>

        </div>

        <button class="btn btnAdd" onclick="addBus()">Add</button>
        <button class="btn btnUpdate" onclick="updateBus()">Update</button>
        <button class="btn btnDelete" onclick="deleteBus()">Delete</button>
        <button class="btn btnClear" onclick="clearBus()">Clear</button>


        <div class="tableContainer">

            <table>

                <thead>
                    <tr>
                        <th>Bus ID</th>
                        <th>Bus Number</th>
                        <th>Bus Name</th>
                        <th>Bus Type</th>
                        <th>Status</th>
                        <th>Available From</th>
                        <th>Action</th>
                    </tr>
                </thead>

                <tbody id="busTableBody"></tbody>

            </table>

        </div>

    </div>


    <!-- =====================================================
         STOP
    ====================================================== -->

    <div id="stop" class="tabContent">

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

        <button class="btn btnAdd" onclick="addStop()">Add</button>
        <button class="btn btnUpdate" onclick="updateStop()">Update</button>
        <button class="btn btnDelete" onclick="deleteStop()">Delete</button>
        <button class="btn btnClear" onclick="clearStop()">Clear</button>


        <div class="tableContainer">

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


    <!-- =====================================================
         ROUTE
    ====================================================== -->

    <div id="route" class="tabContent">

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
                <label>Source</label>
                <input type="text" id="source">
            </div>

            <div class="formGroup">
                <label>Destination</label>
                <input type="text" id="destination">
            </div>

            <div class="formGroup">
                <label>Total Distance</label>
                <input type="number" id="distance" step="0.01">
            </div>

        </div>

        <button class="btn btnAdd" onclick="addRoute()">Add</button>
        <button class="btn btnUpdate" onclick="updateRoute()">Update</button>
        <button class="btn btnDelete" onclick="deleteRoute()">Delete</button>
        <button class="btn btnClear" onclick="clearRoute()">Clear</button>


        <div class="tableContainer">

            <table>

                <thead>
                    <tr>
                        <th>Route ID</th>
                        <th>Route Name</th>
                        <th>Source</th>
                        <th>Destination</th>
                        <th>Distance</th>
                        <th>Action</th>
                    </tr>
                </thead>

                <tbody id="routeTableBody"></tbody>

            </table>

        </div>

    </div>


    <!-- =====================================================
         ROUTE STOP
    ====================================================== -->

    <div id="routeStop" class="tabContent">

        <div class="routeStopHeader">

            <h2>Route Stop Management</h2>

            <p>
                Select the stops required for each route.
                Stop order and distance are maintained separately
                for every route.
            </p>

        </div>


        <!-- Hidden fields used by JS -->

        <input
            type="hidden"
            id="routeStopId">

        <input
            type="hidden"
            id="rsRouteId">

        <input
            type="hidden"
            id="rsStopId">

        <input
            type="hidden"
            id="rsStopName">

        <input
            type="hidden"
            id="rsStopOrder">

        <input
            type="hidden"
            id="rsDistance">


        <div class="tableContainer">

            <table class="routeStopTable">

                <thead>

                    <tr>

                        <th>Route</th>

                        <th>Select</th>

                        <th>Stop ID</th>

                        <th>Stop Name</th>

                        <th>Stop Order</th>

                        <th>Distance From Previous</th>

                    </tr>

                </thead>

                <tbody id="routeStopTableBody"></tbody>

            </table>

        </div>


        <div class="routeStopActions">

            <button
                class="btn btnUpdate"
                onclick="saveAllRouteStops()">
                Save Selected Stops
            </button>

            <button
                class="btn btnDelete"
                onclick="deleteSelectedRouteStops()">
                Delete Unselected
            </button>

            <button
                class="btn btnClear"
                onclick="loadRouteStopMatrix()">
                Refresh
            </button>

        </div>

    </div>


    <!-- =====================================================
         SCHEDULE
    ====================================================== -->

    <div id="schedule" class="tabContent">

        <h2>Manage Schedule</h2>

        <div class="formGrid">

            <div class="formGroup">

                <label>Schedule ID</label>

                <input
                    type="text"
                    id="scheduleId">

            </div>


            <div class="formGroup">

                <label>Route</label>

                <select
                    id="scheduleRouteId"
                    onchange="loadSourceDestination()">

                    <option value="">
                        -- Select Route --
                    </option>

                </select>

            </div>


            <div class="formGroup">

                <label>Source</label>

                <input
                    type="text"
                    id="sourceName"
                    readonly>

            </div>


            <div class="formGroup">

                <label>Destination</label>

                <input
                    type="text"
                    id="destinationName"
                    readonly>

            </div>

        </div>


        <h3>Departure Times</h3>

        <div id="departureTimesContainer">

            <div class="timeRow">

                <input
                    type="time"
                    class="departureTime">

                <button
                    type="button"
                    class="btn btnDelete"
                    onclick="removeDepartureTime(this)">
                    Remove
                </button>

            </div>

        </div>

        <button
            type="button"
            class="btn btnAdd"
            onclick="addDepartureTime()">
            Add Departure Time
        </button>


        <h3 style="margin-top:20px;">
            Arrival Times
        </h3>

        <div id="arrivalTimesContainer">

            <div class="timeRow">

                <input
                    type="time"
                    class="arrivalTime">

                <button
                    type="button"
                    class="btn btnDelete"
                    onclick="removeArrivalTime(this)">
                    Remove
                </button>

            </div>

        </div>

        <button
            type="button"
            class="btn btnAdd"
            onclick="addArrivalTime()">
            Add Arrival Time
        </button>


        <h3 style="margin-top:20px;">
            Operating Days
        </h3>

        <div class="daysContainer">

            <label>
                <input
                    type="checkbox"
                    name="operatingDays"
                    value="Monday">
                Monday
            </label>

            <label>
                <input
                    type="checkbox"
                    name="operatingDays"
                    value="Tuesday">
                Tuesday
            </label>

            <label>
                <input
                    type="checkbox"
                    name="operatingDays"
                    value="Wednesday">
                Wednesday
            </label>

            <label>
                <input
                    type="checkbox"
                    name="operatingDays"
                    value="Thursday">
                Thursday
            </label>

            <label>
                <input
                    type="checkbox"
                    name="operatingDays"
                    value="Friday">
                Friday
            </label>

            <label>
                <input
                    type="checkbox"
                    name="operatingDays"
                    value="Saturday">
                Saturday
            </label>

            <label>
                <input
                    type="checkbox"
                    name="operatingDays"
                    value="Sunday">
                Sunday
            </label>

        </div>


        <div style="margin-top:20px;">

            <button
                class="btn btnAdd"
                onclick="addSchedule()">
                Add
            </button>

            <button
                class="btn btnUpdate"
                onclick="updateSchedule()">
                Update
            </button>

            <button
                class="btn btnDelete"
                onclick="deleteSchedule()">
                Delete
            </button>

            <button
                class="btn btnClear"
                onclick="clearSchedule()">
                Clear
            </button>

        </div>


        <div class="tableContainer">

            <table>

                <thead>

                    <tr>

                        <th>Schedule ID</th>
                        <th>Route ID</th>
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


<script src="${pageContext.request.contextPath}/js/dashboard.js"></script>

</body>

</html>