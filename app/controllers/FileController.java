package controllers;

import Model.File;
import Model.User;

public class FileController {
	
	private LocalFileManager localfile = new LocalFileManager();
	private TagManager tagmanager = new TagManager();
	
	/**
	 * 새로운 파일을 저장한다.
	 * @param user 파일 저장을 요청한 유저 정보
	 * @param fileinfo 파일 정보가 저장된 객체
	 * @return
	 */
	public boolean saveNewfile(User user, File fileinfo)
	{
		// 1. 디비에서 이미 해당 사용자에 대해 동일한 파일 이름이 존재하는지 확인
		//  - 이미 존재하면 return false
		
		// 로컬 파일 시스템에 해당 파일을 저장한다.
		String localpath = localfile.store(fileinfo);
		
		// ezstorage_file table에 새로운 파일을 등록한다.

		// 태그 등록은 api 가 있는 코드에서 호출 해 줄 것이기 때문에 여기서는 필요 없다.
		return true;
	}
}
