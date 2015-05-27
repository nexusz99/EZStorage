package controllers;

import java.io.File;

import Model.EZFile;

import com.typesafe.config.ConfigFactory;

public class LocalFileManager {
	
	public static String base = ConfigFactory.load().getString("basepath");
	
	public String store(EZFile f)
	{
		File body = f.getBody();
		File newpath = new File(base, f.getId());
		boolean r = body.renameTo(newpath);
		return newpath.getPath();
	}
}