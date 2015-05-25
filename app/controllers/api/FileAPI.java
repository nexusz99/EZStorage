package controllers.api;

import play.mvc.Controller;
import play.mvc.Http.MultipartFormData;
import play.mvc.Http.MultipartFormData.FilePart;
import play.mvc.Result;
import play.mvc.Results;

public class FileAPI extends Controller {
	public static Result upload(int user_id)
	{
		MultipartFormData body = request().body().asMultipartFormData();
		FilePart file = body.getFile("uploadedFile");
		
		String filename =  file.getFilename();
		String[] tags = body.asFormUrlEncoded().get("tags");
		System.out.println(filename);
		System.out.println(tags[0]);
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
}
