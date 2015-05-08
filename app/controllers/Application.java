package controllers;

import java.util.Map;

import play.mvc.Controller;
import play.mvc.Http.MultipartFormData;
import play.mvc.Http.MultipartFormData.FilePart;
import play.mvc.Result;
import views.html.index;

public class Application extends Controller {

    public static Result index() {
        return ok(index.render("Your new application is ready."));
    }
    
    public static Result file()
    {
    	MultipartFormData body = request().body().asMultipartFormData();
    	FilePart file = body.getFile("uploadFile");
    	long fileSize = file.getFile().length();
    	String fileName = file.getFilename();
    	
    	String[] tags = body.asFormUrlEncoded().get("tags");
    	
    	return ok("filename : " + fileName + " / file size : " + fileSize + " / tags : " + tags);
    }
}
