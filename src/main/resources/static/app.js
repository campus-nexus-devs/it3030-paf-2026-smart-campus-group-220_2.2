const API_BASE = "/api/resources";

const form = document.getElementById("resource-form");
const cancelEditBtn = document.getElementById("cancel-edit");
const tbody = document.getElementById("resource-table-body");
const statusText = document.getElementById("status-text");

const applyFiltersBtn = document.getElementById("apply-filters");
const clearFiltersBtn = document.getElementById("clear-filters");

if (!form || !cancelEditBtn || !tbody || !statusText || !applyFiltersBtn || !clearFiltersBtn) {
    throw new Error("Resource page elements are missing.");
}

async function fetchResources() {
    const params = buildFilterParams();
    const url = params.toString() ? `${API_BASE}?${params.toString()}` : API_BASE;

    const response = await fetch(url);
    if (!response.ok) {
        throw new Error("Failed to fetch resources");
    }
    const resources = await response.json();
    renderResources(resources);
    statusText.textContent = `Loaded ${resources.length} resource(s)`;
}

function buildFilterParams() {
    const params = new URLSearchParams();
    addParamIfPresent(params, "name", document.getElementById("filter-name").value);
    addParamIfPresent(params, "type", document.getElementById("filter-type").value);
    addParamIfPresent(params, "minCapacity", document.getElementById("filter-min-capacity").value);
    addParamIfPresent(params, "maxCapacity", document.getElementById("filter-max-capacity").value);
    addParamIfPresent(params, "wifiAvailable", document.getElementById("filter-wifi").value);
    addParamIfPresent(params, "acAvailable", document.getElementById("filter-ac").value);
    return params;
}

function addParamIfPresent(params, key, value) {
    if (value !== null && value !== undefined && value !== "") {
        params.append(key, value);
    }
}

function renderResources(resources) {
    tbody.innerHTML = "";

    resources.forEach((resource) => {
        const row = document.createElement("tr");
        row.innerHTML = `
            <td>${resource.id}</td>
            <td>${resource.name}</td>
            <td>${resource.type}</td>
            <td>${resource.location}</td>
            <td>${resource.capacity}</td>
            <td>${resource.wifiAvailable ? "Yes" : "No"}</td>
            <td>${resource.acAvailable ? "Yes" : "No"}</td>
            <td>
                <button data-id="${resource.id}" class="edit-btn">Edit</button>
                <button data-id="${resource.id}" class="delete-btn secondary">Delete</button>
            </td>
        `;
        tbody.appendChild(row);
    });

    wireRowButtons(resources);
}

function wireRowButtons(resources) {
    document.querySelectorAll(".edit-btn").forEach((button) => {
        button.addEventListener("click", () => {
            const id = Number(button.dataset.id);
            const resource = resources.find((r) => r.id === id);
            if (resource) {
                fillFormForEdit(resource);
            }
        });
    });

    document.querySelectorAll(".delete-btn").forEach((button) => {
        button.addEventListener("click", async () => {
            const id = Number(button.dataset.id);
            if (confirm("Delete this resource?")) {
                await deleteResource(id);
            }
        });
    });
}

function fillFormForEdit(resource) {
    document.getElementById("resource-id").value = resource.id;
    document.getElementById("name").value = resource.name;
    document.getElementById("type").value = resource.type;
    document.getElementById("location").value = resource.location;
    document.getElementById("capacity").value = resource.capacity;
    document.getElementById("wifiAvailable").value = String(resource.wifiAvailable);
    document.getElementById("acAvailable").value = String(resource.acAvailable);
    statusText.textContent = `Editing resource #${resource.id}`;
}

function clearForm() {
    form.reset();
    document.getElementById("resource-id").value = "";
}

function getPayload() {
    return {
        name: document.getElementById("name").value.trim(),
        type: document.getElementById("type").value,
        location: document.getElementById("location").value.trim(),
        capacity: Number(document.getElementById("capacity").value),
        wifiAvailable: document.getElementById("wifiAvailable").value === "true",
        acAvailable: document.getElementById("acAvailable").value === "true"
    };
}

async function saveResource(event) {
    event.preventDefault();
    const id = document.getElementById("resource-id").value;
    const payload = getPayload();

    const isEdit = id !== "";
    const url = isEdit ? `${API_BASE}/${id}` : API_BASE;
    const method = isEdit ? "PUT" : "POST";

    const response = await fetch(url, {
        method,
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(payload)
    });

    if (!response.ok) {
        const errorText = await response.text();
        throw new Error(`Failed to save resource: ${errorText}`);
    }

    clearForm();
    await fetchResources();
}

async function deleteResource(id) {
    const response = await fetch(`${API_BASE}/${id}`, { method: "DELETE" });
    if (!response.ok) {
        throw new Error("Failed to delete resource");
    }
    await fetchResources();
}

function clearFilters() {
    document.getElementById("filter-form").reset();
}

form.addEventListener("submit", async (event) => {
    try {
        await saveResource(event);
    } catch (error) {
        statusText.textContent = error.message;
    }
});

cancelEditBtn.addEventListener("click", () => {
    clearForm();
    statusText.textContent = "Edit cancelled";
});

applyFiltersBtn.addEventListener("click", async () => {
    try {
        await fetchResources();
    } catch (error) {
        statusText.textContent = error.message;
    }
});

clearFiltersBtn.addEventListener("click", async () => {
    clearFilters();
    try {
        await fetchResources();
    } catch (error) {
        statusText.textContent = error.message;
    }
});

window.addEventListener("load", async () => {
    try {
        await fetchResources();
    } catch (error) {
        statusText.textContent = error.message;
    }
});
