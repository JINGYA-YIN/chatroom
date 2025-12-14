<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<fmt:setLocale value="zh_CN" />
<html>
<head>
  <title>在线聊天室 - ${currentUsername}</title>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/views/style.css">

  <script>
    // 全局变量
    window.CONTEXT_PATH = "${pageContext.request.contextPath}";
    window.currentUsername = "${currentUsername}";
    window.lastTimestamp = 0;

    window.messages = [];
    <c:forEach var="msg" items="${messages}">
    window.messages.push({
      sender: "${msg.sender}",
      receiver: "${msg.receiver}",
      content: "${msg.content}",
      timestamp: ${msg.timestamp},
      type: "${msg.type}"
    });
    window.lastTimestamp = ${msg.timestamp};
    </c:forEach>

    window.onlineUsers = [];
    <c:forEach var="user" items="${onlineUsers}">
    <c:if test="${user != currentUsername}">
    window.onlineUsers.push("${user}");
    </c:if>
    </c:forEach>
  </script>
</head>
<body>
<div id="welcome-bar">
  <span id="username-display">欢迎你, ${currentUsername}</span>
  <button id="logout-btn">登出</button>
</div>

<div id="chat-container">
  <div id="user-list">
    <h3>在线用户</h3>
    <ul id="online-users"></ul>
  </div>

  <div id="chat-area">
    <div id="chat-box"></div>

    <form id="send-form">
      <input id="message" type="text" placeholder="输入文字 (点击用户私聊)" required />
      <button type="submit">发送</button>
    </form>
  </div>
</div>

<script src="${pageContext.request.contextPath}/views/index.js"></script>
</body>
</html>
