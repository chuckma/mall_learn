<%--
  Created by IntelliJ IDEA.
  User: Andy
  Date: 2017/4/20
  Time: 22:49
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>mall商城测试页</title>
</head>
<body>
<h3>mall商城测试页</h3>

springmvc上传文件
<form action="/manage/product/upload.do" name="form1" method="post" enctype="multipart/form-data">
    <input type="file" name="upload_file"/>
    <input type="submit" value="springmvc上传文件">
</form>
富文本文件上传
<form action="/manage/product/richtext_img_upload.do" name="form1" method="post" enctype="multipart/form-data">
    <input type="file" name="upload_file"/>
    <input type="submit" value="富文本文件上传">
</form>
</body>
</html>
