<%--
  Created by IntelliJ IDEA.
  User: caisheng
  Date: 2019-08-20
  Time: 21:11
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Title</title>
</head>
<body>

<form action="${pageContext.request.contextPath}/uploadFile" id="domeform" method="post" enctype="multipart/form-data">
    <input type="file" name="file" value="选择文件">
    <%--<input type="submit" value="表单提交">--%>
    <%--<input type="button" value="ajax提交" id="ajaxsub">--%>
    <input type="button" value="提交" id="submitFile">
</form>
<div id="resultMsg"></div>

</body>

<script type="text/javascript" src="resource/js/tool/jquery-1.11.3.min.js"></script>
<script type="text/javascript" src="resource/js/home.js"></script>
</html>
