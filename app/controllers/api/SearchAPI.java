package controllers.api;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Results;
import tools.ResultFileCompare;
import Model.ResultFile;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;

import controllers.SearchController;
import exception.CategoryException;

public class SearchAPI extends Controller {

	private static SearchController sc = new SearchController();
	
	public static Result searchByTag()
	{
		ArrayList<String> tags = new ArrayList<String>();
		JsonNode jn = request().body().asJson();
		
		int user_id = jn.get("user_id").asInt();
		ArrayNode an = (ArrayNode)jn.get("tags");
		for(JsonNode n : an)
		{
			tags.add(n.asText().trim());
		}
		
		Collection<ResultFile> re = sc.searchFile(user_id, tags, false);
		List<ResultFile> lre = new ArrayList<ResultFile>();
		for(ResultFile f: re)
		{
			lre.add(f);
		}
		Collections.sort(lre, new ResultFileCompare());
		return Results.ok(Json.toJson(lre));
	}
	
	
	public static Result searchByCategory()
	{
		JsonNode jn = request().body().asJson();
		int user_id = jn.get("user_id").asInt();
		int category_id = jn.get("category_id").asInt();
		
		Collection<ResultFile> re = null;
		
		try {
			re = sc.searchCategory(user_id, category_id);
		} catch (SQLException e) {
			return internalServerError("데이터베이스 에러");
		} catch (CategoryException e) {
			return notFound("카테고리를 찾을 수 없습니다.");
		}
		
		List<ResultFile> lre = new ArrayList<ResultFile>();
		for(ResultFile f: re)
		{
			lre.add(f);
		}
		Collections.sort(lre, new ResultFileCompare());
		return Results.ok(Json.toJson(lre));
	}
}
