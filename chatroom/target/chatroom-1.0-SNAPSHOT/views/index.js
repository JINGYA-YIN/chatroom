// 全局变量
const CONTEXT_PATH = window.CONTEXT_PATH || "";
const API_MESSAGE_URL = CONTEXT_PATH + "/api/v1/message";
const LOGOUT_URL = CONTEXT_PATH + "/logout";

const chatBox = document.querySelector("#chat-box");
const sendForm = document.querySelector("#send-form");
const messageInput = document.querySelector("#message");
const logoutBtn = document.querySelector("#logout-btn");
const onlineUsersList = document.querySelector("#online-users");

let lastTimestamp = window.lastTimestamp || 0;
let currentUsername = window.currentUsername || "";
let privateReceiver = null;
let refreshInterval = null;

// 格式化时间
function formatTimestamp(ts) {
    return new Date(ts).toLocaleTimeString();
}

// 渲染消息
function renderMessages(msgs) {
    if (!chatBox) return;
    const shouldScroll = chatBox.scrollHeight - chatBox.scrollTop - chatBox.clientHeight < 50;
    const fragment = document.createDocumentFragment();

    msgs.forEach(m => {
        const div = document.createElement("div");
        let contentHtml = "";
        const time = formatTimestamp(m.timestamp);
        const classes = ["message"];

        if (m.type === "SYSTEM") {
            classes.push("system-msg");
            contentHtml = `<b>[系统]</b>: ${m.content}`;
        } else if (m.type === "PRIVATE") {
            classes.push(m.sender === currentUsername ? "sent" : "received");
            contentHtml = `<b>${m.sender}</b> 私聊: ${m.content}`;
        } else if (m.sender === currentUsername) {
            classes.push("self-msg");
            contentHtml = `<b>你</b>: ${m.content}`;
        } else {
            contentHtml = `<b>${m.sender}</b>: ${m.content}`;
        }

        div.className = classes.join(" ");
        div.innerHTML = `${contentHtml}<div class="meta">${time}</div>`;
        fragment.appendChild(div);
    });

    chatBox.appendChild(fragment);
    if (shouldScroll) chatBox.scrollTop = chatBox.scrollHeight;
    if (msgs.length > 0) lastTimestamp = msgs[msgs.length - 1].timestamp;
}

// 渲染在线用户
function renderOnlineUsers(users) {
    if (!onlineUsersList) return;
    onlineUsersList.innerHTML = "";
    users.forEach(u => {
        if (u === currentUsername) return;
        const li = document.createElement("li");
        li.textContent = u;
        li.dataset.username = u;
        li.className = "online-user-item";
        if (u === privateReceiver) li.classList.add("active-private-chat");
        onlineUsersList.appendChild(li);
    });
}

// 点击用户切换私聊
onlineUsersList?.addEventListener("click", e => {
    const target = e.target;
    if (target.tagName !== "LI") return;
    const user = target.dataset.username;
    privateReceiver = privateReceiver === user ? null : user;
    updateUserListHighlight();
    updateInputPlaceholder();
});

function updateUserListHighlight() {
    onlineUsersList.querySelectorAll("li").forEach(li => {
        li.classList.remove("active-private-chat");
        if (li.dataset.username === privateReceiver) li.classList.add("active-private-chat");
    });
}

function updateInputPlaceholder() {
    if (messageInput) {
        messageInput.placeholder = privateReceiver
            ? `正在私聊 ${privateReceiver} (点击退出)`
            : "输入文字 (点击用户私聊)";
    }
}

// 发送消息
sendForm?.addEventListener("submit", async e => {
    e.preventDefault();
    const content = messageInput.value.trim();
    if (!content) return;

    try {
        const res = await fetch(API_MESSAGE_URL, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            credentials: "include",
            body: JSON.stringify({ content, receiver: privateReceiver })
        });

        if (res.status === 201) {
            messageInput.value = "";
            loadMessages(true);
        } else {
            alert("发送失败");
        }
    } catch (err) {
        console.error(err);
    }
});

// 定时刷新消息
async function loadMessages(useTimestamp = true) {
    let url = API_MESSAGE_URL;
    if (useTimestamp && lastTimestamp > 0) url += `?timestamp=${lastTimestamp}`;

    try {
        const res = await fetch(url, { credentials: "include" });
        if (res.status === 401) {
            alert("会话过期");
            window.location.href = CONTEXT_PATH + "/views/index.jsp";
            return;
        }
        const data = await res.json();
        renderMessages(data.messages || []);
        renderOnlineUsers(Array.from(data.onlineUsers || []));
    } catch (err) {
        console.error(err);
    }
}

// 登出
logoutBtn?.addEventListener("click", async () => {
    try {
        await fetch(LOGOUT_URL, { method: "POST", credentials: "include" });
    } catch (err) {
        console.error(err);
    } finally {
        window.location.href = CONTEXT_PATH + "/views/index.jsp";
    }
});

// 初始化
document.addEventListener("DOMContentLoaded", () => {
    renderMessages(window.messages || []);
    renderOnlineUsers(window.onlineUsers || []);
    refreshInterval = setInterval(() => loadMessages(true), 1500);
});
