package controllers.api;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Http.Cookie;
import play.mvc.Http.MultipartFormData;
import play.mvc.Http.MultipartFormData.FilePart;
import play.mvc.Result;
import play.mvc.Results;
import play.utils.UriEncoding;
import Model.EZFile;
import Model.User;

import com.fasterxml.jackson.databind.JsonNode;

import controllers.FileController;
import controllers.Session;
import exception.FileUploadException;

public class FileAPI extends Controller {
	
	private static FileController fc = new FileController();
	
	public static Result upload(int user_id)
	{
		if(!requestValidation(user_id))
			return forbidden("잘못된 접근입니다.");
		
		MultipartFormData body = request().body().asMultipartFormData();
		FilePart file = body.getFile("uploadedFile");
		String filename =  file.getFilename();
		String tags = body.asFormUrlEncoded().get("tags")[0];
		
		
		EZFile f = new EZFile();
		f.setBody(file.getFile());
		f.setName(filename);
		f.setUser_id(user_id);
		f.setSize(file.getFile().length());
		parseTagList(f, tags);
		
		User u = new User();
		u.setUserId(user_id);
		
		try
		{
			boolean result = fc.saveNewfile(u, f);
			if(!result)
				return status(CONFLICT, "[CONFLICT] File already uploaded");
		}
		catch(FileUploadException e)
		{
			return internalServerError(e.getMessage());
		}
		
		return redirect("/");
	}
	
	public static Result delete(int user_id, String file_id)
	{
		if(!requestValidation(user_id))
			return forbidden("잘못된 접근입니다.");
		
		boolean result = fc.deleteFile(user_id, file_id);
		if(!result)
			return badRequest("해당 파일이 존재하지 않습니다.");
		
		return redirect("/");
	}
	
	public static Result info(int user_id, String file_id)
	{
		return Results.ok(Json.toJson(fc.getFile(user_id, file_id, true)));
	}
	
	public static Result download(int user_id, String file_id) throws UnsupportedEncodingException
	{
		if(!requestValidation(user_id))
			return forbidden("잘못된 접근입니다.");
		
		EZFile f = fc.getFile(user_id, file_id, false);
		if(f == null)
			return notFound("파일을 찾을 수 없습니다.");
		
		String header = getBrowser();
		String fileName = f.getName();
		if (header.contains("MSIE")) {
		       String docName = URLEncoder.encode(fileName,"UTF-8").replaceAll("\\+", "%20");
		       response().setHeader("Content-Disposition", "attachment;filename=" + docName + ";");
		} else if (header.contains("Firefox")) {
		       String docName = new String(fileName.getBytes("UTF-8"), "ISO-8859-1");
		       response().setHeader("Content-Disposition", "attachment; filename=\"" + docName + "\"");
		} else if (header.contains("Opera")) {
		       String docName = new String(fileName.getBytes("UTF-8"), "ISO-8859-1");
		       response().setHeader("Content-Disposition", "attachment; filename=\"" + docName + "\"");
		} else if (header.contains("Chrome")) {
		       String docName = new String(fileName.getBytes("UTF-8"), "ISO-8859-1");
		       response().setHeader("Content-Disposition", "attachment; filename=\"" + docName + "\"");
		}
		
		return Results.ok(f.getBody());
	}
	
	
	public static Result recentList(int user_id, int marker, int limit)
	{
		if(!requestValidation(user_id))
			return forbidden("잘못된 접근입니다.");
		ArrayList<EZFile> list = fc.getFileList(user_id, marker, limit);
		if(list == null)
			return badRequest("요청이 잘못되었습니다.");
		JsonNode jn = Json.toJson(list);
		return Results.ok(Json.toJson(list));
	}
	
	private static String getBrowser() {
        String header =request().getHeader("User-Agent");
        if (header.contains("MSIE")) {
               return "MSIE";
        } else if(header.contains("Chrome")) {
               return "Chrome";
        } else if(header.contains("Opera")) {
               return "Opera";
        }
        return "Firefox";
  }
	
	private static void parseTagList(EZFile f, String tags)
	{
		String[] sp= tags.split(",");
		
		int splen = sp.length;
		
		for(int i = 0 ; i < splen; i++)
		{
			f.addTag(sp[i].trim());
		}
	}
	
	private static boolean requestValidation(int user_id)
	{
		Cookie s = request().cookie("userid");
		
		if(s == null)
			return false;
		
		int uid_cookie = Integer.parseInt(s.value());
		if(uid_cookie != user_id)
			return false;
		
		s = request().cookie("auth_key");
		if(s == null)
			return false;
		
		String session = s.value();
		String remoteAddr = request().remoteAddress();
		if(!Session.isValideSession(session, remoteAddr))
			return false;
		
		return true;
	}
}
