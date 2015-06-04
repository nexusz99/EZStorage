package Model;

import java.util.ArrayList;
import java.util.Iterator;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Category {
	private int id;
	private String name;
	private String created;
	
	@JsonProperty("tags")
	private ArrayList<String> tags = new ArrayList<String>();
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getCreated() {
		return created;
	}
	public void setCreated(String created) {
		this.created = created;
	}
	public void addTag(String tag){
		tags.add(tag);
	}
	
	public Iterator<String> iterTags(){
		return tags.iterator();
	}
	
	public void addTag(ArrayList<String> list)
	{
		for(String t : list)
		{
			addTag(t);
		}
	}
}
