<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@page import="com.amazonaws.services.s3.AmazonS3Client" %>
<%@page import="com.amazonaws.regions.Region" %>
<%@page import="com.amazonaws.services.s3.model.S3ObjectSummary"%>
<%@page import="com.amazonaws.services.s3.iterable.S3Objects"%>
<%@page import="static servlets.VideoUpload.*"%>
<%@page import="static com.amazon.videobroadcast.AWSResourceSetup.*" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>SUB TOPIC</title>
</head>
<body>
Topic:<%=request.getParameter("topic") %>
<%
	int i=1;
	String topic=request.getParameter("topic");
    String bucket_name=S3_BUCKET_NAME;
    String prefix=MAIN_TOPIC_PATH+topic;
    S3Objects s3objects=S3Objects.withPrefix(S3,bucket_name,prefix);
    for (S3ObjectSummary summary : s3objects) {
    	System.out.println("Main topic:"+summary.getKey());
%>
        
        <div class="video-topic">
        <video id="example_video_1" class="video-js vjs-default-skin"
		    controls preload="auto" width="400" height="300"
		    data-setup='{"example_option":true}'>
		<!-- <source src="http://d12nufei91mcqm.cloudfront.net/<%=summary.getKey()%>" type='video/mp4' /> -->   
	    <source src="http://<%=bucket_name%>.s3.amazonaws.com/<%=summary.getKey()%>" type='video/mp4' /> 
		</video>
        </div>
        
  
<%
        i++;
    }
%>

<form method="post" action="${ pageContext.request.contextPath}/VideoUpload"
encType="multipart/form-data">
<input type="hidden" name="type" value="following">
<input type="hidden" name="topic" value=<%=topic%>>
<input type="file" name="file" value="select images..."/>
<br>
UserName:<input type="text" name="username">
<input type="submit" value="Reply to the topic"/>
</form>

<hr>
All replies:
<%
	int j=1;
    String prefixFollows=FOLLOWING_PATH+topic;
    for (S3ObjectSummary summary : S3Objects.withPrefix(S3,bucket_name,prefixFollows)) {
    	System.out.println("followings:"+summary.getKey());
%>
        
        <div class="video-topic">
        <video id="example_video_1" class="video-js vjs-default-skin"
		    controls preload="auto" width="400" height="300"
		    data-setup='{"example_option":true}'>
		<!-- <source src="http://d12nufei91mcqm.cloudfront.net/<%=summary.getKey()%>" type='video/mp4' /> -->   
	    <source src="http://<%=bucket_name%>.s3.amazonaws.com/<%=summary.getKey()%>" type='video/mp4' /> 
		</video>
        </div>
        
  
<%
        j++;
    }
%>

</body>
</html>