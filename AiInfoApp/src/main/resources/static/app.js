// app.js
// (am scos complet partea cu base url, fiindca nu mai ai pill-ul in html)

/*
  afiseaza DOAR erorile.
  backend ideal trimite: { "message": "..." }
*/
function show(elId, status, data) {
    const el = document.getElementById(elId);
    if (!el) return;

    // succes -> nu afisam nimic
    if (String(status).startsWith("2")) {
        el.textContent = "";
        return;
    }

    let msg = null;

    if (status === "fetch-error") {
        msg = "cannot reach server (check if backend is running)";
    } else if (status === 401) {
        msg = "unauthorized (login again)";
    } else if (status === 403) {
        msg = "forbidden (you don't have access)";
    } else if (data && typeof data === "object") {
        msg = data.message || data.error || data.detail || null;
    } else if (typeof data === "string") {
        msg = data;
    }

    el.textContent = msg || "unexpected error";
}

async function readBody(res) {
    const ct = res.headers.get("content-type") || "";
    try {
        if (ct.includes("application/json")) return await res.json();
        return await res.text();
    } catch (e) {
        return { message: "cannot read response body: " + String(e) };
    }
}

// ---------------- session (cu basic auth) ----------------
function saveSession(user, email, password) {
    const obj = { ...user, _authEmail: email, _authPass: password };
    localStorage.setItem("aic_user", JSON.stringify(obj));
}
function loadSession() {
    const raw = localStorage.getItem("aic_user");
    if (!raw) return null;
    try {
        return JSON.parse(raw);
    } catch {
        return null;
    }
}
function clearSession() {
    localStorage.removeItem("aic_user");
}
function userIdOf(u) {
    return Number(u?.id ?? 0);
}

// header basic auth (pentru /api/student/** si /api/admin/**)
function authHeader() {
    const u = loadSession();
    if (!u || !u._authEmail || !u._authPass) return {};
    const token = btoa(`${u._authEmail}:${u._authPass}`);
    return { Authorization: `Basic ${token}` };
}

async function getJson(path) {
    try {
        const res = await fetch(path, { headers: { ...authHeader() } });
        const data = await readBody(res);
        return { status: res.status, data };
    } catch (err) {
        return { status: "fetch-error", data: String(err) };
    }
}

async function postJson(path, payload, method = "POST") {
    try {
        const res = await fetch(path, {
            method,
            headers: { "Content-Type": "application/json", ...authHeader() },
            body: payload ? JSON.stringify(payload) : null,
        });
        const data = await readBody(res);
        return { status: res.status, data };
    } catch (err) {
        return { status: "fetch-error", data: String(err) };
    }
}

async function del(path) {
    try {
        const res = await fetch(path, { method: "DELETE", headers: { ...authHeader() } });
        const data = await readBody(res);
        return { status: res.status, data };
    } catch (err) {
        return { status: "fetch-error", data: String(err) };
    }
}

// ---------------- tabs ----------------
const tabLogin = document.getElementById("tabLogin");
const tabRegister = document.getElementById("tabRegister");
const loginBox = document.getElementById("loginBox");
const registerBox = document.getElementById("registerBox");

function activateTab(which) {
    if (!tabLogin || !tabRegister || !loginBox || !registerBox) return;

    if (which === "login") {
        tabLogin.classList.add("active");
        tabRegister.classList.remove("active");
        loginBox.classList.remove("hidden");
        registerBox.classList.add("hidden");
    } else {
        tabRegister.classList.add("active");
        tabLogin.classList.remove("active");
        registerBox.classList.remove("hidden");
        loginBox.classList.add("hidden");
    }
}

tabLogin?.addEventListener("click", () => activateTab("login"));
tabRegister?.addEventListener("click", () => activateTab("register"));

// hide student fields if role != STUDENT
const regRole = document.getElementById("regRole");
const studentFields = document.getElementById("studentFields");

function updateStudentFieldsVisibility() {
    if (!regRole || !studentFields) return;

    const role = regRole.value;
    if (role === "STUDENT") studentFields.classList.remove("hidden");
    else {
        studentFields.classList.add("hidden");
        const f = document.getElementById("regFaculty");
        const y = document.getElementById("regYearOfStudy");
        if (f) f.value = "";
        if (y) y.value = "";
    }
}
regRole?.addEventListener("change", updateStudentFieldsVisibility);
updateStudentFieldsVisibility();

// ---------------- views helpers ----------------
function showDashboardFor(user) {
    document.getElementById("authView")?.classList.add("hidden");
    document.getElementById("dashView")?.classList.remove("hidden");

    const who = document.getElementById("whoami");
    if (who) who.textContent = `${user.name || "?"} (${user.role || "?"}) id=${userIdOf(user) || "?"}`;

    document.getElementById("studentView")?.classList.add("hidden");
    document.getElementById("adminView")?.classList.add("hidden");

    if (user.role === "STUDENT") document.getElementById("studentView")?.classList.remove("hidden");
    if (user.role === "ADMIN") document.getElementById("adminView")?.classList.remove("hidden");

    if (user.role === "STUDENT") openStudentMenu();

    show("loginResult", 200, {});
    show("registerResult", 200, {});
}

function showAuth() {
    document.getElementById("dashView")?.classList.add("hidden");
    document.getElementById("authView")?.classList.remove("hidden");
    activateTab("login");
}

// ---------------- student mini pages ----------------
function openStudentMenu() {
    document.getElementById("studentMenu")?.classList.remove("hidden");
    document.getElementById("studentConvView")?.classList.add("hidden");
    document.getElementById("studentReqView")?.classList.add("hidden");
}

function openStudentConversations() {
    document.getElementById("studentMenu")?.classList.add("hidden");
    document.getElementById("studentConvView")?.classList.remove("hidden");
    document.getElementById("studentReqView")?.classList.add("hidden");
}

function openStudentRequests() {
    document.getElementById("studentMenu")?.classList.add("hidden");
    document.getElementById("studentConvView")?.classList.add("hidden");
    document.getElementById("studentReqView")?.classList.remove("hidden");
}

document.getElementById("goConv")?.addEventListener("click", () => {
    openStudentConversations();
    loadConversations();
});
document.getElementById("goReq")?.addEventListener("click", async () => {
    openStudentRequests();
    await loadMyRequests();
});

document.getElementById("backToMenu1")?.addEventListener("click", openStudentMenu);
document.getElementById("backToMenu2")?.addEventListener("click", openStudentMenu);

// init from session
const existing = loadSession();
if (existing) showDashboardFor(existing);

// logout
document.getElementById("btnLogout")?.addEventListener("click", () => {
    clearSession();

    document.getElementById("myReqList")?.replaceChildren();
    document.getElementById("convList")?.replaceChildren();
    document.getElementById("msgList")?.replaceChildren();

    document.getElementById("studentList")?.replaceChildren();
    document.getElementById("adminReqList")?.replaceChildren();
    document.getElementById("adminConvList")?.replaceChildren();
    document.getElementById("adminMsgList")?.replaceChildren();

    showAuth();
});

// ---------------- REGISTER ----------------
document.getElementById("btnRegister")?.addEventListener("click", async () => {
    const role = document.getElementById("regRole")?.value || "STUDENT";

    const payload = {
        name: (document.getElementById("regName")?.value || "").trim(),
        email: (document.getElementById("regEmail")?.value || "").trim(),
        password: document.getElementById("regPassword")?.value || "",
        role,
    };

    if (!payload.name || !payload.email || !payload.password) {
        return show("registerResult", 400, { message: "fill name/email/password" });
    }

    if (role === "STUDENT") {
        payload.faculty = (document.getElementById("regFaculty")?.value || "").trim();
        const y = (document.getElementById("regYearOfStudy")?.value || "").trim();
        payload.yearOfStudy = y === "" ? null : Number(y);

        if (!payload.faculty || !payload.yearOfStudy) {
            return show("registerResult", 400, { message: "for student: fill faculty + year" });
        }
    }

    const r = await postJson("/api/auth/register", payload);
    show("registerResult", r.status, r.data);

    if (String(r.status).startsWith("2")) {
        activateTab("login");
        const le = document.getElementById("loginEmail");
        const lp = document.getElementById("loginPassword");
        if (le) le.value = payload.email;
        if (lp) lp.value = payload.password;
    }
});

// ---------------- LOGIN ----------------
document.getElementById("btnLogin")?.addEventListener("click", async () => {
    const payload = {
        email: (document.getElementById("loginEmail")?.value || "").trim(),
        password: document.getElementById("loginPassword")?.value || "",
    };

    if (!payload.email || !payload.password) {
        return show("loginResult", 400, { message: "fill email + password" });
    }

    const r = await postJson("/api/auth/login", payload);
    show("loginResult", r.status, r.data);

    if (String(r.status).startsWith("2") && r.data && typeof r.data === "object") {
        saveSession(r.data, payload.email, payload.password);
        showDashboardFor(loadSession());
    }
});

// ---------------- helpers ----------------
function escapeHtml(str) {
    return String(str).replaceAll("&", "&amp;").replaceAll("<", "&lt;").replaceAll(">", "&gt;");
}

// ======================================================================
// ======================= STUDENT: conversations ========================
// ======================================================================
let currentConversationId = null;

function setCurrentConv(id, title) {
    currentConversationId = id;
    const el = document.getElementById("currentConv");
    if (el) el.textContent = id ? `${title} (#${id})` : "none";
}

async function loadConversations() {
    const user = loadSession();
    if (!user) return;

    const r = await getJson(`/api/student/conversations?studentId=${userIdOf(user)}`);
    show("convResult", r.status, r.data);

    const list = document.getElementById("convList");
    if (!list) return;
    list.innerHTML = "";

    if (!String(r.status).startsWith("2") || !Array.isArray(r.data)) return;

    r.data.forEach((c) => {
        const div = document.createElement("div");
        div.className = "item";

        div.innerHTML = `
      <div class="itemTop">
        <div>
          <b>${escapeHtml(c.title)} (#${c.id})</b>
          <div class="muted">${escapeHtml(c.createdAt || "")}</div>
        </div>
        <div class="itemRight">
          <button type="button" data-open="1">open</button>
          <button type="button" data-del="1">delete</button>
        </div>
      </div>
    `;

        div.querySelector('button[data-open="1"]').addEventListener("click", async () => {
            setCurrentConv(c.id, c.title);
            document.getElementById("threadsPage")?.classList.add("hidden");
            document.getElementById("conversationPage")?.classList.remove("hidden");
            await loadMessages();
        });

        div.querySelector('button[data-del="1"]').addEventListener("click", async () => {
            const rr = await del(`/api/student/conversations/${c.id}?studentId=${userIdOf(user)}`);
            show("convResult", rr.status, rr.data);

            if (String(rr.status).startsWith("2")) {
                if (currentConversationId === c.id) {
                    setCurrentConv(null, "");
                    document.getElementById("msgList") && (document.getElementById("msgList").innerHTML = "");
                }
                await loadConversations();
            }
        });

        list.appendChild(div);
    });
}

async function loadMessages() {
    const user = loadSession();
    if (!user) return show("msgResult", 400, { message: "please login first" });
    if (!currentConversationId) return show("msgResult", 400, { message: "select a conversation first" });

    const r = await getJson(
        `/api/student/conversations/${currentConversationId}/messages?studentId=${userIdOf(user)}`
    );
    show("msgResult", r.status, r.data);

    const list = document.getElementById("msgList");
    if (!list) return;
    list.innerHTML = "";

    if (!String(r.status).startsWith("2") || !Array.isArray(r.data)) return;

    r.data.forEach((m) => {
        const div = document.createElement("div");
        div.className = "item";

        let contentHtml;
        if (m.sender === "AI") contentHtml = marked.parse(m.text || "");
        else contentHtml = `<div>${escapeHtml(m.text)}</div>`;

        div.innerHTML = `
      <b>${escapeHtml(m.sender)}</b>
      <div class="msgContent">${contentHtml}</div>
      <div class="muted">${escapeHtml(m.createdAt || "")}</div>
    `;

        list.appendChild(div);
    });
}

document.getElementById("btnCreateConv")?.addEventListener("click", async () => {
    const user = loadSession();
    if (!user) return show("convResult", 400, { message: "please login first" });

    const title = (document.getElementById("convTitle")?.value || "").trim();
    if (!title) return show("convResult", 400, { message: "title required" });

    const payload = { studentId: userIdOf(user), title };
    const r = await postJson("/api/student/conversations", payload);
    show("convResult", r.status, r.data);

    if (String(r.status).startsWith("2") && r.data?.id) {
        setCurrentConv(r.data.id, r.data.title);
        await loadConversations();
        await loadMessages();
    }
});

document.getElementById("btnLoadConvs")?.addEventListener("click", loadConversations);

document.getElementById("btnSendMsg")?.addEventListener("click", async () => {
    const user = loadSession();
    if (!user) return show("msgResult", 400, { message: "please login first" });
    if (!currentConversationId) return show("msgResult", 400, { message: "select a conversation first" });

    const msgInput = document.getElementById("msgText");
    const message = (msgInput?.value || "").trim();
    if (!message) return show("msgResult", 400, { message: "message required" });

    const resultEl = document.getElementById("msgResult");
    if (resultEl) resultEl.textContent = "sending to ai...";

    const payload = { studentId: userIdOf(user), message };
    const r = await postJson(`/api/student/conversations/${currentConversationId}/messages`, payload);

    console.log("send response:", r.status, r.data);
    show("msgResult", r.status, r.data);

    if (String(r.status).startsWith("2")) {
        if (msgInput) msgInput.value = "";
        await loadMessages();
    }
});

// ======================================================================
// ========================== STUDENT: requests ==========================
// ======================================================================
const btnOpenReqForm = document.getElementById("btnOpenReqForm");
const reqForm = document.getElementById("reqForm");
const btnCancelReq = document.getElementById("btnCancelReq");

function openReqForm() {
    reqForm?.classList.remove("hidden");
    btnOpenReqForm?.classList.add("hidden");
}

function closeReqForm() {
    reqForm?.classList.add("hidden");
    btnOpenReqForm?.classList.remove("hidden");
    const msg = document.getElementById("reqMsg");
    if (msg) msg.value = "";
    show("reqResult", 200, {});
}

btnOpenReqForm?.addEventListener("click", openReqForm);
btnCancelReq?.addEventListener("click", closeReqForm);

async function loadMyRequests() {
    const user = loadSession();
    if (!user) return show("reqResult", 400, { message: "please login first" });

    const r = await getJson(`/api/student/requests?studentId=${userIdOf(user)}`);
    show("reqResult", r.status, r.data);

    const list = document.getElementById("myReqList");
    if (!list) return;
    list.innerHTML = "";

    if (!String(r.status).startsWith("2") || !Array.isArray(r.data)) return;

    r.data.forEach((req) => {
        const div = document.createElement("div");
        div.className = "item";

        const isRejected = req.status === "REJECTED";

        const downloadLink = req.attachmentName
            ? `<a href="/api/student/requests/${req.id}/attachment?studentId=${userIdOf(user)}">download: ${escapeHtml(
                req.attachmentName
            )}</a>`
            : "";

        div.innerHTML = `
      <b>request #${req.id}</b>
      <div>${escapeHtml(req.message)}</div>
      <div class="muted">status: ${escapeHtml(req.status || "")}</div>
      <div class="muted">${isRejected ? "Request Rejected!" : "admin: " + escapeHtml(req.adminResponse || "-")}</div>
      <div style="margin-top:8px;">${downloadLink}</div>
    `;
        list.appendChild(div);
    });
}

document.getElementById("btnSubmitReq")?.addEventListener("click", async () => {
    const user = loadSession();
    if (!user) return show("reqResult", 400, { message: "please login first" });

    const message = (document.getElementById("reqMsg")?.value || "").trim();
    if (!message) return show("reqResult", 400, { message: "message required" });

    const payload = { studentId: userIdOf(user), message };

    const r = await postJson("/api/student/requests", payload);
    show("reqResult", r.status, r.data);

    if (String(r.status).startsWith("2")) {
        const box = document.getElementById("reqResult");
        if (box) box.textContent = "request sent ✅";

        closeReqForm();
        await loadMyRequests();

        setTimeout(() => {
            const b = document.getElementById("reqResult");
            if (b && b.textContent === "request sent ✅") b.textContent = "";
        }, 2000);
    }
});

document.getElementById("btnMyReqs")?.addEventListener("click", loadMyRequests);

// ======================================================================
// ============================ ADMIN: students ==========================
// ======================================================================
let selectedStudentId = null;

function setSelectedStudent(id) {
    selectedStudentId = id;
    const el = document.getElementById("selectedStudentId");
    if (el) el.textContent = id ? String(id) : "none";
}

document.getElementById("btnLoadStudents")?.addEventListener("click", async () => {
    const r = await getJson("/api/admin/students");
    show("adminStudentResult", r.status, r.data);

    const list = document.getElementById("studentList");
    if (!list) return;
    list.innerHTML = "";

    if (!String(r.status).startsWith("2") || !Array.isArray(r.data)) return;

    r.data.forEach((s) => {
        const div = document.createElement("div");
        div.className = "item";

        div.innerHTML = `
      <div class="itemTop">
        <div>
          <b>${escapeHtml(s.name)} (#${s.id})</b>
          <div class="muted">${escapeHtml(s.email || "")}</div>
        </div>
        <div class="itemRight">
          <button type="button" data-select="1">select</button>
        </div>
      </div>
    `;

        div.querySelector('button[data-select="1"]').addEventListener("click", () => {
            setSelectedStudent(s.id);

            const nameEl = document.getElementById("editStudentName");
            if (nameEl) nameEl.value = s.name || "";

            document.getElementById("adminReqList")?.replaceChildren();
            document.getElementById("adminConvList")?.replaceChildren();
            document.getElementById("adminMsgList")?.replaceChildren();
            show("adminReqResult", 200, {});
            show("adminConvResult", 200, {});
            show("adminMsgResult", 200, {});
        });

        list.appendChild(div);
    });
});

document.getElementById("btnSaveStudent")?.addEventListener("click", async () => {
    if (!selectedStudentId) return show("adminEditResult", 400, { message: "select student first" });

    const payload = { name: (document.getElementById("editStudentName")?.value || "").trim() };
    if (!payload.name) return show("adminEditResult", 400, { message: "fill name" });

    const r = await postJson(`/api/admin/students/${selectedStudentId}`, payload, "PUT");
    show("adminEditResult", r.status, r.data);

    if (String(r.status).startsWith("2")) {
        const box = document.getElementById("adminEditResult");
        if (box) box.textContent = "saved ✅";
        setTimeout(() => {
            const b = document.getElementById("adminEditResult");
            if (b && b.textContent === "saved ✅") b.textContent = "";
        }, 1200);

        document.getElementById("btnLoadStudents")?.click();
    }
});

// ======================================================================
// ============================ ADMIN: requests ==========================
// ======================================================================
async function loadRequestsForSelectedStudent() {
    if (!selectedStudentId) {
        document.getElementById("adminReqList")?.replaceChildren();
        return show("adminReqResult", 400, { message: "select student first" });
    }

    const r = await getJson(`/api/admin/students/${selectedStudentId}/requests`);
    show("adminReqResult", r.status, r.data);

    const list = document.getElementById("adminReqList");
    if (!list) return;
    list.innerHTML = "";

    if (!String(r.status).startsWith("2") || !Array.isArray(r.data)) return;

    r.data.forEach((req) => {
        const div = document.createElement("div");
        div.className = "item";

        div.innerHTML = `
      <b>request #${req.id}</b>
      <div>${escapeHtml(req.message)}</div>
      <div class="muted">status: ${escapeHtml(req.status || "")}</div>

      <div class="row" style="margin-top:10px; justify-content:flex-start; gap:10px;">
        <button type="button" data-reject="1" class="btnGhost">reject</button>
        <button type="button" data-respond="1">respond</button>
      </div>

      <div data-respond-box="1" class="hidden" style="margin-top:10px;">
        <label>admin response
          <input type="text" placeholder="write response..." value="${escapeHtml(req.adminResponse || "")}">
        </label>

        <div class="row" style="margin-top:8px; justify-content:flex-start; gap:10px;">
          <button type="button" data-send="1">send</button>
          <button type="button" data-cancel="1" class="btnGhost">cancel</button>
        </div>
      </div>

      <div style="margin-top:10px;">
        <div class="muted">attachment: ${req.attachmentName ? escapeHtml(req.attachmentName) : "-"}</div>

	        <div class="row" style="justify-content:flex-start; gap:10px; margin-top:6px;">
	          <input type="file" id="file-${req.id}" data-file="1">
	          <button type="button" data-upload="1">upload document</button>
	        </div>

        <div class="muted" data-upload-status="1" style="margin-top:6px;"></div>
      </div>

      <div data-reject-msg="1" class="muted hidden" style="margin-top:10px;">
        Request Rejected!
      </div>
    `;

        const respondBox = div.querySelector('[data-respond-box="1"]');
        const rejectMsg = div.querySelector('[data-reject-msg="1"]');
        const responseInput = respondBox?.querySelector("input");

	        // luam exact input-ul de fisier din cardul curent (id unic per request)
	        const fileInput = div.querySelector(`#file-${req.id}`);
        const uploadBtn = div.querySelector('[data-upload="1"]');
        const uploadStatusEl = div.querySelector('[data-upload-status="1"]');

        // show respond UI
        div.querySelector('button[data-respond="1"]').addEventListener("click", () => {
            respondBox?.classList.remove("hidden");
            rejectMsg?.classList.add("hidden");
        });

        // cancel respond UI
        div.querySelector('button[data-cancel="1"]').addEventListener("click", () => {
            respondBox?.classList.add("hidden");
        });

        // send respond -> ANSWERED
        div.querySelector('button[data-send="1"]').addEventListener("click", async () => {
            const payload = {
                adminResponse: (responseInput?.value || "").trim() || "Answered.",
                status: "ANSWERED",
            };
            const rr = await postJson(`/api/admin/requests/${req.id}/respond`, payload);
            show("adminReqResult", rr.status, rr.data);

            if (String(rr.status).startsWith("2")) {
                respondBox?.classList.add("hidden");
                await loadRequestsForSelectedStudent();
            }
        });

        // reject -> REJECTED
        div.querySelector('button[data-reject="1"]').addEventListener("click", async () => {
            const payload = { adminResponse: "Request Rejected!", status: "REJECTED" };
            const rr = await postJson(`/api/admin/requests/${req.id}/respond`, payload);
            show("adminReqResult", rr.status, rr.data);

            if (String(rr.status).startsWith("2")) {
                respondBox?.classList.add("hidden");
                rejectMsg?.classList.remove("hidden");
                await loadRequestsForSelectedStudent();
            }
        });

	        // upload attachment (admin) - FIX
	        uploadBtn.addEventListener("click", async () => {
	            // IMPORTANT: citim fisierul direct din input-ul acestui request
	            const f = fileInput?.files?.[0];
            if (!f) {
                if (uploadStatusEl) uploadStatusEl.textContent = "select a file first";
                return;
            }

            if (uploadStatusEl) uploadStatusEl.textContent = "uploading...";
            uploadBtn.disabled = true;

            const fd = new FormData();
            fd.append("file", f, f.name);

            try {
                const res = await fetch(`/api/admin/requests/${req.id}/attachment`, {
                    method: "POST",
                    headers: { ...authHeader() }, // NU pune Content-Type
                    body: fd
                });

                const data = await readBody(res);

                if (!String(res.status).startsWith("2")) {
                    const msg =
                        data && typeof data === "object"
                            ? data.message || JSON.stringify(data)
                            : String(data);
                    if (uploadStatusEl) uploadStatusEl.textContent = "upload failed: " + msg;
                    return;
                }

                if (uploadStatusEl) uploadStatusEl.textContent = "uploaded ✅";
                await loadRequestsForSelectedStudent();

            } catch (e) {
                if (uploadStatusEl) uploadStatusEl.textContent = "upload failed: " + String(e);
            } finally {
                uploadBtn.disabled = false;
            }
        });


        list.appendChild(div);
    });
}

// load requests button (for selected student)
document.getElementById("btnLoadAllReq")?.addEventListener("click", loadRequestsForSelectedStudent);

// ======================================================================
// ===================== ADMIN: conversations (read-only) ================
// ======================================================================
let adminCurrentConversationId = null;

function setAdminCurrentConv(id, title) {
    adminCurrentConversationId = id;
    const el = document.getElementById("adminCurrentConv");
    if (el) el.textContent = id ? `${title} (#${id})` : "none";
}

async function loadAdminConversations() {
    if (!selectedStudentId) {
        document.getElementById("adminConvList")?.replaceChildren();
        setAdminCurrentConv(null, "");
        document.getElementById("adminMsgList")?.replaceChildren();
        return show("adminConvResult", 400, { message: "select student first" });
    }

    const r = await getJson(`/api/admin/students/${selectedStudentId}/conversations`);
    show("adminConvResult", r.status, r.data);

    const list = document.getElementById("adminConvList");
    if (!list) return;
    list.innerHTML = "";

    if (!String(r.status).startsWith("2") || !Array.isArray(r.data)) return;

    r.data.forEach((c) => {
        const div = document.createElement("div");
        div.className = "item";

        div.innerHTML = `
      <div class="itemTop">
        <div>
          <b>${escapeHtml(c.title || "conversation")} (#${c.id})</b>
          <div class="muted">${escapeHtml(c.createdAt || "")}</div>
        </div>
        <div class="itemRight">
          <button type="button" data-open="1">open</button>
        </div>
      </div>
    `;

        div.querySelector('button[data-open="1"]').addEventListener("click", async () => {
            setAdminCurrentConv(c.id, c.title || "conversation");
            await loadAdminMessages();
        });

        list.appendChild(div);
    });
}

async function loadAdminMessages() {
    if (!selectedStudentId) return show("adminMsgResult", 400, { message: "select student first" });
    if (!adminCurrentConversationId) return show("adminMsgResult", 400, { message: "select conversation first" });

    const r = await getJson(
        `/api/admin/students/${selectedStudentId}/conversations/${adminCurrentConversationId}/messages`
    );
    show("adminMsgResult", r.status, r.data);

    const list = document.getElementById("adminMsgList");
    if (!list) return;
    list.innerHTML = "";

    if (!String(r.status).startsWith("2") || !Array.isArray(r.data)) return;

    r.data.forEach((m) => {
        const div = document.createElement("div");
        div.className = "item";
        div.innerHTML = `
      <b>${escapeHtml(m.sender || "")}</b>
      <div>${m.sender === "AI" ? marked.parse(m.text || "") : escapeHtml(m.text || "")}</div>
      <div class="muted">${escapeHtml(m.createdAt || "")}</div>
    `;
        list.appendChild(div);
    });
}

// load conversations button (admin)
document.getElementById("btnAdminLoadConvs")?.addEventListener("click", loadAdminConversations);

// back button (student conversation)
document.getElementById("btnBackToThreads")?.addEventListener("click", () => {
    document.getElementById("conversationPage")?.classList.add("hidden");
    document.getElementById("threadsPage")?.classList.remove("hidden");
});
