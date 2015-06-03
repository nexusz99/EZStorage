package controllers.api;

import java.sql.SQLException;
import java.util.ArrayList;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

import controllers.CategoryController;
import controllers.Session;
import exception.CategoryException;
import Model.Category;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Results;
import play.mvc.Http.Cookie;

public class CategoryAPI extends Controller {
	
	private static CategoryController cc = new CategoryController();
	
	public static Result create(int user_id)
	{
		if(!requestValidation(user_id))
			return forbidden("잘못된 접근입니다.");
		JsonNode jn = request().body().asJson();
		
		Category c = new Category();
		
		String name = jn.get("name").asText();
		c.setName(name);
		ArrayNode an = (ArrayNode)jn.get("tags");
		for(JsonNode n : an)
		{
			c.addTag(n.asText());
		}
		
		boolean result;
		try {
			result = cc.createNewCategory(user_id, c);
			if(!result)
				return badRequest("잘못된 요청입니다.");
		} catch (CategoryException e) {
			return  status(CONFLICT, "이미 존재하는 카테고리입니다.");
		}
		
		return Results.ok();
	}
	
	public static Result lists(int user_id)
	{
		if(!requestValidation(user_id))
			return forbidden("잘못된 접근입니다.");
		ArrayList<Category> list = cc.getCategoryList(user_id);
		JsonNode jn = Json.toJson(list);
		return Results.ok(Json.toJson(list));
	}
	
	public static Result get(int user_id, int category_id) throws JsonProcessingException
	{
		Category c = null;
		try {
			c = cc.getCategory(user_id, category_id);
		} catch (CategoryException e) {
			return notFound(e.getMessage());
		} catch (SQLException e) {
			return internalServerError(e.getMessage());
		}
		return Results.ok(new ObjectMapper().writeValueAsString(c));
	}
	
	public static Result delete(int user_id, int category_id)
	{
		return Results.ok();
	}
	
	public static Result update(int user_id, int category_id)
	{
		return Results.ok();
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
