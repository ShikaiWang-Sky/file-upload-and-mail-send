<%--
  Created by IntelliJ IDEA.
  User: Wang
  Date: 2020/8/25
  Time: 16:33
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
  <head>
    <title>$Title$</title>
  </head>
  <body>

  <%--通过表单上传文件
    get: 上传文件大小有限制
    post: 上传文件大小无限制
    文件上传必须设定enctype="multipart/form-data
  --%>
  <%--${pageContext.request.contextPath}  获取服务器路径--%>
  <form action="${pageContext.request.contextPath}/upload.do" method="post" enctype="multipart/form-data">
    上传用户: <input type="text" name="username"><br/>
    <p><input type="file" name="file1"></p>
    <p><input type="file" name="file2"></p>
    <p><input type="submit" value="提交">  |   <input type="reset" value="重置"></p>

  </form>



  </body>
</html>
