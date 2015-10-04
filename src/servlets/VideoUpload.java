package servlets;


import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.tomcat.util.http.fileupload.FileItemIterator;
import org.apache.tomcat.util.http.fileupload.FileItemStream;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.apache.tomcat.util.http.fileupload.servlet.ServletFileUpload;
import org.apache.tomcat.util.http.fileupload.util.Streams;

import com.amazon.videobroadcast.Video;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.ClasspathPropertiesFileCredentialsProvider;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;

import static com.amazon.videobroadcast.AWSResourceSetup.*;

/**
 * Servlet implementation class VideoUpload
 */
public class VideoUpload extends HttpServlet {
	private static final long serialVersionUID = 1L;
//	public static final AWSCredentialsProvider CREDENTIALS_PROVIDER =
//            new ClasspathPropertiesFileCredentialsProvider();
//	public static AmazonS3 s3 = new AmazonS3Client(CREDENTIALS_PROVIDER);
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public VideoUpload() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		System.out.println("doPost");
		boolean ca = (request.getParameter("info")!=null);
		InputStream in = request.getInputStream();
		

		HashMap<String, Object> metadatas=getFileFromRequest(request);
		File video=(File)metadatas.get("Video");
		String tmpfileName=video.getName();
		String bucketName=S3_BUCKET_NAME;
		String type=(String) metadatas.get("type");
		String s3FileName=createS3FileName( type,tmpfileName);
		System.out.println("type: "+type+" s3FileName: "+s3FileName);
		// store video in s3
		S3.putObject(new PutObjectRequest(bucketName, s3FileName, video).withCannedAcl(CannedAccessControlList.PublicRead));
		
		String username=(String) metadatas.get("username");
		String topic=(String)metadatas.get("topic");
		String fileName=(String)metadatas.get("fileName");
		if(type.equals(MAIN_TOPIC)){
			// to avoid duplicate
			topic=tmpfileName;
		}
		Video v=new Video(topic,type,fileName, username, s3FileName);
		v.saveVideoToDynamoDB();
	}
	
	protected String createS3FileName(String type, String fileName){
		String S3FileName="";
		if(type.equals(MAIN_TOPIC)){
			S3FileName=MAIN_TOPIC_PATH+fileName;
		}else if(type.equals(FOLLOWING)){
			S3FileName=FOLLOWING_PATH+fileName;
		}
		
		return S3FileName;
	}
	
	protected HashMap<String,Object> getFileFromRequest(HttpServletRequest request) throws IOException{
		/*
		 metadatas includes:
		 1. type 
		 2. fileName
		 3. video
		 4. username
		 5. topic
		 6. videoName
		 */
		
		HashMap<String,Object> metadatas=new HashMap<String,Object>();
		if(!ServletFileUpload.isMultipartContent(request)){
		   System.out.println("Nothing to upload");
		   return null; 
		} 
		 
		ServletFileUpload upload = new ServletFileUpload();
		FileItemIterator iter;
		try {
			InputStream inputStream = null;
			iter = upload.getItemIterator(request);
			System.out.println("File name iterator:" + iter.hasNext());
			while (iter.hasNext()) {
				FileItemStream item = iter.next();
				inputStream = item.openStream();
				System.out.println("File name INput stream:"
						+ inputStream.available());
				if (item.isFormField()) {
					// hidden field for the video type
					String fieldName = item.getFieldName();
					String fildeVluae = Streams.asString(inputStream);
					System.out.println(fieldName + ":" + fildeVluae);
					metadatas.put(fieldName, fildeVluae);
					
				}else{
					//VIDEO
					String fileName=item.getName();
					metadatas.put("fileName",fileName);
					System.out.println("fileName:"+fileName);
					String fileNameToLowerCase = fileName.toLowerCase();
					// has the file extension
					String fileExtension = fileNameToLowerCase.substring(
							fileNameToLowerCase.indexOf("."),
							fileNameToLowerCase.length());
					System.out.println("fileextension:" + fileExtension);
					String videoName=fileNameToLowerCase.substring(0,
							fileNameToLowerCase.indexOf("."));
					metadatas.put("videoName",videoName);
					File tmpVideo = File.createTempFile(videoName,fileExtension);
					FileOutputStream outStream=new FileOutputStream(tmpVideo);
					IOUtils.copy(inputStream, outStream);
					System.out.println("Video Size:"+tmpVideo.getTotalSpace());
					metadatas.put("Video", tmpVideo);
					System.out.println("video name:"+tmpVideo.getName());
					
				}
	
			}
		} catch (FileUploadException e) {
			
			e.printStackTrace();
		}
		
		return metadatas;
	}


}
