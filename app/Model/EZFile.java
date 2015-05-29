package Model;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;

public class EZFile {
	private String id;
	private String name;
	private String localpath;
	private long size;
	private String createDate;
	private File body;
	private ArrayList<String> tags = new ArrayList<String>();
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getLocalpath() {
		return localpath;
	}
	public void setLocalpath(String localpath) {
		this.localpath = localpath;
	}
	public long getSize() {
		return size;
	}
	public void setSize(long size) {
		this.size = size;
	}
	public String getCreateDate() {
		return createDate;
	}
	public void setCreateDate(String createDate) {
		this.createDate = createDate;
	}
	public File getBody() {
		return body;
	}
	public void setBody(File body) {
		this.body = body;
	}
	
	public void addTag(String tag){
		tags.add(tag);
	}
	
	public Iterator<String> iterTags(){
		return tags.iterator();
	}
}
