package controllers.api;

import java.io.File;

import controllers.LocalFileManager;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Results;

public class ThumbNailAPI extends Controller {

	public static Result getThumb(String file)
	{
		LocalFileManager lfm = new LocalFileManager();
		File f = lfm.getThumbNail(file);
		if(!f.exists())
		{
			return notFound("파일없음");
		}
		return Results.ok(f);
	}
}
