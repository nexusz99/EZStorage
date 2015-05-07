package controllers;

import play.mvc.Controller;
import play.mvc.Http.Cookie;
import play.mvc.Result;
import views.html.index;

public class Application extends Controller {

    public static Result index() {
    	Cookie c= request().cookies().get("auth_key");
    	if(c==null)
    		return forbidden("접근이 금지되었습니다.");
    	
        return ok(index.render("Your new application is ready. " + c.value()));
    }
    
    public static Result login(){
    	response().setCookie("auth_key", request().body().asJson().toString());
    	return ok("asdf");
    }
}
