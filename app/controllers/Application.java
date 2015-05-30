package controllers;

import play.*;
import play.mvc.*;

import views.html.*;

public class Application extends Controller {

    public static Result index() {
        return ok(index.render("Your new application is ready."));
    }

    public static Result files()
    {
    	return ok("[{\"filename\": \"test.txt\"}, {\"filename: \"wow.jpg\"}, {\"filename\": \"1234.hwp\"}]");
    }
}
