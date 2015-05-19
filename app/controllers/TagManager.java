package controllers;

import java.util.Iterator;

import Model.File;

public class TagManager {
	
	public void saveFileTag(File fileinfo)
	{
		Iterator<String> tags = fileinfo.iterTags();
		
		// 태그를 iterator 하면서 eztags 테이블에 태그를 등록
		
		// 그리고 eztags_has_storage_file 테이블에 m:n 관계 등록
	}
}
