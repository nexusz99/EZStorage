package controllers.api;

import java.io.File;

import controllers.Session;
import play.Play;
import play.mvc.Controller;
import play.mvc.Http.Cookie;
import play.mvc.Result;
import play.mvc.Results;

public class Index extends Controller {
	public static Result index()
	{
		Cookie a  = request().cookie("auth_key");
		File file = Play.application().getFile("/public/index.html");
		if(a == null)
		{
			return Results.ok(file, true);
		}
		
		String session = a.value();
		String remoteAddr = request().remoteAddress();
		boolean validate = Session.isValideSession(session, remoteAddr);
		if(validate)
			file = Play.application().getFile("/public/drive.html");
		
		return Results.ok(file, true);
	}
}
