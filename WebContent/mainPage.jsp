<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Main Page</title>
</head>
<body>
<br>
<form method="post" action="${ pageContext.request.contextPath}/VideoUpload"
encType="multipart/form-data">
<input type="hidden" name="type" value="topic">
<input type="file" name="file" value="select images..."/>
<br>
UserName:<input type="text" name="username">
<input type="submit" value="Create a topic"/>
</form>

</body>
</html>