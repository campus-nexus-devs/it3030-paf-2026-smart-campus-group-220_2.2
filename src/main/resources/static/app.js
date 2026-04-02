const API_BASE = "/api/resources";
const BROWSE_ONLY = window.RESOURCE_PAGE_MODE === "browse";
const ADMIN_ADD = window.RESOURCE_ADMIN_PAGE === "add";
const ADMIN_LIST = window.RESOURCE_ADMIN_PAGE === "list";

const form = document.getElementById("resource-form");
const cancelEditBtn = document.getElementById("cancel-edit");
const tbody = document.getElementById("resource-table-body");
const statusText = document.getElementById("status-text");
const formStatus = document.getElementById("form-status");

const applyFiltersBtn = document.getElementById("apply-filters");
const clearFiltersBtn = document.getElementById("clear-filters");

function setListStatus(msg) {
    if (statusText) {
        statusText.textContent = msg;
    }
}

function setFormStatus(msg) {
    if (formStatus) {
        formStatus.textContent = msg;
    }
}

if (BROWSE_ONLY || ADMIN_LIST) {
    if (!tbody || !statusText || !applyFiltersBtn || !clearFiltersBtn) {
        throw new Error("Resource list page elements are missing.");
    }
}
if (ADMIN_ADD) {
    if (!form || !cancelEditBtn || !formStatus) {
        throw new Error("Admin add-resource form elements are missing.");
    }
}

const CATEGORY_LABELS = {
    STAFF_LEARNING: "Learning space (staff)",
    STUDENT_DISCUSSION: "Discussion space (students)",
    HALL_LAB: "Halls & labs",
    PROJECTOR_EQUIPMENT: "Projectors & equipment"
};

function formatCategoryLabel(value) {
    if (!value) {
        return "—";
    }
    return CATEGORY_LABELS[value] || value;
}

function isResourceAdmin() {
    try {
        const raw = localStorage.getItem("smartCampusUser");
        if (!raw) {
            return false;
        }
        return JSON.parse(raw).role === "ADMIN";
    } catch {
        return false;
    }
}

function mergeMutationHeaders(base) {
    const api = window.SmartCampusResourceApi;
    if (api && typeof api.mutationHeaders === "function") {
        return { ...base, ...api.mutationHeaders() };
    }
    return { ...base };
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
    setListStatus(`Loaded ${resources.length} resource(s)`);
}

function buildFilterParams() {
    const params = new URLSearchParams();
    addParamIfPresent(params, "name", document.getElementById("filter-name").value);
    addParamIfPresent(params, "category", document.getElementById("filter-category").value);
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

    const showActions = !BROWSE_ONLY && isResourceAdmin();
    const actionsTh = document.getElementById("resource-actions-th");
    if (actionsTh) {
        actionsTh.style.display = showActions ? "" : "none";
    }

    resources.forEach((resource) => {
        const row = document.createElement("tr");
        const actionsCell = showActions
            ? `<td>
                <button type="button" data-id="${resource.id}" class="edit-btn">Edit</button>
                <button type="button" data-id="${resource.id}" class="delete-btn secondary">Delete</button>
            </td>`
            : "";
        row.innerHTML = `
            <td>${resource.id}</td>
            <td>${resource.name}</td>
            <td>${resource.type}</td>
            <td>${formatCategoryLabel(resource.category)}</td>
            <td>${resource.location}</td>
            <td>${resource.capacity}</td>
            <td>${resource.wifiAvailable ? "Yes" : "No"}</td>
            <td>${resource.acAvailable ? "Yes" : "No"}</td>
            ${actionsCell}
        `;
        tbody.appendChild(row);
    });

    if (showActions) {
        wireRowButtons(resources);
    }
}

function wireRowButtons(resources) {
    document.querySelectorAll(".edit-btn").forEach((button) => {
        button.addEventListener("click", () => {
            const id = Number(button.dataset.id);
            if (ADMIN_LIST) {
                window.location.href = `/admin-add-resource.html?edit=${encodeURIComponent(id)}`;
                return;
            }
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
    if (!form) {
        return;
    }
    document.getElementById("resource-id").value = resource.id;
    document.getElementById("name").value = resource.name;
    document.getElementById("type").value = resource.type;
    const catSelect = document.getElementById("resource-category");
    if (catSelect) {
        catSelect.value = resource.category || "HALL_LAB";
    }
    document.getElementById("location").value = resource.location;
    document.getElementById("capacity").value = resource.capacity;
    document.getElementById("wifiAvailable").value = String(resource.wifiAvailable);
    document.getElementById("acAvailable").value = String(resource.acAvailable);
    setFormStatus(`Editing resource #${resource.id}`);
}

function clearForm(options) {
    if (!form) {
        return;
    }
    const clearStatus = !options || options.clearStatus !== false;
    form.reset();
    const rid = document.getElementById("resource-id");
    if (rid) {
        rid.value = "";
    }
    if (clearStatus) {
        setFormStatus("");
    }
    if (window.history && window.history.replaceState && ADMIN_ADD) {
        window.history.replaceState(null, "", "/admin-add-resource.html");
    }
}

function getPayload() {
    const categoryEl = document.getElementById("resource-category");
    return {
        name: document.getElementById("name").value.trim(),
        type: document.getElementById("type").value,
        category: categoryEl ? categoryEl.value : null,
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
        headers: mergeMutationHeaders({ "Content-Type": "application/json" }),
        body: JSON.stringify(payload)
    });

    if (!response.ok) {
        const errorText = await response.text();
        throw new Error(`Failed to save resource: ${errorText}`);
    }

    clearForm({ clearStatus: false });
    if (ADMIN_LIST || BROWSE_ONLY) {
        await fetchResources();
    } else {
        setFormStatus("Saved successfully. Open Show resources to view the list.");
    }
}

async function deleteResource(id) {
    const response = await fetch(`${API_BASE}/${id}`, {
        method: "DELETE",
        headers: mergeMutationHeaders({})
    });
    if (!response.ok) {
        throw new Error("Failed to delete resource");
    }
    await fetchResources();
}

async function tryLoadEditFromQuery() {
    if (!ADMIN_ADD || !form) {
        return;
    }
    const params = new URLSearchParams(window.location.search);
    const editId = params.get("edit");
    if (!editId) {
        return;
    }
    const response = await fetch(`${API_BASE}/${encodeURIComponent(editId)}`);
    if (!response.ok) {
        setFormStatus("Could not load resource to edit.");
        return;
    }
    const resource = await response.json();
    fillFormForEdit(resource);
}

if (form) {
    form.addEventListener("submit", async (event) => {
        if (!isResourceAdmin()) {
            event.preventDefault();
            setFormStatus("Only administrators can add or change resources.");
            return;
        }
        try {
            await saveResource(event);
        } catch (error) {
            setFormStatus(error.message);
        }
    });
}

if (cancelEditBtn) {
    cancelEditBtn.addEventListener("click", () => {
        clearForm();
        setFormStatus("Edit cancelled.");
    });
}

if (applyFiltersBtn) {
    applyFiltersBtn.addEventListener("click", async () => {
        try {
            await fetchResources();
        } catch (error) {
            setListStatus(error.message);
        }
    });
}

if (clearFiltersBtn) {
    clearFiltersBtn.addEventListener("click", async () => {
        clearFilters();
        document.querySelectorAll(".category-pick").forEach((b) => b.classList.remove("is-active"));
        try {
            await fetchResources();
        } catch (error) {
            setListStatus(error.message);
        }
    });
}

document.querySelectorAll(".category-pick").forEach((btn) => {
    btn.addEventListener("click", async () => {
        const filterCat = document.getElementById("filter-category");
        if (filterCat) {
            filterCat.value = btn.dataset.category ?? "";
        }
        document.querySelectorAll(".category-pick").forEach((b) => b.classList.remove("is-active"));
        btn.classList.add("is-active");
        try {
            await fetchResources();
        } catch (error) {
            setListStatus(error.message);
        }
    });
});

const filterCategoryEl = document.getElementById("filter-category");
if (filterCategoryEl) {
    filterCategoryEl.addEventListener("change", async () => {
        const val = filterCategoryEl.value;
        document.querySelectorAll(".category-pick").forEach((b) => {
            b.classList.toggle("is-active", (b.dataset.category || "") === val);
        });
        try {
            await fetchResources();
        } catch (error) {
            setListStatus(error.message);
        }
    });
}

window.addEventListener("load", async () => {
    try {
        if (ADMIN_LIST || BROWSE_ONLY) {
            await fetchResources();
        }
        if (ADMIN_ADD) {
            await tryLoadEditFromQuery();
        }
    } catch (error) {
        if (statusText) {
            statusText.textContent = error.message;
        }
        if (formStatus) {
            formStatus.textContent = error.message;
        }
    }
});

function clearFilters() {
    const ff = document.getElementById("filter-form");
    if (ff) {
        ff.reset();
    }
}
