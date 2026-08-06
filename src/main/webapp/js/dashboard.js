const API = `${window.location.origin}/BusTracker/api/admin`;

/* ===========================
   HELPER FUNCTIONS
=========================== */

const getVal = (id) => {
  const el = document.getElementById(id);
  return el ? el.value.trim() : "";
};

const setVal = (id, val) => {
  const el = document.getElementById(id);
  if (el) el.value = val ?? "";
};

const populateDropdown = (elementId, items, valueKey, labelKey, defaultText) => {
  const select = document.getElementById(elementId);
  if (!select) return;

  const options = items
    .map((item) => `<option value="${item[valueKey]}">${typeof labelKey === "function" ? labelKey(item) : item[labelKey]}</option>`)
    .join("");

  select.innerHTML = `<option value="">${defaultText}</option>${options}`;
};

/* ===========================
   TAB NAVIGATION
=========================== */

function showTab(tabId, evt) {
  document.querySelectorAll(".tabContent").forEach((tab) => {
    tab.style.display = "none";
  });

  document.querySelectorAll(".tabButton").forEach((btn) => {
    btn.classList.remove("active");
  });

  const targetTab = document.getElementById(tabId);
  if (targetTab) targetTab.style.display = "block";

  if (evt?.currentTarget) {
    evt.currentTarget.classList.add("active");
  }

  switch (tabId) {
    case "bus":
      loadBus();
      loadRouteDropdownForBus();
      break;
    case "stop":
      loadStop();
      break;
    case "route":
      loadRoute();
      loadStopsForRoute();
      break;
    case "schedule":
      loadSchedule();
      loadBusDropdown();
      loadRouteDropdown();
      break;
  }
}

/* ===========================
   BUS CRUD
=========================== */

async function loadBus() {
  try {
    const res = await fetch(`${API}/bus`);
    if (!res.ok) throw new Error("Failed to load buses");

    const data = await res.json();
    const body = document.getElementById("busTableBody");
    if (!body) return;

    body.innerHTML = data
      .map((bus) => {
        const rId = bus.routeId || "";
        return `
<tr>
  <td>${bus.busId}</td>
  <td>${bus.busNumber}</td>
  <td>${bus.busName}</td>
  <td>${bus.busType}</td>
  <td>${rId}</td>
  <td>
    <button class="btnUpdate" onclick="editBus('${bus.busId}', '${bus.busNumber}', '${bus.busName}', '${bus.busType}', '${rId}')">Edit</button>
  </td>
</tr>`;
      })
      .join("");
  } catch (err) {
    console.error(err);
  }
}

async function loadRouteDropdownForBus() {
  try {
    const res = await fetch(`${API}/route`);
    const data = await res.json();
    populateDropdown("routeId", data, "routeId", "routeName", "-- Select Route --");
  } catch (err) {
    console.error(err);
  }
}

async function addBus() {
  const bus = {
    busId: getVal("busId"),
    busNumber: getVal("busNumber"),
    busName: getVal("busName"),
    busType: getVal("busType"),
    routeId: getVal("routeId")
  };

  try {
    const res = await fetch(`${API}/bus`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(bus)
    });
    if (!res.ok) throw new Error("Failed to add bus");

    alert("Bus Added Successfully");
    clearBus();
    loadBus();
  } catch (err) {
    alert(`Error: ${err.message}`);
  }
}

function editBus(id, number, name, type, route) {
  setVal("busId", id);
  setVal("busNumber", number);
  setVal("busName", name);
  setVal("busType", type);
  setVal("routeId", route);
}

async function updateBus() {
  const bus = {
    busId: getVal("busId"),
    busNumber: getVal("busNumber"),
    busName: getVal("busName"),
    busType: getVal("busType"),
    routeId: getVal("routeId")
  };

  try {
    const res = await fetch(`${API}/bus`, {
      method: "PUT",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(bus)
    });

    if (!res.ok) throw new Error("Failed to update bus");

    alert("Bus Updated Successfully");
    clearBus();
    loadBus();
  } catch (err) {
    alert(`Error: ${err.message}`);
  }
}

async function deleteBus() {
  const id = getVal("busId");
  if (!id) return alert("Please specify a Bus ID to delete.");

  try {
    const res = await fetch(`${API}/bus/${id}`, { method: "DELETE" });
    if (!res.ok) throw new Error("Failed to delete bus");

    alert("Bus Deleted Successfully");
    clearBus();
    loadBus();
  } catch (err) {
    alert(`Error: ${err.message}`);
  }
}

function clearBus() {
  setVal("busId", "");
  setVal("busNumber", "");
  setVal("busName", "");
  const busType = document.getElementById("busType");
  if (busType) busType.selectedIndex = 0;

  const routeId = document.getElementById("routeId");
  if (routeId) routeId.selectedIndex = 0;
}

/* ===========================
   STOP CRUD
=========================== */

async function loadStop() {
  try {
    const res = await fetch(`${API}/stop`);
    const data = await res.json();
    const body = document.getElementById("stopTableBody");
    if (!body) return;

    body.innerHTML = data
      .map(
        (stop) => `
<tr>
  <td>${stop.stopId}</td>
  <td>${stop.stopName}</td>
  <td>
    <button class="btnUpdate" onclick="editStop('${stop.stopId}', '${stop.stopName}')">Edit</button>
  </td>
</tr>`
      )
      .join("");
  } catch (err) {
    console.error(err);
  }
}

async function addStop() {
  const stop = {
    stopId: getVal("stopId"),
    stopName: getVal("stopName")
  };

  try {
    const res = await fetch(`${API}/stop`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(stop)
    });
    if (!res.ok) throw new Error("Failed to add stop");

    alert("Stop Added Successfully");
    clearStop();
    loadStop();
  } catch (err) {
    alert(`Error: ${err.message}`);
  }
}

function editStop(id, name) {
  setVal("stopId", id);
  setVal("stopName", name);
}

async function updateStop() {
  const stop = {
    stopId: getVal("stopId"),
    stopName: getVal("stopName")
  };

  try {
    const res = await fetch(`${API}/stop`, {
      method: "PUT",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(stop)
    });

    if (!res.ok) throw new Error("Failed to update stop");

    alert("Stop Updated Successfully");
    clearStop();
    loadStop();
  } catch (err) {
    alert(`Error: ${err.message}`);
  }
}

async function deleteStop() {
  const id = getVal("stopId");
  if (!id) return alert("Please specify a Stop ID to delete.");

  try {
    const res = await fetch(`${API}/stop/${id}`, { method: "DELETE" });
    if (!res.ok) throw new Error("Failed to delete stop");

    alert("Stop Deleted Successfully");
    clearStop();
    loadStop();
  } catch (err) {
    alert(`Error: ${err.message}`);
  }
}

function clearStop() {
  setVal("stopId", "");
  setVal("stopName", "");
}

/* ===========================
   ROUTE CRUD
=========================== */

async function loadRoute() {
  try {
    const res = await fetch(`${API}/route`);
    if (!res.ok) throw new Error("Failed to load routes");

    const data = await res.json();
    const body = document.getElementById("routeTableBody");
    body.innerHTML = "";

    data.forEach((route) => {
      let stops = "";
      if (route.routeStops) {
        stops = route.routeStops
          .sort((a, b) => a.stopOrder - b.stopOrder)
          .map((s) => `${s.stopName} (${s.distanceFromPrevious} km)`)
          .join(" ➜ ");
      }

      body.innerHTML += `
<tr>
  <td>${route.routeId}</td>
  <td>${route.routeName}</td>
  <td>${route.source || ''}</td>
  <td>${route.destination || ''}</td>
  <td>${route.distance}</td>
  <td>${stops}</td>
  <td>
    <button class="btnUpdate" onclick="editRoute('${route.routeId}')">Edit</button>
  </td>
</tr>`;
    });
  } catch (err) {
    console.error(err);
  }
}

async function loadStopsForRoute() {
  try {
    const res = await fetch(`${API}/stop`);
    const data = await res.json();
    const tbody = document.getElementById("routeStopsBody");
    if (!tbody) return;

    tbody.innerHTML = data
      .map(
        (stop, index) => `
<tr>
  <td>
    <input type="checkbox" value="${stop.stopId}" data-name="${stop.stopName}">
  </td>
  <td>${stop.stopName}</td>
  <td>
    <input type="number" class="stopOrder" value="${index + 1}" min="1">
  </td>
  <td>
    <input type="number" class="distancePrev" value="${index == 0 ? 0 : ''}" min="0">
  </td>
</tr>`
      )
      .join("");
  } catch (err) {
    console.error(err);
  }
}

function getRouteStops() {
  const rows = document.querySelectorAll("#routeStopsBody tr");
  const routeStops = [];

  rows.forEach((row) => {
    const checkBox = row.querySelector("input[type='checkbox']");

    if (checkBox.checked) {
      routeStops.push({
        stopId: checkBox.value,
        stopName: checkBox.dataset.name,
        stopOrder: parseInt(row.querySelector(".stopOrder").value),
        distanceFromPrevious: parseFloat(row.querySelector(".distancePrev").value || 0)
      });
    }
  });

  return routeStops;
}

async function addRoute() {
  const route = {
    routeId: getVal("routeInputId"),
    routeName: getVal("routeName"),
    source: getVal("source"),
    destination: getVal("destination"),
    distance: parseFloat(getVal("distance")),
    routeStops: getRouteStops()
  };

  try {
    const res = await fetch(`${API}/route`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(route)
    });

    if (!res.ok) throw new Error("Failed to add route");

    alert("Route Added Successfully");
    clearRoute();
    loadRoute();
  } catch (err) {
    alert(err.message);
  }
}

async function editRoute(routeId) {
  try {
    const res = await fetch(`${API}/route/${routeId}`);
    if (!res.ok) throw new Error("Route not found");

    const route = await res.json();

    setVal("routeInputId", route.routeId);
    setVal("routeName", route.routeName);
    setVal("source", route.source || "");
    setVal("destination", route.destination || "");
    setVal("distance", route.distance);

    document.querySelectorAll("#routeStopsBody tr").forEach((row) => {
      row.querySelector("input[type='checkbox']").checked = false;
      row.querySelector(".stopOrder").value = "";
      row.querySelector(".distancePrev").value = "";
    });

    if (route.routeStops) {
      route.routeStops.forEach((stop) => {
        document.querySelectorAll("#routeStopsBody tr").forEach((row) => {
          const chk = row.querySelector("input[type='checkbox']");
          if (chk.value === stop.stopId) {
            chk.checked = true;
            row.querySelector(".stopOrder").value = stop.stopOrder;
            row.querySelector(".distancePrev").value = stop.distanceFromPrevious;
          }
        });
      });
    }
  } catch (err) {
    alert(err.message);
  }
}

async function updateRoute() {
  const route = {
    routeId: getVal("routeInputId"),
    routeName: getVal("routeName"),
    source: getVal("source"),
    destination: getVal("destination"),
    distance: parseFloat(getVal("distance")),
    routeStops: getRouteStops()
  };

  try {
    const res = await fetch(`${API}/route`, {
      method: "PUT",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(route)
    });

    if (!res.ok) throw new Error("Failed to update route");

    alert("Route Updated Successfully");
    clearRoute();
    loadRoute();
  } catch (err) {
    alert(err.message);
  }
}

async function deleteRoute() {
  const id = getVal("routeInputId");
  if (!id) return alert("Please specify a Route ID to delete.");

  try {
    const res = await fetch(`${API}/route/${id}`, { method: "DELETE" });

    if (!res.ok) throw new Error("Failed to delete route");

    alert("Route Deleted Successfully");
    clearRoute();
    loadRoute();
  } catch (err) {
    alert(`Error: ${err.message}`);
  }
}

function clearRoute() {
  setVal("routeInputId", "");
  setVal("routeName", "");
  setVal("source", "");
  setVal("destination", "");
  setVal("distance", "");

  document.querySelectorAll("#routeStopsBody tr").forEach((row) => {
    row.querySelector("input[type='checkbox']").checked = false;
    row.querySelector(".stopOrder").value = "";
    row.querySelector(".distancePrev").value = "";
  });
}

/* ===========================
   SCHEDULE CRUD
=========================== */

function getSelectedDays() {
  const checkboxes = document.querySelectorAll('input[name="operatingDays"]:checked');
  return Array.from(checkboxes).map((cb) => cb.value);
}

function setSelectedDays(daysList) {
  const daysArray = Array.isArray(daysList)
    ? daysList.map((d) => d.trim().toUpperCase())
    : daysList ? daysList.split(",").map((d) => d.trim().toUpperCase()) : [];

  document.querySelectorAll('input[name="operatingDays"]').forEach((cb) => {
    cb.checked = daysArray.includes(cb.value.toUpperCase());
  });
}

async function loadSchedule() {

    try {

        const res = await fetch(`${API}/schedule`);

        const data = await res.json();

        const body = document.getElementById("scheduleTableBody");

        body.innerHTML = "";

        data.forEach(schedule => {

            const day =
                schedule.operatingDays
                    ? schedule.operatingDays.join(", ")
                    : "";

            body.innerHTML += `

<tr>

<td>${schedule.scheduleId}</td>

<td>${schedule.busId}</td>

<td>${schedule.routeId}</td>

<td>${schedule.sourceName}</td>

<td>${schedule.destinationName}</td>

<td>${schedule.departureTime}</td>

<td>${schedule.arrivalTime}</td>

<td>${day}</td>

<td>

<button class="btnUpdate"

onclick='editSchedule(${JSON.stringify(schedule)})'>

Edit

</button>

</td>

</tr>

`;

        });

    }

    catch(err){

        console.error(err);

    }

}

async function loadBusDropdown() {
  try {
    const res = await fetch(`${API}/bus`);
    const data = await res.json();
    populateDropdown("scheduleBusId", data, "busId", (b) => `${b.busNumber} (${b.busName})`, "-- Select Bus --");
  } catch (err) {
    console.error(err);
  }
}

async function loadRouteDropdown() {
  try {
    const res = await fetch(`${API}/route`);
    const data = await res.json();
    populateDropdown("scheduleRouteId", data, "routeId", "routeName", "-- Select Route --");
  } catch (err) {
    console.error(err);
  }
}
async function loadSourceDestination(){

    const routeId=getVal("scheduleRouteId");

    if(!routeId){

        setVal("sourceName","");

        setVal("destinationName","");

        return;

    }

    try{

        const res=await fetch(`${API}/route/${routeId}`);

        const route=await res.json();

        if(route.routeStops && route.routeStops.length){

            route.routeStops.sort((a,b)=>a.stopOrder-b.stopOrder);

            setVal("sourceName",route.routeStops[0].stopName);

            setVal(

                "destinationName",

                route.routeStops[route.routeStops.length-1].stopName

            );

        }

    }

    catch(err){

        console.error(err);

    }

}

const formatTime = (timeStr) => (timeStr.length === 5 ? `${timeStr}:00` : timeStr);

async function addSchedule() {
  const selectedDays = getSelectedDays();

  if (selectedDays.length === 0) {
    alert("Please select at least one operating day.");
    return;
  }

   const schedule = {

    scheduleId: getVal("scheduleId"),

    busId: getVal("scheduleBusId"),

    routeId: getVal("scheduleRouteId"),

    sourceName: getVal("sourceName"),

    destinationName: getVal("destinationName"),

    departureTime: formatTime(getVal("departureTime")),

    arrivalTime: formatTime(getVal("arrivalTime")),

    operatingDays: selectedDays

};

  try {
    const res = await fetch(`${API}/schedule`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(schedule)
    });

    if (!res.ok) {
      const errorText = await res.text();
      throw new Error(errorText || "Failed to add schedule");
    }

    alert("Schedule Added Successfully");
    clearSchedule();
    loadSchedule();
  } catch (err) {
    alert(`Error: ${err.message}`);
  }
}

function editSchedule(schedule){

    setVal("scheduleId", schedule.scheduleId);

    setVal("scheduleBusId", schedule.busId);

    setVal("scheduleRouteId", schedule.routeId);

    setVal("sourceName", schedule.sourceName);

    setVal("destinationName", schedule.destinationName);

    setVal("departureTime", schedule.departureTime);

    setVal("arrivalTime", schedule.arrivalTime);

    setSelectedDays(schedule.operatingDays);

}
async function updateSchedule() {
  const selectedDays = getSelectedDays();

  if (selectedDays.length === 0) {
    alert("Please select at least one operating day.");
    return;
  }

  const schedule = {

    scheduleId: getVal("scheduleId"),

    busId: getVal("scheduleBusId"),

    routeId: getVal("scheduleRouteId"),

    sourceName: getVal("sourceName"),

    destinationName: getVal("destinationName"),

    departureTime: formatTime(getVal("departureTime")),

    arrivalTime: formatTime(getVal("arrivalTime")),

    operatingDays: selectedDays

};

  try {
    const res = await fetch(`${API}/schedule`, {
      method: "PUT",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(schedule)
    });

    if (!res.ok) {
      const errorText = await res.text();
      throw new Error(errorText || "Failed to update schedule");
    }

    alert("Schedule Updated Successfully");
    clearSchedule();
    loadSchedule();
  } catch (err) {
    alert(`Error: ${err.message}`);
  }
}

async function deleteSchedule() {
  const id = getVal("scheduleId");
  if (!id) return alert("Please specify a Schedule ID to delete.");

  try {
    const res = await fetch(`${API}/schedule/${id}`, { method: "DELETE" });

    if (!res.ok) {
      const errorText = await res.text();
      throw new Error(errorText || "Failed to delete schedule");
    }

    alert("Schedule Deleted Successfully");
    clearSchedule();
    loadSchedule();
  } catch (err) {
    alert(`Error: ${err.message}`);
  }
}

function clearSchedule() {
  setVal("scheduleId", "");
  setVal("departureTime", "");
  setVal("arrivalTime", "");
  setVal("sourceName","");
 setVal("destinationName","");

  const busSelect = document.getElementById("scheduleBusId");
  if (busSelect) busSelect.selectedIndex = 0;

  const routeSelect = document.getElementById("scheduleRouteId");
  if (routeSelect) routeSelect.selectedIndex = 0;

  document.querySelectorAll('input[name="operatingDays"]').forEach((cb) => {
    cb.checked = false;
  });
}

/* ===========================
   INITIAL LOAD
=========================== */

window.addEventListener("DOMContentLoaded", () => {
  loadBus();
  loadRouteDropdownForBus();
  loadStop();
  loadRoute();
  loadStopsForRoute();
  loadSchedule();
  loadBusDropdown();
  loadRouteDropdown();
});
