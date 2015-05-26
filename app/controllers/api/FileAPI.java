package controllers.api;

import java.util.ArrayList;
import java.util.Iterator;

import controllers.FileController;
import controllers.Session;
import controllers.TagManager;
import Model.EZFile;
import Model.User;
import play.mvc.Controller;
import play.mvc.Http.Cookie;
import play.mvc.Http.MultipartFormData;
import play.mvc.Http.MultipartFormData.FilePart;
import play.mvc.Result;
import play.mvc.Results;

public class FileAPI extends Controller {
	
	private static FileController fc = new FileController();
	private static TagManager tm = new TagManager();
	
	public static Result upload(int user_id)
	{
		// TODO 전체 과정을 Transaction으로 처리하기
		if(!requestValidation(user_id))
			return forbidden();
		
		MultipartFormData body = request().body().asMultipartFormData();
		FilePart file = body.getFile("uploadedFile");
		String filename =  file.getFilename();
		String tags = body.asFormUrlEncoded().get("tags")[0];
		
		
		EZFile f = new EZFile();
		f.setBody(file.getFile());
		f.setName(filename);
		f.setSize(file.getFile().length());
		parseTagList(f, tags);
		
		User u = new User();
		u.setUserId(user_id);
		
		boolean result = fc.saveNewfile(u, f);
		if(!result)
			return status(CONFLICT, "[CONFLICT] File already uploaded");
		
		tm.saveFileTag(f);
		
		return redirect("/");
	}
	
	public static Result download(int user_id, String file_id)
	{
		return Results.ok(file_id);
	}
	
	public static Result recentList(int user_id)
	{
		return Results.ok("Recent List User id : " + user_id);
	}
	
	public static void parseTagList(EZFile f, String tags)
	{
		String[] sp= tags.split(",");
		
		int splen = sp.length;
		
		for(int i = 0 ; i < splen; i++)
		{
			f.addTag(sp[i]);
		}
	}
	
	public static boolean requestValidation(int user_id)
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
