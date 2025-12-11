const baseUrl = location.origin + "/chatroom-1.0-SNAPSHOT/";

const chatBox = document.querySelector("#chat-box");
const sendForm = document.querySelector("#send-form");
const messageInput = document.querySelector("#message");
const loginBtn = document.querySelector("#login-btn");
const logoutBtn = document.querySelector("#logout-btn");
const usernameDisplay = document.querySelector("#username-display");

let username = null;
let loadInterval = null;

// 页面加载初始化按钮显示
window.addEventListener("load", () => {
    loginBtn.style.display = "inline-block";
    logoutBtn.style.display = "none";
});

// 登录
loginBtn.addEventListener("click", async () => {
    let inputName = prompt("请输入用户名：");
    if (!inputName) return;

    try {
        const res = await fetch(`${baseUrl}login`, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ username: inputName }),
            credentials: "include" // 保持 session
        });

        if (!res.ok) {
            alert("登录失败！");
            return;
        }

        const data = await res.json();
        username = data.username;
        usernameDisplay.textContent = `欢迎你, ${username}`;
        loginBtn.style.display = "none";
        logoutBtn.style.display = "inline-block";

        await loadMessages();
        loadInterval = setInterval(loadMessages, 1500);

    } catch (err) {
        console.error("登录错误", err);
        alert("登录错误，请检查网络或接口路径！");
    }
});

// 登出
async function logout() {
    try {
        const res = await fetch(`${baseUrl}logout`, {
            method: "POST",
            credentials: "include"
        });
        if (res.ok) {
            username = null;
            usernameDisplay.textContent = "欢迎你, 用户";
            loginBtn.style.display = "inline-block";
            logoutBtn.style.display = "none";
            chatBox.innerHTML = "";
            if (loadInterval) clearInterval(loadInterval);
        } else {
            alert("登出失败！");
        }
    } catch (err) {
        console.error("登出失败", err);
        alert("登出错误，请检查网络！");
    }
}

logoutBtn.addEventListener("click", logout);

// 发送消息
sendForm.addEventListener("submit", async (e) => {
    e.preventDefault();

    if (!username) {
        alert("请先登录！");
        return;
    }

    const text = messageInput.value.trim();
    if (!text) return;

    try {
        const res = await fetch(`${baseUrl}chat`, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ message: text }),
            credentials: "include"
        });

        if (res.ok) {
            messageInput.value = "";
            await loadMessages();
        } else if (res.status === 401) {
            alert("你还没有登录或会话已过期！");
        } else {
            alert("发送消息失败！");
        }
    } catch (err) {
        console.error("发送消息失败", err);
        alert("发送消息错误，请检查网络！");
    }
});

// 获取消息
async function loadMessages() {
    try {
        const res = await fetch(`${baseUrl}chat`, { credentials: "include" });
        if (!res.ok) return;
        const msgs = await res.json();
        chatBox.innerHTML = "";

        msgs.forEach(m => {
            const div = document.createElement("div");
            div.className = "message";
            div.innerHTML = `<b>${m.user}</b>: ${m.text}<div class="meta">${m.timestamp}</div>`;
            chatBox.appendChild(div);
        });

        chatBox.scrollTop = chatBox.scrollHeight;
    } catch (err) {
        console.error("获取消息失败", err);
    }
}
