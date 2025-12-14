<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title>在线聊天室</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/views/style.css">
</head>
<body>
<div id="welcome-bar">
    <span id="username-display">请登录</span>
</div>

<div id="login-form-container">
    <h2>欢迎加入聊天室</h2>
    <form id="login-form" action="${pageContext.request.contextPath}/login" method="post">
        <input type="text" name="username" id="username-input" placeholder="输入用户名" required>
        <button type="submit">登录</button>
        <!-- 显示登录错误 -->
        <div id="error-message" style="color: red; margin-top: 10px;">
            <c:if test="${not empty error}">
                ${error}
            </c:if>
        </div>
    </form>
</div>

</body>
</html>
