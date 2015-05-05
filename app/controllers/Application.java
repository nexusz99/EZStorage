package controllers;

import play.*;
import play.mvc.*;

import views.html.*;

public class Application extends Controller {

    public static Result index() {
        return ok(index.render("Your new application is ready."));
    }
    
    public static Result login(){
    	String json2 = request().body().asJson().toString();
    	return ok(json2);
    }
}
