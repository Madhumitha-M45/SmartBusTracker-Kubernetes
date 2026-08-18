const API =
    `${window.location.origin}/BusTracker/api/admin`;
let allBuses = [];
let allStops = [];
let allRoutes = [];
let allRouteStops = [];
let allSchedules = [];
function getVal(id) {
    const el = document.getElementById(id);
    return el ? el.value.trim() : "";
}
function setVal(id, value) {
    const el = document.getElementById(id);
    if (el) {
        el.value = value ?? "";
    }
}
function escapeHtml(value) {
    if (value === null || value === undefined) {
        return "";
    }
    return String(value)
        .replace(/&/g, "&amp;")
        .replace(/</g, "&lt;")
        .replace(/>/g, "&gt;")
        .replace(/"/g, "&quot;")
        .replace(/'/g, "&#039;");
}
function showMessage(message, type = "success") {
    const box =
        document.getElementById("message");
    if (!box) return;
    box.textContent = message;
    box.className =
        `message ${type}`;
    box.style.display = "block";
    setTimeout(() => {
        box.style.display = "none";
    }, 3500);
}
function showError(message) {
    console.error(message);
    showMessage(
        message,
        "error"
    );
}
async function apiRequest(
    url,
    options = {}
) {
    try {
        const response =
            await fetch(url, {
                ...options,
                headers: {
                    "Content-Type":
                        "application/json",
                    ...(options.headers || {})
                }
            });
        const text =
            await response.text();
        let data = null;
        if (text) {
            try {
                data =
                    JSON.parse(text);
            } catch {
                data = text;
            }
        }
        if (!response.ok) {
            let message =
                `HTTP ${response.status}`;
            if (
                typeof data === "string" &&
                data.trim()
            ) {
                message = data;
            } else if (data) {
                message =
                    data.message ||
                    data.error ||
                    message;
            }
            throw new Error(message);
        }
        return data;
    } catch (error) {
        console.error(
            "API Error:",
            error
        );
        throw error;
    }
}
function showTab(
    tabId,
    event
) {
    document
        .querySelectorAll(".tabContent")
        .forEach(tab => {
            tab.style.display =
                "none";
        });
    document
        .querySelectorAll(".tabButton")
        .forEach(button => {
            button.classList
                .remove("active");
        });
    const tab =
        document.getElementById(tabId);
    if (tab) {
        tab.style.display =
            "block";
    }
    if (
        event &&
        event.currentTarget
    ) {
        event.currentTarget
            .classList
            .add("active");
    }
    if (tabId === "bus") {
        loadBuses();
    }
    if (tabId === "stop") {
        loadStops();
    }
    if (tabId === "route") {
        loadRoutes();
    }
    if (tabId === "routeStop") {
        loadRoutes();
        loadStops();
        loadRouteStopMatrix();
    }
    if (tabId === "schedule") {
        loadRoutes().then(() => {
            populateScheduleRoutes();
        });
        loadSchedules();
    }
}
async function loadBuses() {
    try {
        allBuses =
            await apiRequest(
                `${API}/bus`
            ) || [];
        renderBuses();
    } catch (error) {
        showError(
            "Unable to load buses: " +
            error.message
        );
    }
}
function renderBuses() {
    const tbody =
        document.getElementById(
            "busTableBody"
        );
    if (!tbody) return;
    tbody.innerHTML = "";
    if (!allBuses.length) {
        tbody.innerHTML = `
            <tr>
                <td colspan="7"
                    class="emptyCell">
                    No buses found
                </td>
            </tr>
        `;
        return;
    }
    allBuses.forEach(bus => {
        const tr =
            document.createElement("tr");
        tr.innerHTML = `
            <td>
                ${escapeHtml(bus.busId)}
            </td>
            <td>
                ${escapeHtml(bus.busNumber)}
            </td>
            <td>
                ${escapeHtml(bus.busName)}
            </td>
            <td>
                ${escapeHtml(bus.busType)}
            </td>
            <td>
                ${escapeHtml(bus.status || "")}
            </td>
            <td>
                ${escapeHtml(
                    bus.availableFrom || ""
                )}
            </td>
            <td>
                <button
                    class="btnTableEdit"
                    onclick="editBus('${escapeHtml(bus.busId)}')">
                    Edit
                </button>
            </td>
        `;
        tbody.appendChild(tr);
    });
}
async function addBus() {
    const bus = {
        busId:
            getVal("busId"),
        busNumber:
            getVal("busNumber"),
        busName:
            getVal("busName"),
        busType:
            getVal("busType"),
        status:
            getVal("busStatus"),
        availableFrom:
            getVal("availableFrom")
    };
    if (!bus.busId) {
        showError(
            "Enter Bus ID"
        );
        return;
    }
    try {
        await apiRequest(
            `${API}/bus`,
            {
                method: "POST",
                body: JSON.stringify(bus)
            }
        );
        showMessage(
            "Bus added successfully"
        );
        clearBus();
        loadBuses();
    } catch (error) {
        showError(
            error.message
        );
    }
}
async function updateBus() {
    const busId =
        getVal("busId");
    if (!busId) {
        showError(
            "Enter Bus ID"
        );
        return;
    }
    const bus = {
        busId,
        busNumber:
            getVal("busNumber"),
        busName:
            getVal("busName"),
        busType:
            getVal("busType"),
        status:
            getVal("busStatus"),
        availableFrom:
            getVal("availableFrom")
    };
    try {
        await apiRequest(
            `${API}/bus`,
            {
                method: "PUT",
                body: JSON.stringify(bus)
            }
        );
        showMessage(
            "Bus updated successfully"
        );
        clearBus();
        loadBuses();
    } catch (error) {
        showError(
            error.message
        );
    }
}
async function deleteBus() {
    const id =
        getVal("busId");
    if (!id) {
        showError(
            "Enter Bus ID"
        );
        return;
    }
    if (!confirm(
        `Delete bus ${id}?`
    )) {
        return;
    }
    try {
        await apiRequest(
            `${API}/bus/${encodeURIComponent(id)}`,
            {
                method: "DELETE"
            }
        );
        showMessage(
            "Bus deleted successfully"
        );
        clearBus();
        loadBuses();
    } catch (error) {
        showError(
            error.message
        );
    }
}
function editBus(id) {
    const bus =
        allBuses.find(
            b =>
                b.busId === id
        );
    if (!bus) return;
    setVal(
        "busId",
        bus.busId
    );
    setVal(
        "busNumber",
        bus.busNumber
    );
    setVal(
        "busName",
        bus.busName
    );
    setVal(
        "busType",
        bus.busType
    );
    setVal(
        "busStatus",
        bus.status
    );
    setVal(
        "availableFrom",
        bus.availableFrom
    );
}
function clearBus() {
    setVal("busId", "");
    setVal("busNumber", "");
    setVal("busName", "");
    setVal("busType", "");
    setVal("busStatus", "");
    setVal("availableFrom", "");
}
async function loadStops() {
    try {
        allStops =
            await apiRequest(
                `${API}/stop`
            ) || [];
        renderStops();
        if (
            document.getElementById(
                "routeStop"
            )?.style.display !== "none"
        ) {
            renderRouteStopMatrix();
        }
    } catch (error) {
        showError(
            "Unable to load stops: " +
            error.message
        );
    }
}
function renderStops() {
    const tbody =
        document.getElementById(
            "stopTableBody"
        );
    if (!tbody) return;
    tbody.innerHTML = "";
    if (!allStops.length) {
        tbody.innerHTML = `
            <tr>
                <td colspan="3"
                    class="emptyCell">
                    No stops found
                </td>
            </tr>
        `;
        return;
    }
    allStops.forEach(stop => {
        const tr =
            document.createElement("tr");
        tr.innerHTML = `
            <td>
                ${escapeHtml(
                    stop.stopId
                )}
            </td>
            <td>
                ${escapeHtml(
                    stop.stopName
                )}
            </td>
            <td>
                <button
                    class="btnTableEdit"
                    onclick="editStop('${escapeHtml(stop.stopId)}')">
                    Edit
                </button>
            </td>
        `;
        tbody.appendChild(tr);
    });
}
async function addStop() {
    const stop = {
        stopId:
            getVal("stopId"),
        stopName:
            getVal("stopName")
    };
    if (
        !stop.stopId ||
        !stop.stopName
    ) {
        showError(
            "Enter Stop ID and Stop Name"
        );
        return;
    }
    try {
        await apiRequest(
            `${API}/stop`,
            {
                method: "POST",
                body: JSON.stringify(stop)
            }
        );
        showMessage(
            "Stop added successfully"
        );
        clearStop();
        loadStops();
    } catch (error) {
        showError(
            error.message
        );
    }
}
async function updateStop() {
    const stop = {
        stopId:
            getVal("stopId"),
        stopName:
            getVal("stopName")
    };
    if (!stop.stopId) {
        showError(
            "Enter Stop ID"
        );
        return;
    }
    try {
        await apiRequest(
            `${API}/stop`,
            {
                method: "PUT",
                body: JSON.stringify(stop)
            }
        );
        showMessage(
            "Stop updated successfully"
        );
        clearStop();
        loadStops();
    } catch (error) {
        showError(
            error.message
        );
    }
}
async function deleteStop() {
    const id =
        getVal("stopId");
    if (!id) {
        showError(
            "Enter Stop ID"
        );
        return;
    }
    if (!confirm(
        `Delete stop ${id}?`
    )) {
        return;
    }
    try {
        await apiRequest(
            `${API}/stop/${encodeURIComponent(id)}`,
            {
                method: "DELETE"
            }
        );
        showMessage(
            "Stop deleted successfully"
        );
        clearStop();
        loadStops();
    } catch (error) {
        showError(
            error.message
        );
    }
}
function editStop(id) {
    const stop =
        allStops.find(
            s =>
                s.stopId === id
        );
    if (!stop) return;
    setVal(
        "stopId",
        stop.stopId
    );
    setVal(
        "stopName",
        stop.stopName
    );
}
function clearStop() {
    setVal(
        "stopId",
        ""
    );
    setVal(
        "stopName",
        ""
    );
}
async function loadRoutes() {
    try {
        allRoutes =
            await apiRequest(
                `${API}/route`
            ) || [];
        renderRoutes();
    } catch (error) {
        showError(
            "Unable to load routes: " +
            error.message
        );
    }
}
function renderRoutes() {
    const tbody =
        document.getElementById(
            "routeTableBody"
        );
    if (!tbody) return;
    tbody.innerHTML = "";
    if (!allRoutes.length) {
        tbody.innerHTML = `
            <tr>
                <td colspan="6"
                    class="emptyCell">
                    No routes found
                </td>
            </tr>
        `;
        return;
    }
    allRoutes.forEach(route => {
        const tr =
            document.createElement("tr");
        tr.innerHTML = `
            <td>
                ${escapeHtml(
                    route.routeId
                )}
            </td>
            <td>
                ${escapeHtml(
                    route.routeName
                )}
            </td>
            <td>
                ${escapeHtml(
                    route.source
                )}
            </td>
            <td>
                ${escapeHtml(
                    route.destination
                )}
            </td>
            <td>
                ${escapeHtml(
                    route.distance
                )}
            </td>
            <td>
                <button
                    class="btnTableEdit"
                    onclick="editRoute('${escapeHtml(route.routeId)}')">
                    Edit
                </button>
            </td>
        `;
        tbody.appendChild(tr);
    });
}
async function addRoute() {
    const distanceValue =
        getVal("distance");
    const route = {
        routeId:
            getVal("routeInputId"),
        routeName:
            getVal("routeName"),
        source:
            getVal("source"),
        destination:
            getVal("destination"),
        distance:
            distanceValue
                ? Number(distanceValue)
                : 0
    };
    if (
        !route.routeId ||
        !route.routeName
    ) {
        showError(
            "Route ID and Route Name are required"
        );
        return;
    }
    try {
        await apiRequest(
            `${API}/route`,
            {
                method: "POST",
                body: JSON.stringify(route)
            }
        );
        showMessage(
            "Route added successfully"
        );
        clearRoute();
        loadRoutes();
    } catch (error) {
        showError(
            error.message
        );
    }
}
async function updateRoute() {
    const route = {
        routeId:
            getVal("routeInputId"),
        routeName:
            getVal("routeName"),
        source:
            getVal("source"),
        destination:
            getVal("destination"),
        distance:
            Number(
                getVal("distance") || 0
            )
    };
    if (!route.routeId) {
        showError(
            "Enter Route ID"
        );
        return;
    }
    try {
        await apiRequest(
            `${API}/route`,
            {
                method: "PUT",
                body: JSON.stringify(route)
            }
        );
        showMessage(
            "Route updated successfully"
        );
        clearRoute();
        loadRoutes();
    } catch (error) {
        showError(
            error.message
        );
    }
}
async function deleteRoute() {
    const id =
        getVal("routeInputId");
    if (!id) {
        showError(
            "Enter Route ID"
        );
        return;
    }
    if (!confirm(
        `Delete route ${id}?`
    )) {
        return;
    }
    try {
        await apiRequest(
            `${API}/route/${encodeURIComponent(id)}`,
            {
                method: "DELETE"
            }
        );
        showMessage(
            "Route deleted successfully"
        );
        clearRoute();
        loadRoutes();
    } catch (error) {
        showError(
            error.message
        );
    }
}
function editRoute(id) {
    const route =
        allRoutes.find(
            r =>
                r.routeId === id
        );
    if (!route) return;
    setVal(
        "routeInputId",
        route.routeId
    );
    setVal(
        "routeName",
        route.routeName
    );
    setVal(
        "source",
        route.source
    );
    setVal(
        "destination",
        route.destination
    );
    setVal(
        "distance",
        route.distance
    );
}
function clearRoute() {
    setVal(
        "routeInputId",
        ""
    );
    setVal(
        "routeName",
        ""
    );
    setVal(
        "source",
        ""
    );
    setVal(
        "destination",
        ""
    );
    setVal(
        "distance",
        ""
    );
}
function findRouteStop(
    routeId,
    stopId
) {
    return allRouteStops.find(
        rs =>
            String(rs.routeId) ===
                String(routeId) &&
            String(rs.stopId) ===
                String(stopId)
    );
}
function routeStopKey(
    routeId,
    stopId
) {
    return (
        String(routeId)
            .replace(/[^a-zA-Z0-9_-]/g, "_")
        +
        "_"
        +
        String(stopId)
            .replace(/[^a-zA-Z0-9_-]/g, "_")
    );
}
async function loadRouteStopMatrix() {
    try {
        if (!allRoutes.length) {
            allRoutes =
                await apiRequest(
                    `${API}/route`
                ) || [];
        }
        if (!allStops.length) {
            allStops =
                await apiRequest(
                    `${API}/stop`
                ) || [];
        }
        allRouteStops =
            await apiRequest(
                `${API}/route-stop`
            ) || [];
        renderRouteStopMatrix();
    } catch (error) {
        showError(
            "Unable to load route stops: " +
            error.message
        );
    }
}
function renderRouteStopMatrix() {
    const tbody =
        document.getElementById(
            "routeStopTableBody"
        );
    if (!tbody) return;
    tbody.innerHTML = "";
    if (!allRoutes.length) {
        tbody.innerHTML = `
            <tr>
                <td colspan="6"
                    class="emptyCell">
                    No routes found
                </td>
            </tr>
        `;
        return;
    }
    if (!allStops.length) {
        tbody.innerHTML = `
            <tr>
                <td colspan="6"
                    class="emptyCell">
                    No stops found
                </td>
            </tr>
        `;
        return;
    }
    allRoutes.forEach(route => {
        const routeHeader =
            document.createElement("tr");
        routeHeader.className =
            "routeGroupRow";
        routeHeader.innerHTML = `
            <td colspan="6">
                ${escapeHtml(
                    route.routeId
                )}
                -
                ${escapeHtml(
                    route.routeName
                )}
                (${escapeHtml(
                    route.source
                )}
                →
                ${escapeHtml(
                    route.destination
                )})
            </td>
        `;
        tbody.appendChild(
            routeHeader
        );
        allStops.forEach(stop => {
            const existing =
                findRouteStop(
                    route.routeId,
                    stop.stopId
                );
            const key =
                routeStopKey(
                    route.routeId,
                    stop.stopId
                );
            const tr =
                document.createElement("tr");
            const checked =
                existing
                    ? "checked"
                    : "";
            const order =
                existing &&
                existing.stopOrder !== undefined
                    ? existing.stopOrder
                    : "";
            const distance =
                existing &&
                existing.distanceFromPrevious !== undefined
                    ? existing.distanceFromPrevious
                    : "";
            tr.dataset.routeId =
                route.routeId;
            tr.dataset.stopId =
                stop.stopId;
            tr.innerHTML = `
                <!-- ROUTE -->
                <td class="routeNameCell">
                    ${escapeHtml(
                        route.routeId
                    )}
                </td>
                <!-- CHECKBOX -->
                <td>
                    <input
                        type="checkbox"
                        class="routeStopCheckbox"
                        data-key="${key}"
                        data-route-id="${escapeHtml(route.routeId)}"
                        data-stop-id="${escapeHtml(stop.stopId)}"
                        ${checked}
                        onchange="routeStopCheckboxChanged(this)">
                </td>
                <!-- STOP ID -->
                <td>
                    ${escapeHtml(
                        stop.stopId
                    )}
                </td>
                <!-- STOP NAME -->
                <td class="stopNameCell">
                    ${escapeHtml(
                        stop.stopName
                    )}
                </td>
                <!-- STOP ORDER -->
                <td>
                    <input
                        type="number"
                        min="1"
                        class="routeStopNumber"
                        data-order-key="${key}"
                        value="${escapeHtml(order)}"
                        placeholder="Order"
                        ${existing ? "" : "disabled"}>
                </td>
                <!-- DISTANCE -->
                <td>
                    <input
                        type="number"
                        min="0"
                        step="0.01"
                        class="routeStopDistance"
                        data-distance-key="${key}"
                        value="${escapeHtml(distance)}"
                        placeholder="Distance"
                        ${existing ? "" : "disabled"}>
                </td>
            `;
            tbody.appendChild(tr);
        });
    });
}
function routeStopCheckboxChanged(
    checkbox
) {
    const row =
        checkbox.closest("tr");
    if (!row) return;
    const orderInput =
        row.querySelector(
            ".routeStopNumber"
        );
    const distanceInput =
        row.querySelector(
            ".routeStopDistance"
        );
    if (
        checkbox.checked
    ) {
        if (orderInput) {
            orderInput.disabled =
                false;
        }
        if (distanceInput) {
            distanceInput.disabled =
                false;
        }
        if (
            orderInput &&
            !orderInput.value
        ) {
            const routeId =
                checkbox.dataset.routeId;
            const routeRows =
                document.querySelectorAll(
                    `tr[data-route-id="${CSS.escape(routeId)}"]`
                );
            let maxOrder = 0;
            routeRows.forEach(
                routeRow => {
                    const cb =
                        routeRow.querySelector(
                            ".routeStopCheckbox"
                        );
                    if (
                        cb &&
                        cb.checked
                    ) {
                        const input =
                            routeRow.querySelector(
                                ".routeStopNumber"
                            );
                        if (input) {
                            const value =
                                Number(
                                    input.value
                                );
                            if (
                                value >
                                maxOrder
                            ) {
                                maxOrder =
                                    value;
                            }
                        }
                    }
                }
            );
            orderInput.value =
                maxOrder + 1;
        }
    } else {
        if (orderInput) {
            orderInput.value =
                "";
            orderInput.disabled =
                true;
        }
        if (distanceInput) {
            distanceInput.value =
                "";
            distanceInput.disabled =
                true;
        }
    }
}
function getRouteStopMatrixData() {
    const rows =
        document.querySelectorAll(
            "#routeStopTableBody tr[data-route-id]"
        );
    const result = [];
    rows.forEach(row => {
        const checkbox =
            row.querySelector(
                ".routeStopCheckbox"
            );
        if (!checkbox) return;
        const routeId =
            checkbox.dataset.routeId;
        const stopId =
            checkbox.dataset.stopId;
        const orderInput =
            row.querySelector(
                ".routeStopNumber"
            );
        const distanceInput =
            row.querySelector(
                ".routeStopDistance"
            );
        const selected =
            checkbox.checked;
        result.push({
            routeId,
            stopId,
            selected,
            stopOrder:
                orderInput &&
                orderInput.value
                    ? Number(
                        orderInput.value
                    )
                    : 0,
            distanceFromPrevious:
                distanceInput &&
                distanceInput.value
                    ? Number(
                        distanceInput.value
                    )
                    : 0
        });
    });
    return result;
}
async function saveAllRouteStops() {
    const data = getRouteStopMatrixData();
    const selected = data.filter(item => item.selected);
    if (!selected.length) {
        showError("Select at least one stop");
        return;
    }
    for (const item of selected) {
        if (!item.stopOrder || item.stopOrder < 1) {
            showError(`Enter Stop Order for ${item.routeId} - ${item.stopId}`);
            return;
        }
        if (item.distanceFromPrevious < 0) {
            showError(`Invalid distance for ${item.routeId} - ${item.stopId}`);
            return;
        }
    }
    try {
        allRouteStops = await apiRequest(`${API}/route-stop`) || [];
        for (const item of data) {
            const existing = findRouteStop(item.routeId, item.stopId);
            if (item.selected) {
                const stop = allStops.find(s => String(s.stopId) === String(item.stopId));
                const routeStop = {
                    id: `${item.routeId}_${item.stopId}`,
                    routeId: item.routeId,
                    stopId: item.stopId,
                    stopName: stop ? stop.stopName : "",
                    stopOrder: item.stopOrder,
                    distanceFromPrevious: item.distanceFromPrevious
                };
                if (existing) {
                    await apiRequest(`${API}/route-stop`, {
                        method: "PUT",
                        body: JSON.stringify(routeStop)
                    });
                } else {
                    await apiRequest(`${API}/route-stop`, {
                        method: "POST",
                        body: JSON.stringify(routeStop)
                    });
                }
            }
        }
        allRouteStops = await apiRequest(`${API}/route-stop`) || [];
        renderRouteStopMatrix();
        showMessage("Route stops saved successfully");
    } catch (error) {
        showError("Unable to save route stops: " + error.message);
    }
}
async function deleteSelectedRouteStops() {
    const data =
        getRouteStopMatrixData();
    const unselected =
        data.filter(
            item =>
                !item.selected
        );
    const existingUnselected =
        unselected.filter(
            item =>
                findRouteStop(
                    item.routeId,
                    item.stopId
                )
        );
    if (
        !existingUnselected.length
    ) {
        showMessage(
            "No existing route stops to delete"
        );
        return;
    }
    if (
        !confirm(
            `Delete ${existingUnselected.length} unselected route stop(s)?`
        )
    ) {
        return;
    }
    try {
        for (
            const item
            of existingUnselected
        ) {
            const id =
                `${item.routeId}_${item.stopId}`;
            await apiRequest(
                `${API}/route-stop/${encodeURIComponent(id)}`,
                {
                    method: "DELETE"
                }
            );
        }
        allRouteStops =
            await apiRequest(
                `${API}/route-stop`
            ) || [];
        renderRouteStopMatrix();
        showMessage(
            "Unselected route stops deleted"
        );
    } catch (error) {
        showError(
            "Unable to delete route stops: " +
            error.message
        );
    }
}
async function loadSchedules() {
    try {
        allSchedules =
            await apiRequest(
                `${API}/schedule`
            ) || [];
        renderSchedules();
    } catch (error) {
        showError(
            "Unable to load schedules: " +
            error.message
        );
    }
}
function renderSchedules() {
    const tbody =
        document.getElementById(
            "scheduleTableBody"
        );
    if (!tbody) return;
    tbody.innerHTML = "";
    if (!allSchedules.length) {
        tbody.innerHTML = `
            <tr>
                <td colspan="8"
                    class="emptyCell">
                    No schedules found
                </td>
            </tr>
        `;
        return;
    }
    allSchedules.forEach(
        schedule => {
            const departure =
                Array.isArray(
                    schedule.departureTimes
                )
                    ? schedule.departureTimes.join(", ")
                    : "";
            const arrival =
                Array.isArray(
                    schedule.arrivalTimes
                )
                    ? schedule.arrivalTimes.join(", ")
                    : "";
            const days =
                Array.isArray(
                    schedule.operatingDays
                )
                    ? schedule.operatingDays.join(", ")
                    : "";
            const tr =
                document.createElement("tr");
            tr.innerHTML = `
                <td>
                    ${escapeHtml(
                        schedule.scheduleId
                    )}
                </td>
                <td>
                    ${escapeHtml(
                        schedule.routeId
                    )}
                </td>
                <td>
                    ${escapeHtml(
                        schedule.sourceName
                    )}
                </td>
                <td>
                    ${escapeHtml(
                        schedule.destinationName
                    )}
                </td>
                <td>
                    ${escapeHtml(
                        departure
                    )}
                </td>
                <td>
                    ${escapeHtml(
                        arrival
                    )}
                </td>
                <td>
                    ${escapeHtml(
                        days
                    )}
                </td>
                <td>
                    <button
                        class="btnTableEdit"
                        onclick="editSchedule('${escapeHtml(schedule.scheduleId)}')">
                        Edit
                    </button>
                </td>
            `;
            tbody.appendChild(tr);
        }
    );
}
function loadSourceDestination() {
    const routeId =
        getVal(
            "scheduleRouteId"
        );
    const route =
        allRoutes.find(
            r =>
                r.routeId ===
                routeId
        );
    if (!route) {
        setVal(
            "sourceName",
            ""
        );
        setVal(
            "destinationName",
            ""
        );
        return;
    }
    setVal(
        "sourceName",
        route.source
    );
    setVal(
        "destinationName",
        route.destination
    );
}
function populateScheduleRoutes() {
    const select =
        document.getElementById(
            "scheduleRouteId"
        );
    if (!select) return;
    const oldValue =
        select.value;
    select.innerHTML = `
        <option value="">
            -- Select Route --
        </option>
    `;
    allRoutes.forEach(
        route => {
            const option =
                document.createElement(
                    "option"
                );
            option.value =
                route.routeId;
            option.textContent =
                `${route.routeId} - ${route.routeName}`;
            select.appendChild(
                option
            );
        }
    );
    if (oldValue) {
        select.value =
            oldValue;
    }
}
function addDepartureTime(
    value = ""
) {
    const container =
        document.getElementById(
            "departureTimesContainer"
        );
    if (!container) return;
    const row =
        document.createElement(
            "div"
        );
    row.className =
        "timeRow";
    row.innerHTML = `
        <input
            type="time"
            class="departureTime"
            value="${escapeHtml(value)}">
        <button
            type="button"
            class="btn btnDelete"
            onclick="removeDepartureTime(this)">
            Remove
        </button>
    `;
    container.appendChild(
        row
    );
}
function removeDepartureTime(
    button
) {
    const rows =
        document.querySelectorAll(
            "#departureTimesContainer .timeRow"
        );
    if (rows.length <= 1) {
        if (rows[0]) {
            rows[0]
                .querySelector("input")
                .value = "";
        }
        return;
    }
    button
        .closest(".timeRow")
        ?.remove();
}
function getDepartureTimes() {
    return [
        ...document.querySelectorAll(
            ".departureTime"
        )
    ]
        .map(
            input =>
                input.value
        )
        .filter(Boolean);
}
function addArrivalTime(
    value = ""
) {
    const container =
        document.getElementById(
            "arrivalTimesContainer"
        );
    if (!container) return;
    const row =
        document.createElement(
            "div"
        );
    row.className =
        "timeRow";
    row.innerHTML = `
        <input
            type="time"
            class="arrivalTime"
            value="${escapeHtml(value)}">
        <button
            type="button"
            class="btn btnDelete"
            onclick="removeArrivalTime(this)">
            Remove
        </button>
    `;
    container.appendChild(
        row
    );
}
function removeArrivalTime(
    button
) {
    const rows =
        document.querySelectorAll(
            "#arrivalTimesContainer .timeRow"
        );
    if (rows.length <= 1) {
        if (rows[0]) {
            rows[0]
                .querySelector("input")
                .value = "";
        }
        return;
    }
    button
        .closest(".timeRow")
        ?.remove();
}
function getArrivalTimes() {
    return [
        ...document.querySelectorAll(
            ".arrivalTime"
        )
    ]
        .map(
            input =>
                input.value
        )
        .filter(Boolean);
}
function getOperatingDays() {
    return [
        ...document.querySelectorAll(
            'input[name="operatingDays"]:checked'
        )
    ]
        .map(
            checkbox =>
                checkbox.value
        );
}
function setOperatingDays(
    days
) {
    document
        .querySelectorAll(
            'input[name="operatingDays"]'
        )
        .forEach(
            checkbox => {
                checkbox.checked =
                    Array.isArray(days) &&
                    days.includes(
                        checkbox.value
                    );
            }
        );
}
async function addSchedule() {
    const schedule = {
        scheduleId:
            getVal("scheduleId"),
        routeId:
            getVal("scheduleRouteId"),
        sourceName:
            getVal("sourceName"),
        destinationName:
            getVal("destinationName"),
        departureTimes:
            getDepartureTimes(),
        arrivalTimes:
            getArrivalTimes(),
        operatingDays:
            getOperatingDays()
    };
    if (!schedule.scheduleId) {
        showError(
            "Enter Schedule ID"
        );
        return;
    }
    if (!schedule.routeId) {
        showError(
            "Select Route"
        );
        return;
    }
    if (
        !schedule.departureTimes.length
    ) {
        showError(
            "Add at least one departure time"
        );
        return;
    }
    if (
        !schedule.arrivalTimes.length
    ) {
        showError(
            "Add at least one arrival time"
        );
        return;
    }
    try {
        await apiRequest(
            `${API}/schedule`,
            {
                method: "POST",
                body:
                    JSON.stringify(
                        schedule
                    )
            }
        );
        showMessage(
            "Schedule added successfully"
        );
        clearSchedule();
        loadSchedules();
    } catch (error) {
        showError(
            error.message
        );
    }
}
async function updateSchedule() {
    const schedule = {
        scheduleId:
            getVal("scheduleId"),
        routeId:
            getVal("scheduleRouteId"),
        sourceName:
            getVal("sourceName"),
        destinationName:
            getVal("destinationName"),
        departureTimes:
            getDepartureTimes(),
        arrivalTimes:
            getArrivalTimes(),
        operatingDays:
            getOperatingDays()
    };
    if (!schedule.scheduleId) {
        showError(
            "Enter Schedule ID"
        );
        return;
    }
    try {
        await apiRequest(
            `${API}/schedule`,
            {
                method: "PUT",
                body:
                    JSON.stringify(
                        schedule
                    )
            }
        );
        showMessage(
            "Schedule updated successfully"
        );
        clearSchedule();
        loadSchedules();
    } catch (error) {
        showError(
            error.message
        );
    }
}
function editSchedule(id) {
    const schedule =
        allSchedules.find(
            s =>
                s.scheduleId === id
        );
    if (!schedule) {
        showError(
            "Schedule not found"
        );
        return;
    }
    setVal(
        "scheduleId",
        schedule.scheduleId
    );
    setVal(
        "scheduleRouteId",
        schedule.routeId
    );
    loadSourceDestination();
    const departureContainer =
        document.getElementById(
            "departureTimesContainer"
        );
    departureContainer.innerHTML =
        "";
    if (
        Array.isArray(
            schedule.departureTimes
        ) &&
        schedule.departureTimes.length
    ) {
        schedule.departureTimes.forEach(
            time =>
                addDepartureTime(time)
        );
    } else {
        addDepartureTime("");
    }
    const arrivalContainer =
        document.getElementById(
            "arrivalTimesContainer"
        );
    arrivalContainer.innerHTML =
        "";
    if (
        Array.isArray(
            schedule.arrivalTimes
        ) &&
        schedule.arrivalTimes.length
    ) {
        schedule.arrivalTimes.forEach(
            time =>
                addArrivalTime(time)
        );
    } else {
        addArrivalTime("");
    }
    setOperatingDays(
        schedule.operatingDays
    );
}
async function deleteSchedule() {
    const id =
        getVal("scheduleId");
    if (!id) {
        showError(
            "Enter Schedule ID"
        );
        return;
    }
    if (!confirm(
        `Delete schedule ${id}?`
    )) {
        return;
    }
    try {
        await apiRequest(
            `${API}/schedule/${encodeURIComponent(id)}`,
            {
                method: "DELETE"
            }
        );
        showMessage(
            "Schedule deleted successfully"
        );
        clearSchedule();
        loadSchedules();
    } catch (error) {
        showError(
            error.message
        );
    }
}
function clearSchedule() {
    setVal(
        "scheduleId",
        ""
    );
    setVal(
        "scheduleRouteId",
        ""
    );
    setVal(
        "sourceName",
        ""
    );
    setVal(
        "destinationName",
        ""
    );
    const departureContainer =
        document.getElementById(
            "departureTimesContainer"
        );
    if (departureContainer) {
        departureContainer.innerHTML = `
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
        `;
    }
    const arrivalContainer =
        document.getElementById(
            "arrivalTimesContainer"
        );
    if (arrivalContainer) {
        arrivalContainer.innerHTML = `
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
        `;
    }
    document
        .querySelectorAll(
            'input[name="operatingDays"]'
        )
        .forEach(
            checkbox =>
                checkbox.checked = false
        );
}
document.addEventListener(
    "DOMContentLoaded",
    async function() {
        console.log(
            "Bus Tracking Admin Panel loaded"
        );
        try {
            await loadBuses();
            await loadStops();
            await loadRoutes();
            populateScheduleRoutes();
            await loadRouteStopMatrix();
            await loadSchedules();
        } catch (error) {
            console.error(
                "Initial loading error:",
                error
            );
        }
    }
);