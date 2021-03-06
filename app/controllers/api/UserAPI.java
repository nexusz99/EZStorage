package controllers.api;

import java.sql.SQLException;

import Model.User;

import com.fasterxml.jackson.databind.JsonNode;

import controllers.Session;
import controllers.UserController;
import exception.PasswordNotCorrectException;
import exception.UserNotExistedException;
import play.*;
import play.mvc.*;
import play.mvc.Http.Cookie;
import play.mvc.Http.Request;
import play.mvc.Http.Response;
import play.utils.UriEncoding;

public class UserAPI extends Controller {
	
	private static UserController uc = new UserController();
	
	public static Result signup()
	{
		JsonNode json = request().body().asJson();		
		User u = new User();
		u.setUsername(json.get("username").asText());
		u.setPasswd(json.get("passwd").asText());
		u.setFirstname(json.get("firstname").asText());
		u.setLastname(json.get("lastname").asText());
		
		boolean result=false;
		try {
			result = uc.signup(u);
		} catch (SQLException e) {
			return status(CONFLICT);
		}
		
		if(!result)
			return internalServerError();
		
		return ok();
	}
	
	public static Result signinorout()
	{
		JsonNode json = request().body().asJson();
		int type = json.get("type").asInt();
		if(type == 1)
		{
			return new UserAPI().signin();
		}
		else if(type == 2)
		{
			return new UserAPI().signout();
		}
		else
		{
			return badRequest("Incorrect Type value");
		}
	}
	
	public Result signin()
	{
		JsonNode json = request().body().asJson();
		String username = json.get("username").asText();
		String passwd = json.get("passwd").asText();
		String remoteAddr = request().remoteAddress();
		
		User u = null;
		try {
			u = uc.signin(username, passwd, remoteAddr);
		} catch (PasswordNotCorrectException | UserNotExistedException e) {
			return forbidden("login fail");
		}
		
		Response response = response();
		response.setCookie("userid",String.valueOf(u.getUserId()));
		response.setCookie("firstname", UriEncoding.encodePathSegment(u.getFirstname(), "utf-8"));
		response.setCookie("lastname",  UriEncoding.encodePathSegment(u.getLastname(), "utf-8"));
		response.setCookie("auth_key", u.getSession());
		return ok();
	}
	
	public Result signout()
	{
		Cookie a  = request().cookie("auth_key");
		if(a==null)
		{
			return forbidden();
		}
		
		String session = a.value();
		String remoteAddr = request().remoteAddress();
		
		if(!Session.isValideSession(session, remoteAddr))
		{
			return forbidden();
		}
		
		Session.expireSession(session);
		
		return redirect("/");
	}
}
