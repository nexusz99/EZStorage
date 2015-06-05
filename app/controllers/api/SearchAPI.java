package controllers.api;

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
		
		Collection<ResultFile> re = sc.searchFile(user_id, tags);
		List<ResultFile> lre = new ArrayList<ResultFile>();
		for(ResultFile f: re)
		{
			lre.add(f);
		}
		Collections.sort(lre, new ResultFileCompare());
		return Results.ok(Json.toJson(lre));
	}
	
}
